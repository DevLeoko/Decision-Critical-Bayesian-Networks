package io.dcbn.backend.inferenceEngine;


import com.google.common.collect.Lists;
import de.fraunhofer.iosb.iad.maritime.datamodel.AreaOfInterest;
import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import io.dcbn.backend.core.VesselCache;
import io.dcbn.backend.datamodel.Outcome;
import io.dcbn.backend.evidenceFormula.model.EvidenceFormula;
import io.dcbn.backend.evidenceFormula.repository.EvidenceFormulaRepository;
import io.dcbn.backend.evidenceFormula.services.EvidenceFormulaEvaluator;
import io.dcbn.backend.graph.*;
import io.dcbn.backend.graph.repositories.GraphRepository;
import io.dcbn.backend.inference.Algorithm;
import io.dcbn.backend.inference.InferenceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InferenceManagerTest {

    private static final int NUM_TIME_SLICES = 5;
    private static final Position ZERO_POSITION = new Position(0, 0);
    private static final AreaOfInterest AREA = new AreaOfInterest("TEST_AREA", null);

    private InferenceManager inferenceManager;
    private Graph graph;
    private Node smuggling;
    private Node nullSpeed;
    private Node inTrajectoryArea;
    private Node isInReportedArea;

    @BeforeEach
    public void setUp() {
         smuggling = new Node("smuggling", null, null, "", null, StateType.BOOLEAN,
                ZERO_POSITION);
         nullSpeed = new Node("nullSpeed", null, null, "",
                "nullSpeed", StateType.BOOLEAN, ZERO_POSITION);
         inTrajectoryArea = new Node("inTrajectoryArea", null, null, "",
                "inTrajectory", StateType.BOOLEAN, ZERO_POSITION);
         isInReportedArea = new Node("isInReportedArea", null, null, "",
                "inArea", StateType.BOOLEAN, ZERO_POSITION);

        List<Node> smugglingParentsList = Lists.reverse(Arrays.asList(isInReportedArea, inTrajectoryArea, nullSpeed));
        double[][] probabilities = {{0.8, 0.2}, {0.6, 0.4}, {0.4, 0.6}, {0.4, 0.6}, {0.2, 0.8},
                {0.2, 0.8}, {0.001, 0.999}, {0.001, 0.999}};
        NodeDependency smuggling0Dep = new NodeDependency(smugglingParentsList,
                new ArrayList<>(), probabilities);
        NodeDependency smugglingTDep = new NodeDependency(smugglingParentsList, new ArrayList<>(),
                probabilities);
        smuggling.setTimeZeroDependency(smuggling0Dep);
        smuggling.setTimeTDependency(smugglingTDep);

        NodeDependency nS0Dep = new NodeDependency(new ArrayList<>(), new ArrayList<>(),
                new double[][]{{0.7, 0.3}});
        NodeDependency nSTDep = new NodeDependency(new ArrayList<>(), new ArrayList<>(),
                new double[][]{{0.7, 0.3}});
        nullSpeed.setTimeZeroDependency(nS0Dep);
        nullSpeed.setTimeTDependency(nSTDep);

        NodeDependency iTA0Dep = new NodeDependency(new ArrayList<>(), new ArrayList<>(),
                new double[][]{{0.8, 0.2}});
        NodeDependency iTATDep = new NodeDependency(new ArrayList<>(), new ArrayList<>(),
                new double[][]{{0.8, 0.2}});
        inTrajectoryArea.setTimeZeroDependency(iTA0Dep);
        inTrajectoryArea.setTimeTDependency(iTATDep);

        NodeDependency iIRA0Dep = new NodeDependency(new ArrayList<>(), new ArrayList<>(),
                new double[][]{{0.8, 0.2}});
        NodeDependency iIRATDep = new NodeDependency(new ArrayList<>(), new ArrayList<>(),
                new double[][]{{0.8, 0.2}});
        isInReportedArea.setTimeZeroDependency(iIRA0Dep);
        isInReportedArea.setTimeTDependency(iIRATDep);

        graph = new Graph(0, "testGraph", NUM_TIME_SLICES,
                Arrays.asList(smuggling, nullSpeed, inTrajectoryArea, isInReportedArea));

        Vessel[] vessels = new Vessel[NUM_TIME_SLICES];
        for (int i = 0; i < vessels.length; ++i) {
            vessels[i] = new Vessel("test", 0);
            vessels[i].setSpeed(i < 2 ? 0.0 : 3.0);
            vessels[i].setLongitude(1.0 * i);
        }

        VesselCache mockCache = mock(VesselCache.class);
        when(mockCache.getVesselsByUuid(anyString()))
                .thenReturn(vessels);

        GraphRepository mockRepository = mock(GraphRepository.class);
        when(mockRepository.findAll())
                .thenReturn(Collections.singletonList(graph));

        EvidenceFormulaEvaluator mockEvaluator = mock(EvidenceFormulaEvaluator.class);
        ArgumentCaptor<EvidenceFormula> evidenceFormulaCaptor = ArgumentCaptor
                .forClass(EvidenceFormula.class);
        ArgumentCaptor<Vessel> vesselCaptor = ArgumentCaptor.forClass(Vessel.class);

        Set<AreaOfInterest> correlatedAois = new HashSet<>();
        Set<Vessel> correlatedVessels = new HashSet<>();
        when(mockEvaluator
                .evaluate(anyInt(), vesselCaptor.capture(), evidenceFormulaCaptor.capture()))
                .then(invocation -> {
                    EvidenceFormula formula = evidenceFormulaCaptor.getValue();
                    Vessel vessel = vesselCaptor.getValue();
                    switch (formula.getName()) {
                        case "nullSpeed":
                            return vessel.getSpeed() <= 2;
                        case "inArea":
                            correlatedAois.add(AREA);
                            return vessel.getLongitude() <= 4 && vessel.getLongitude() >= 2;
                        default:
                            return false;
                    }
                });

        doAnswer(invocation -> {
            correlatedAois.clear();
            correlatedVessels.clear();
            return null;
        }).when(mockEvaluator).reset();

        when(mockEvaluator.getCorrelatedAois()).thenReturn(correlatedAois);
        when(mockEvaluator.getCorrelatedVessels()).thenReturn(correlatedVessels);

        EvidenceFormulaRepository mockFormulaRepository = mock(EvidenceFormulaRepository.class);
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        when(mockFormulaRepository.findByName(nameCaptor.capture())).then(invocation -> Optional.of((new EvidenceFormula(nameCaptor.getValue(), null))));
        when(mockFormulaRepository.existsByName(anyString())).thenReturn(true);

        inferenceManager = new InferenceManager(mockCache, mockRepository, mockFormulaRepository,
                mockEvaluator);
    }

    @Test
    @DisplayName("InferenceManager produces correct output graph.")
    void testInferenceManagerCorrectResult() {
        List<Outcome> outcomes = inferenceManager.calculateInference("test");

        assertEquals(1, outcomes.size());
        Outcome outcome = outcomes.get(0);

        double[][][] expectedValues = new double[][][]{
                {{0.4, 0.6}, {0.4, 0.6}, {0.001, 0.999}, {0.001, 0.999}, {0.001, 0.999}},
                {{1.0, 0.0}, {1.0, 0.0}, {0.0, 1.0}, {0.0, 1.0}, {0.0, 1.0}},
                {{0.0, 1.0}, {0.0, 1.0}, {0.0, 1.0}, {0.0, 1.0}, {0.0, 1.0}},
                {{0.0, 1.0}, {0.0, 1.0}, {1.0, 0.0}, {1.0, 0.0}, {1.0, 0.0}},
        };

        Graph graph = outcome.getCorrelatedNetwork();
        for (int nodeIndex = 0; nodeIndex < expectedValues.length; nodeIndex++) {
            Node node = graph.getNodes().get(nodeIndex);
            assertTrue(node.isValueNode());
            ValueNode valueNode = (ValueNode) node;
            for (int timeSlice = 0; timeSlice < NUM_TIME_SLICES; timeSlice++) {
                assertArrayEquals(expectedValues[nodeIndex][timeSlice], valueNode.getValue()[timeSlice], 1e-2);
            }
        }
    }

    @Test
    @DisplayName("InferenceManager set correlated vessels and areas of interest.")
    void testCorrelatedVesselsAndAreasCorrect() {
        List<Outcome> outcomes = inferenceManager.calculateInference("test");
        Outcome outcome = outcomes.get(0);

        Set<AreaOfInterest> correlatedAois = outcome.getCorrelatedAOIs();
        assertEquals(1, correlatedAois.size());
        assertTrue(correlatedAois.contains(AREA));

        Set<Vessel> correlatedVessels = outcome.getCorrelatedVessels();
        assertEquals(1, correlatedVessels.size());

        Vessel vessel = correlatedVessels.iterator().next();
        assertEquals("test", vessel.getUuid());
    }

    @Test
    void testVirtualEvidence() {
        List<Node> newNodes = new ArrayList<>();
        smuggling.setEvidenceFormulaName(null);
        nullSpeed.setEvidenceFormulaName(null);
        inTrajectoryArea.setEvidenceFormulaName(null);
        isInReportedArea.setEvidenceFormulaName(null);
        newNodes.add(smuggling);
        newNodes.add(nullSpeed);
        ValueNode newInTrajectoryArea = new ValueNode(inTrajectoryArea, new double[][] {{0.6, 0.4}});
        newNodes.add(newInTrajectoryArea);
        newNodes.add(isInReportedArea);
        graph = new Graph(0, "testGraph", NUM_TIME_SLICES, newNodes);
        AmidstGraphAdapter adapter = new AmidstGraphAdapter(graph);
        inferenceManager.calculateInference(adapter, null, Algorithm.IMPORTANCE_SAMPLING);
    }
}
