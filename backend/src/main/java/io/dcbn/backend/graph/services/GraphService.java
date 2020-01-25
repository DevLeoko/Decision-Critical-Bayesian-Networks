package io.dcbn.backend.graph.services;

import io.dcbn.backend.authentication.repositories.DcbnUserRepository;
import io.dcbn.backend.graph.AmidstGraphAdapter;
import io.dcbn.backend.graph.Graph;
import io.dcbn.backend.inference.Algorithm;
import io.dcbn.backend.inference.InferenceManager;
import org.springframework.stereotype.Service;
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

    private Map<Long, GraphLock> lock;
    private final DcbnUserRepository dcbnUserRepository;
    private InferenceManager manager;

    public GraphService(DcbnUserRepository dcbnUserRepository, InferenceManager manager) {
        this.dcbnUserRepository = dcbnUserRepository;
        this.manager = manager;
        this.lock = new HashMap<>();
    }

    //checks if Graph has cycles
    public boolean hasCycles(Graph graph) {
        AmidstGraphAdapter graphAdapter = new AmidstGraphAdapter(graph);
        return graphAdapter.getDbn().getDynamicDAG().containCycles();
    }

    public void updateLock(long graphId, String userName) throws IllegalArgumentException {
        long userId = dcbnUserRepository.findByUsername(userName)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist!")).getId();

        if (!lock.containsKey(graphId)) {
            lock.put(graphId, new GraphLock(userId));
        } else if (lock.get(graphId).getUserId() == userId) {
            lock.put(graphId, new GraphLock(userId));
        } else if (lock.get(graphId).getUserId() != userId && lock.get(graphId).isExpired()) {
            lock.put(graphId, new GraphLock(userId));
        } else {
            throw new IllegalArgumentException("Graph already locked by another user!");
        }
    }

    public Graph evaluateGraph(Graph graph) {
        AmidstGraphAdapter adaptedGraph = new AmidstGraphAdapter(graph);
        return manager
                .calculateInference(adaptedGraph, (i, formula) -> "false", Algorithm.IMPORTANCE_SAMPLING);
    }
}
