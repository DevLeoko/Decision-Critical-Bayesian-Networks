package io.dcbn.backend.graph.services;

import io.dcbn.backend.graph.AmidstGraphAdapter;
import io.dcbn.backend.graph.Graph;
import io.dcbn.backend.graph.Node;
import io.dcbn.backend.graph.ValueNode;
import io.dcbn.backend.inference.Algorithm;
import io.dcbn.backend.inference.InferenceManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class GraphService {

  @Getter
  private Map<Long, GraphLock> lock;

  private final InferenceManager manager;

  @Autowired
  public GraphService(InferenceManager manager) {
    this.manager = manager;
    this.lock = new HashMap<>();
  }

  //checks if Graph is a DAG //TODO: Implement CycleSearch
  public boolean validate(Graph graph) {
    return false;
  }

  //locks Graph if possible
  public void lockGraph(long graphId, long userId) {
    if (lock.containsKey(graphId)) {
      throw new IllegalArgumentException("Graph is being edited by another User");
    } else {
      //check and remove all existing locks from thee user
      for (Map.Entry<Long, GraphLock> entry : lock.entrySet()) {
        if (entry.getValue().getUserId() == userId) {
          lock.remove(entry.getKey());
        }
      }
      lock.put(graphId, new GraphLock(userId));
    }
  }

  //unlocks the locked Graph
  public void unlockGraph(long graphId, long userId) {
    if (lock.containsKey(graphId)) {
      lock.remove(graphId);
    } else {
      throw new IllegalArgumentException("Graph not found");
    }
  }

  //checks if any locks have timed out and removes such locks
  @Scheduled(fixedRateString = "$graphlock.refresh.time")
  public void refreshLocks() {
    for (Map.Entry<Long, GraphLock> entry : lock.entrySet()) {
      if (entry.getValue().getExpireTime() < System.currentTimeMillis()) {
        lock.remove(entry.getKey());
      }
    }
  }

  public Graph evaluateGraph(Graph graph, Map<String, double[][]> values) {
    List<Node> newNodes = graph.getNodes().stream().map(node -> {
      if (values.containsKey(node.getName())) {
        double[][] valuesArray = values.get(node.getName());
        return new ValueNode(node, valuesArray);
      } else {
        return node;
      }
    }).collect(Collectors.toList());
    graph.setNodes(newNodes);

    AmidstGraphAdapter adaptedGraph = new AmidstGraphAdapter(graph);

    return manager
        .calculateInference(adaptedGraph, (i, formula) -> "false", Algorithm.IMPORTANCE_SAMPLING);
  }
}
