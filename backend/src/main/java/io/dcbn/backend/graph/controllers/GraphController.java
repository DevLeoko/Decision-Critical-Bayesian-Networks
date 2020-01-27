package io.dcbn.backend.graph.controllers;

import io.dcbn.backend.graph.Graph;
import io.dcbn.backend.graph.repositories.GraphRepository;
import io.dcbn.backend.graph.services.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@PreAuthorize("hasRole('MODERATOR')")
public class GraphController {

    private final GraphRepository repository;
    private final GraphService graphService;

    @Autowired
    public GraphController(GraphRepository repository, GraphService graphService) {
        this.repository = repository;
        this.graphService = graphService;
    }

    @GetMapping("/graphs")
    public Iterable<Graph> getGraphs(@RequestParam boolean withStructure) {
        return StreamSupport.stream(repository.findAll().spliterator(), false).peek(graph -> {
            if (!withStructure) {
                graph.setNodes(Collections.emptyList());
            }
        }).collect(Collectors.toList());
    }

    @GetMapping("/graphs/{id}")
    public Graph getGraphById(@PathVariable long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/graphs")
    public void createGraph(@Valid @RequestBody Graph graph, Principal principal) {
        try {
            graphService.updateLock(graph.getId(), principal.getName());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        if (graphService.hasCycles(graph)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Graph has cycles!");
        }

        repository.save(graph);
    }

    @DeleteMapping("/graphs/{id}")
    public void deleteById(@PathVariable long id, Principal principal) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        try {
            graphService.updateLock(id, principal.getName());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        repository.deleteById(id);
    }

    @PutMapping("/graphs/{id}")
    public void updateGraphById(@PathVariable long id, @Valid @RequestBody Graph graph, Principal principal) {
        try {
            graphService.updateLock(graph.getId(), principal.getName());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        if (graphService.hasCycles(graph)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Graph has cycles!");
        }

        Graph oldGraph = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        oldGraph.setNodes(graph.getNodes());
        oldGraph.setName(graph.getName());
        oldGraph.setTimeSlices(graph.getTimeSlices());
        repository.save(oldGraph);
    }

    @PostMapping("/graphs/evaluate")
    public Graph evaluateGraphById(@Valid @RequestBody Graph graph) {
        return graphService.evaluateGraph(graph);
    }

    @PutMapping("/graphs/{id}/lock")
    public void updateGraphLockById(@PathVariable long id, Principal principal) {
        try {
            graphService.updateLock(id, principal.getName());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
