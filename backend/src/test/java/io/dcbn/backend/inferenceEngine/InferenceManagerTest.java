package io.dcbn.backend.inferenceEngine;


import io.dcbn.backend.graph.*;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InferenceManagerTest {

    private final Position ZERO_POSITION = new Position(0, 0);
    private Graph graph;

    @BeforeEach
    public void setUp() {
        List<Node> nodes = new ArrayList<>();
        Node smuggling = new Node(0, "smuggling", null, null, "", null, StateType.BOOLEAN, ZERO_POSITION);
        Node nullSpeed = new Node(0, "nullSpeed", null, null, "", null, StateType.BOOLEAN, ZERO_POSITION);
        Node inTrajectoryArea = new Node(0, "inTrajectoryArea", null, null, "", null, StateType.BOOLEAN, ZERO_POSITION);
        Node isInReportedArea = new Node(0, "isInReportedArea", null, null, "", null, StateType.BOOLEAN, ZERO_POSITION);

        Node[] smugglingParents = new Node[]{nullSpeed, inTrajectoryArea, isInReportedArea};
        double[][] probabilities = {{0.8, 0.2}, {0.6, 0.4}, {0.4, 0.6}, {0.4, 0.6}, {0.2, 0.8}, {0.2, 0.8}, {0.001, 0.999}, {0.001, 0.999}};
        NodeDependency smuggling0Dep = new NodeDependency(0, Arrays.asList(smugglingParents), new ArrayList<>(), probabilities);
        NodeDependency smugglingTDep = new NodeDependency(0, new ArrayList<>(), new ArrayList<>(), new double[][] {});
        smuggling.setTimeZeroDependency(smuggling0Dep);
        smuggling.setTimeTDependency(smugglingTDep);

        NodeDependency nS0Dep = new NodeDependency(0, new ArrayList<>(), new ArrayList<>(), new double[][] {{0.7, 0.3}});
        NodeDependency nSTDep = new NodeDependency(0, new ArrayList<>(), new ArrayList<>(), new double[][] {{0.7, 0.3}});
        nullSpeed.setTimeZeroDependency(nS0Dep);
        nullSpeed.setTimeTDependency(nSTDep);

        NodeDependency iTA0Dep = new NodeDependency(0, new ArrayList<>(), new ArrayList<>(), new double[][] {{0.8, 0.2}});
        NodeDependency iTATDep = new NodeDependency(0, new ArrayList<>(), new ArrayList<>(), new double[][] {{0.8, 0.2}});
        inTrajectoryArea.setTimeZeroDependency(iTA0Dep);
        inTrajectoryArea.setTimeTDependency(iTATDep);

        NodeDependency iIRA0Dep = new NodeDependency(0, new ArrayList<>(), new ArrayList<>(), new double[][] {{0.8, 0.2}});
        NodeDependency iIRATDep = new NodeDependency(0, new ArrayList<>(), new ArrayList<>(), new double[][] {{0.8, 0.2}});
        isInReportedArea.setTimeZeroDependency(iIRA0Dep);
        isInReportedArea.setTimeTDependency(iIRATDep);

        this.graph = new Graph(0, "testGraph", 10, nodes);
    }
}
