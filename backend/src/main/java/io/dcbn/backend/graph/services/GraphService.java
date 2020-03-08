package io.dcbn.backend.graph.services;

import io.dcbn.backend.authentication.repositories.DcbnUserRepository;
import io.dcbn.backend.evidence_formula.repository.EvidenceFormulaRepository;
import io.dcbn.backend.graph.AmidstGraphAdapter;
import io.dcbn.backend.graph.Graph;
import io.dcbn.backend.graph.Node;
import io.dcbn.backend.inference.InferenceManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GraphService {

    @Value("${graph.lock.expire.time}")
    private long graphLockExpireTime;

    private Map<Long, GraphLock> lock;
    private final DcbnUserRepository dcbnUserRepository;
    private final EvidenceFormulaRepository evidenceFormulaRepository;
    private InferenceManager manager;

    public GraphService(DcbnUserRepository dcbnUserRepository, EvidenceFormulaRepository evidenceFormulaRepository, InferenceManager manager) {
        this.dcbnUserRepository = dcbnUserRepository;
        this.evidenceFormulaRepository = evidenceFormulaRepository;
        this.manager = manager;
        this.lock = new HashMap<>();
    }

    public void setGraphLockExpireTime(long graphLockExpireTime) {
        this.graphLockExpireTime = graphLockExpireTime;
    }

    public boolean cannotAccess(long graphId, String username) {
        long userId = dcbnUserRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(("User does not exist!"))).getId();

        GraphLock l = lock.get(graphId);
        return l != null && !l.isExpired() && l.getUserId() != userId;
    }

    //checks if Graph has cycles
    public boolean hasCycles(Graph graph) {
        AmidstGraphAdapter graphAdapter = new AmidstGraphAdapter(graph);
        return !graph.getNodes().isEmpty() && graphAdapter.getDbn().getDynamicDAG().containCycles();
    }

    public boolean updateLock(long graphId, String userName) {
        long userId = dcbnUserRepository.findByUsername(userName)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist!")).getId();

        if (!lock.containsKey(graphId)) {
            lock.values().removeIf(l -> l.getUserId() == userId);
            lock.put(graphId, new GraphLock(userId, graphLockExpireTime));
            return true;
        } else if (lock.get(graphId).getUserId() == userId) {
            lock.put(graphId, new GraphLock(userId, graphLockExpireTime));
            return false;
        } else if (lock.get(graphId).getUserId() != userId && lock.get(graphId).isExpired()) {
            lock.values().removeIf(l -> l.getUserId() == userId);
            lock.put(graphId, new GraphLock(userId, graphLockExpireTime));
            return true;
        } else {
            throw new IllegalArgumentException("Graph already locked by another user!");
        }
    }

    public boolean updateExpiredLocks() {
        return lock.entrySet().removeIf(e -> e.getValue().isExpired());
    }

    public Graph evaluateGraph(Graph graph) {
        return manager
                .calculateInference(graph, "").getCorrelatedNetwork();
    }

    public boolean notAllEvidenceFormulasExist(Graph graph) {
        List<Node> nodes = graph.getNodes();
        for (Node node : nodes) {
            if (node.getEvidenceFormulaName() != null && !evidenceFormulaRepository.existsByName(node.getEvidenceFormulaName())) {
                return true;
            }
        }
        return false;
    }
}
