package io.dcbn.backend.graph.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dcbn.backend.graph.AmidstGraphAdapter;
import io.dcbn.backend.graph.Graph;
import io.dcbn.backend.graph.repositories.GraphRepository;
import io.dcbn.backend.graph.services.GraphService;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@PreAuthorize("hasRole('MODERATOR')")
public class GraphController {

  private final GraphRepository repository;
  private final GraphService service;

  @Autowired
  public GraphController(GraphRepository repository,
      GraphService service) {
    this.repository = repository;
    this.service = service;
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
  public void createGraph(@Valid @RequestBody Graph graph) {
    // throw exception if graph has cycles
    AmidstGraphAdapter graphAdapter = new AmidstGraphAdapter(graph);
    if(graphAdapter.getDbn().getDynamicDAG().containCycles()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    repository.save(graph);
  }

  @DeleteMapping("/graphs/{id}")
  public void deleteById(@PathVariable long id) {
    if (!repository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    repository.deleteById(id);
  }

  @PutMapping("/graphs/{id}")
  public void updateGraphById(@PathVariable long id, @Valid @RequestBody Graph graph) {
    // throw exception if graph has cycles
    AmidstGraphAdapter graphAdapter = new AmidstGraphAdapter(graph);
    if(graphAdapter.getDbn().getDynamicDAG().containCycles()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    Graph oldGraph = repository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    oldGraph.setNodes(graph.getNodes());
    oldGraph.setName(graph.getName());
    oldGraph.setTimeSlices(graph.getTimeSlices());
    repository.save(oldGraph);
  }

  @PostMapping("/graphs/{id}/evaluate")
  public Graph evaluateGraphById(@PathVariable long id, HttpServletRequest request) {
    Graph graph = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    try {
      Map<String, double[][]> values = new ObjectMapper().readValue(request.getReader(),
          new TypeReference<Map<String, double[][]>>(){});

      return service.evaluateGraph(graph, values);
    } catch (IOException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
  }
}
