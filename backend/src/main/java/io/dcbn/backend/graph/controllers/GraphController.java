package io.dcbn.backend.graph.controllers;

import io.dcbn.backend.graph.Graph;
import io.dcbn.backend.graph.Node;
import io.dcbn.backend.graph.PotentiallyLockedGraph;
import io.dcbn.backend.graph.ValueNode;
import io.dcbn.backend.graph.converters.GenieConverter;
import io.dcbn.backend.graph.repositories.GraphRepository;
import io.dcbn.backend.graph.services.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.xml.sax.SAXException;

import javax.validation.Valid;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
public class GraphController {

    private final GraphRepository repository;
    private final GraphService graphService;
    private static final String GRAPH_NOT_FOUND = "Graph not found";

    private final Map<String, DeferredResult<Iterable<PotentiallyLockedGraph>>> resultMap = new ConcurrentHashMap<>();

    @Scheduled(fixedRate = 3000)
    public void updateLocks() {
        if (graphService.updateExpiredLocks()) {
            updateResults();
        }
    }

    @Autowired
    public GraphController(GraphRepository repository, GraphService graphService) {
        this.repository = repository;
        this.graphService = graphService;
    }

    private Iterable<PotentiallyLockedGraph> getPotentiallyLockedGraphs(String username) {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .peek(graph -> graph.setNodes(Collections.emptyList()))
                .map(graph -> new PotentiallyLockedGraph(graph, graphService.cannotAccess(graph.getId(), username)))
                .collect(Collectors.toList());
    }

    private void updateResults() {
        for (Map.Entry<String, DeferredResult<Iterable<PotentiallyLockedGraph>>> result : resultMap.entrySet()) {
            result.getValue().setResult(getPotentiallyLockedGraphs(result.getKey()));
        }
        resultMap.clear();
    }

    @GetMapping("/graphs")
    @PreAuthorize("hasRole('MODERATOR')")
    public DeferredResult<Iterable<PotentiallyLockedGraph>> getGraphs(@RequestParam(required = false, defaultValue = "false") boolean delayed, Principal principal) {
        DeferredResult<Iterable<PotentiallyLockedGraph>> result =
                new DeferredResult<>(60000L, getPotentiallyLockedGraphs(principal.getName()));

        if (!delayed) {
            result.setResult(getPotentiallyLockedGraphs(principal.getName()));
        } else {
            DeferredResult<Iterable<PotentiallyLockedGraph>> old = resultMap.put(principal.getName(), result);
            if(old != null){
                old.setResult(getPotentiallyLockedGraphs(principal.getName()));
            }
        }
        return result;
    }

    @GetMapping("/graphs/{id}")
    @PreAuthorize("hasRole('MODERATOR')")
    public Graph getGraphById(@PathVariable long id, Principal principal) {
        if (graphService.cannotAccess(id, principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/graphs")
    @PreAuthorize("hasRole('ADMIN')")
    public long createGraph(@Valid @RequestBody Graph graph) {
        if (graphService.hasCycles(graph)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Graph has cycles!");
        }

        if (graphService.notAllEvidenceFormulasExist(graph)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some evidence formulas do not exist!");
        }

        long id = repository.save(graph).getId();
        updateResults();
        return id;
    }

    @DeleteMapping("/graphs/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(@PathVariable long id, Principal principal) {
        if (graphService.cannotAccess(id, principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, GRAPH_NOT_FOUND);
        }

        repository.deleteById(id);
        updateResults();
    }

    @PutMapping("/graphs/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void updateGraphById(@PathVariable long id, @Valid @RequestBody Graph graph, Principal principal) {
        if (graphService.cannotAccess(id, principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (graphService.hasCycles(graph)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Graph has cycles!");
        }

        if (graphService.notAllEvidenceFormulasExist(graph)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some evidence formulas do not exist!");
        }

        Graph oldGraph = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        oldGraph.getNodes().clear();
        oldGraph.getNodes().addAll(graph.getNodes());
        oldGraph.setName(graph.getName());
        oldGraph.setTimeSlices(graph.getTimeSlices());

        repository.save(oldGraph);
    }

    @PostMapping("/graphs/{id}/name")
    @PreAuthorize("hasRole('ADMIN')")
    public void renameGraphById(@PathVariable long id, @RequestBody String name) {
        Graph graph = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, GRAPH_NOT_FOUND));
        //Checking that the graph name is unique
        for (Graph graphSaved : repository.findAll()) {
            if (graphSaved.getName().equals(name)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A graph with the same name ("
                        + name + ") already exists");
            }
        }

        graph.setName(name);
        repository.save(graph);
        updateResults();
    }

    @PostMapping("/graphs/{id}/evaluate")
    @PreAuthorize("hasRole('MODERATOR')")
    public Map<String, double[][]> evaluateGraphById(@PathVariable long id, @RequestBody Map<String, double[][]> values) {
        Graph graph = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Graph does not exist!"));
        Map<String, Node> nodeMap = graph.getNodes().stream().collect(Collectors.toMap(Node::getName, node -> node));

        // Replace all nodes that have values with ValueNodes
        for (Map.Entry<String, double[][]> valueEntry : values.entrySet()) {
            Node node = nodeMap.get(valueEntry.getKey());
            if (node == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid node name in map!");
            }
            nodeMap.put(valueEntry.getKey(), new ValueNode(node, valueEntry.getValue()));
        }

        // nodeMap now contains all graph nodes with the correct nodes replaced by ValueNodes

        // Change all references to old Node objects to ValueNodes.
        for (Map.Entry<String, Node> nodeEntry : nodeMap.entrySet()) {
            Node node = nodeEntry.getValue();

            List<List<Node>> parents = Arrays.asList(node.getTimeZeroDependency().getParentsTm1(),
                    node.getTimeZeroDependency().getParents(),
                    node.getTimeTDependency().getParentsTm1(),
                    node.getTimeTDependency().getParents());
            for (List<Node> currentParents : parents) {
                for (int i = 0; i < currentParents.size(); ++i) {
                    String parentName = currentParents.get(i).getName();
                    if (values.containsKey(parentName)) {
                        currentParents.set(i, nodeMap.get(parentName));
                    }
                }
            }
        }

        graph.setNodes(new ArrayList<>(nodeMap.values()));
        Graph resultGraph = graphService.evaluateGraph(graph);
        return resultGraph.getNodes().stream().filter(Node::isValueNode)
                .map(node -> (ValueNode) node).collect(Collectors.toMap(Node::getName, ValueNode::getValue));
    }

    @PutMapping("/graphs/{id}/lock")
    @PreAuthorize("hasRole('MODERATOR')")
    public void updateGraphLockById(@PathVariable long id, Principal principal) {
        try {
            if (graphService.updateLock(id, principal.getName())) {
                updateResults();
            };
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    @PostMapping("/graphs/import")
    @PreAuthorize("hasRole('ADMIN')")
    public Graph importGraphFromGenie(@RequestParam("graph") MultipartFile uploadedFile) {
        if (uploadedFile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No file uploaded");
        }
        GenieConverter genieConverter = new GenieConverter();
        Graph graph;
        try {
            graph = genieConverter.fromGenieToDcbn(uploadedFile.getInputStream());
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File conversion failed");
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        //Checking that the graph name is unique
        for (Graph graphSaved : repository.findAll()) {
            if (graphSaved.getName().equals(graph.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A graph with the same name ("
                        + graph.getName() + ") already exists");
            }
        }
        repository.save(graph);
        graph = repository.findById(graph.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Save Failed"));

        updateResults();
        return graph;
    }

    @GetMapping("/graphs/{id}/export")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<String> exportGraph(@PathVariable long id) {
        Graph graph = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, GRAPH_NOT_FOUND));
        GenieConverter genieConverter = new GenieConverter();
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_XML)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + graph.getName() + ".xdsl\"")
                    .body(genieConverter.fromDcbnToGenie(graph));
        } catch (TransformerException | ParserConfigurationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File conversion failed");
        }
    }
}
