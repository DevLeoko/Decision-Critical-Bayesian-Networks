package io.dcbn.backend.inferenceEngine;


import static org.junit.jupiter.api.Assertions.assertEquals;

import de.fraunhofer.iosb.iad.maritime.datamodel.AreaOfInterest;
import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import io.dcbn.backend.core.VesselCache;
import io.dcbn.backend.datamodel.Outcome;
import io.dcbn.backend.evidenceFormula.model.EvidenceFormula;
import io.dcbn.backend.evidenceFormula.services.EvidenceFormulaEvaluator;
import io.dcbn.backend.graph.AmidstGraphAdapter;
import io.dcbn.backend.graph.Graph;
import io.dcbn.backend.graph.Node;
import io.dcbn.backend.graph.NodeDependency;
import io.dcbn.backend.graph.Position;
import io.dcbn.backend.graph.StateType;
import io.dcbn.backend.graph.repositories.GraphRepository;
import io.dcbn.backend.inference.InferenceManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class InferenceManagerTest {

  private final Position ZERO_POSITION = new Position(0, 0);
  private Graph graph;

  private VesselCache mockCache;
  private GraphRepository mockRepository;
  private EvidenceFormulaEvaluator mockEvaluator;
  private InferenceManager inferenceManager;

  @BeforeEach
  public void setUp() {
    Node smuggling = new Node(0, "smuggling", null, null, "", null, StateType.BOOLEAN,
        ZERO_POSITION);
    Node nullSpeed = new Node(0, "nullSpeed", null, null, "",
        new EvidenceFormula("nullSpeed", null), StateType.BOOLEAN, ZERO_POSITION);
    Node inTrajectoryArea = new Node(0, "inTrajectoryArea", null, null, "",
        new EvidenceFormula("inTrajectory", null), StateType.BOOLEAN, ZERO_POSITION);
    Node isInReportedArea = new Node(0, "isInReportedArea", null, null, "",
        new EvidenceFormula("inArea", null), StateType.BOOLEAN, ZERO_POSITION);

    Node[] smugglingParents = new Node[]{nullSpeed, inTrajectoryArea, isInReportedArea};
    double[][] probabilities = {{0.8, 0.2}, {0.6, 0.4}, {0.4, 0.6}, {0.4, 0.6}, {0.2, 0.8},
        {0.2, 0.8}, {0.001, 0.999}, {0.001, 0.999}};
    NodeDependency smuggling0Dep = new NodeDependency(0, Arrays.asList(smugglingParents),
        new ArrayList<>(), probabilities);
    NodeDependency smugglingTDep = new NodeDependency(0, new ArrayList<>(), new ArrayList<>(),
        new double[][]{});
    smuggling.setTimeZeroDependency(smuggling0Dep);
    smuggling.setTimeTDependency(smugglingTDep);

    NodeDependency nS0Dep = new NodeDependency(0, new ArrayList<>(), new ArrayList<>(),
        new double[][]{{0.7, 0.3}});
    NodeDependency nSTDep = new NodeDependency(0, new ArrayList<>(), new ArrayList<>(),
        new double[][]{{0.7, 0.3}});
    nullSpeed.setTimeZeroDependency(nS0Dep);
    nullSpeed.setTimeTDependency(nSTDep);

    NodeDependency iTA0Dep = new NodeDependency(0, new ArrayList<>(), new ArrayList<>(),
        new double[][]{{0.8, 0.2}});
    NodeDependency iTATDep = new NodeDependency(0, new ArrayList<>(), new ArrayList<>(),
        new double[][]{{0.8, 0.2}});
    inTrajectoryArea.setTimeZeroDependency(iTA0Dep);
    inTrajectoryArea.setTimeTDependency(iTATDep);

    NodeDependency iIRA0Dep = new NodeDependency(0, new ArrayList<>(), new ArrayList<>(),
        new double[][]{{0.8, 0.2}});
    NodeDependency iIRATDep = new NodeDependency(0, new ArrayList<>(), new ArrayList<>(),
        new double[][]{{0.8, 0.2}});
    isInReportedArea.setTimeZeroDependency(iIRA0Dep);
    isInReportedArea.setTimeTDependency(iIRATDep);

    this.graph = new Graph(0, "testGraph", 10,
        Arrays.asList(smuggling, nullSpeed, inTrajectoryArea, isInReportedArea));

    Vessel[] vessels = new Vessel[5];
    for (int i = 0; i < 5; ++i) {
      vessels[i] = new Vessel();
      vessels[i].setSpeed(0.0);
      vessels[i].setLongitude(1.0 * i);
    }

    List<AreaOfInterest> correlatedAois = new ArrayList<>();

    mockCache = Mockito.mock(VesselCache.class);
    Mockito.when(mockCache.getVesselsByUuid(Mockito.anyString()))
        .thenReturn(vessels);

    mockRepository = Mockito.mock(GraphRepository.class);
    Mockito.when(mockRepository.findAll())
        .thenReturn(Collections.singletonList(graph));

    mockEvaluator = Mockito.mock(EvidenceFormulaEvaluator.class);
    ArgumentCaptor<EvidenceFormula> evidenceFormulaCaptor = ArgumentCaptor
        .forClass(EvidenceFormula.class);
    ArgumentCaptor<Vessel> vesselCaptor = ArgumentCaptor.forClass(Vessel.class);

    Mockito.when(mockEvaluator
        .evaluate(Mockito.anyInt(), vesselCaptor.capture(), evidenceFormulaCaptor.capture()))
        .then(invocation -> {
          EvidenceFormula formula = evidenceFormulaCaptor.getValue();
          Vessel vessel = vesselCaptor.getValue();
          switch (formula.getName()) {
            case "nullSpeed":
              return vessel.getSpeed() > 2;
            case "inArea":
              correlatedAois.add(new AreaOfInterest("TEST_AREA", null));
              return vessel.getLongitude() <= 4 && vessel.getLongitude() >= 2;
            default:
              return false;
          }
        });

    Mockito.when(mockEvaluator.getCorrelatedAois()).thenReturn(correlatedAois);
    inferenceManager = new InferenceManager(mockCache, mockRepository, mockEvaluator);
  }

  @Test
  void testInferenceManagerCorrectResult() {
    List<Outcome> outcomes = inferenceManager.calculateInference("test");
    Mockito.verify(mockEvaluator).evaluate(Mockito.anyInt(), Mockito.any(Vessel.class),
        Mockito.argThat(formula -> "inArea".equals(formula.getName())));

    assertEquals(1, outcomes.size());
    Outcome outcome = outcomes.get(0);

    Mockito.verifyNoMoreInteractions(mockEvaluator);

    List<AreaOfInterest> correlatedAois = outcome.getCorrelatedAOIs();
    assertEquals(1, correlatedAois.size());
    assertEquals(new AreaOfInterest("TEST_AREA", null), correlatedAois.get(0));
  }
}
