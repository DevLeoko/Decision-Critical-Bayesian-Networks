package io.dcbn.backend.inferenceEngine;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import de.fraunhofer.iosb.iad.maritime.datamodel.AreaOfInterest;
import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import io.dcbn.backend.core.VesselCache;
import io.dcbn.backend.datamodel.Outcome;
import io.dcbn.backend.evidenceFormula.model.EvidenceFormula;
import io.dcbn.backend.evidenceFormula.services.EvidenceFormulaEvaluator;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class InferenceManagerTest {

  private static final Position ZERO_POSITION = new Position(0, 0);
  private static final AreaOfInterest AREA = new AreaOfInterest("TEST_AREA", null);

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
        probabilities);
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

    Graph graph = new Graph(0, "testGraph", 5,
        Arrays.asList(smuggling, nullSpeed, inTrajectoryArea, isInReportedArea));

    Vessel[] vessels = new Vessel[5];
    for (int i = 0; i < 5; ++i) {
      vessels[i] = new Vessel("test", 0);
      vessels[i].setSpeed(0.0);
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
      return null;
    }).when(mockEvaluator).reset();

    when(mockEvaluator.getCorrelatedAois()).thenReturn(correlatedAois);
    when(mockEvaluator.getCorrelatedVessels()).thenReturn(correlatedVessels);
    inferenceManager = new InferenceManager(mockCache, mockRepository, mockEvaluator);
  }

  @Test
  void testInferenceManagerCorrectResult() {
    List<Outcome> outcomes = inferenceManager.calculateInference("test");

    assertEquals(1, outcomes.size());
    ObjectMapper mapper = new JsonMapper();
    try {
      System.out.println(mapper.writeValueAsString(outcomes.iterator().next()));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  @Test
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
}
