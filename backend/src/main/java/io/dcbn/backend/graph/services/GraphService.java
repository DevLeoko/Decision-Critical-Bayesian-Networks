package io.dcbn.backend.graph.services;

import io.dcbn.backend.authentication.repositories.DcbnUserRepository;
import io.dcbn.backend.evidenceFormula.repository.EvidenceFormulaRepository;
import io.dcbn.backend.evidenceFormula.services.EvidenceFormulaEvaluator;
import io.dcbn.backend.graph.AmidstGraphAdapter;
import io.dcbn.backend.graph.Graph;
import io.dcbn.backend.graph.Node;
import io.dcbn.backend.inference.Algorithm;
import io.dcbn.backend.inference.InferenceManager;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GraphService {

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

    //checks if Graph has cycles
    public boolean hasCycles(Graph graph) {
        AmidstGraphAdapter graphAdapter = new AmidstGraphAdapter(graph);
        return graph.getNodes().size() != 0 && graphAdapter.getDbn().getDynamicDAG().containCycles();
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
