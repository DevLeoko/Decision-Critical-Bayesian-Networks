package io.dcbn.backend.graph.services;

import io.dcbn.backend.authentication.repositories.DcbnUserRepository;
import io.dcbn.backend.graph.AmidstGraphAdapter;
import io.dcbn.backend.graph.Graph;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GraphService {

    private Map<Long, GraphLock> lock;
    private final DcbnUserRepository dcbnUserRepository;

    @Autowired
    public GraphService(DcbnUserRepository dcbnUserRepository) {
        this.dcbnUserRepository = dcbnUserRepository;
        this.lock = new HashMap<>();
    }

    //checks if Graph has cycles
    public boolean hasCycles(Graph graph) {
        AmidstGraphAdapter graphAdapter = new AmidstGraphAdapter(graph);
        return graphAdapter.getDbn().getDynamicDAG().containCycles();
    }

    public void updateLock(long graphId, String userName) throws IllegalArgumentException {
        if(!dcbnUserRepository.existsByUsernameOrEmail(userName, null)) {
            throw new IllegalArgumentException("Username does not exist!");
        }
        long userId = dcbnUserRepository.findByUsernameOrEmail(userName, null).get().getId();

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
}
