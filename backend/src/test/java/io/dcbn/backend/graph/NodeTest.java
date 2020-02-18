package io.dcbn.backend.graph;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class NodeTest {

    private final Position ZERO_POSITION = new Position(0.0, 0.0);

    private Node smuggling;
    private Node nullSpeed;
    private Node inTrajectoryArea;
    private Node isInReportedArea;

    @BeforeEach
    public void setUp() {
        smuggling = new Node("smuggling", null, null, "",
                null, StateType.BOOLEAN, ZERO_POSITION);
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
    }

    @Test
    public void equalsTest() {
        assertEquals(smuggling, smuggling);
        assertEquals(nullSpeed, nullSpeed);
        assertEquals(inTrajectoryArea, inTrajectoryArea);
        assertEquals(isInReportedArea, isInReportedArea);

        NodeDependency nd1 = new NodeDependency(Collections.emptyList(), Collections.emptyList(), new double[][]{{0.6, 0.4}});
        Node node1 = new Node("smuggling", nd1, nd1, "",
                null, StateType.BOOLEAN, ZERO_POSITION);
        node1.setId(0);

        NodeDependency nd2 = new NodeDependency(Collections.emptyList(), Collections.emptyList(), new double[][]{{0.6, 0.4}});
        Node node2 = new Node("smuggling", nd2, nd2, "",
                null, StateType.BOOLEAN, ZERO_POSITION);
        node2.setId(0);

        assertEquals(node1, node2);
    }

    @Test
    public void notEqualsTest() {
        NodeDependency nS0Dep = new NodeDependency(new ArrayList<>(), new ArrayList<>(),
                new double[][]{{0.7, 0.3}});
        Node n1 = new Node("node1", nS0Dep, nS0Dep, "", null, StateType.BOOLEAN, ZERO_POSITION);
        Node n2 = new Node("node1", nS0Dep, nS0Dep, "", null, StateType.BOOLEAN, ZERO_POSITION);
        assertEquals(n1, n2);

        n1.setName("node2");
        assertNotEquals(n1, n2);
        n1.setName("node1");

        n1.setColor("xffff");
        assertNotEquals(n1, n2);
        n1.setColor("");

        n1.setEvidenceFormulaName("testFormula");
        assertNotEquals(n1, n2);
        n1.setEvidenceFormulaName(null);

        n1.setPosition(new Position(1.0, 0.0));
        assertNotEquals(n1, n2);
        n1.setPosition(ZERO_POSITION);

        n1.setTimeTDependency(new NodeDependency(new ArrayList<>(), new ArrayList<>(), new double[][]{{0,6,0,4}}));
        assertNotEquals(n1, n2);
        n1.setTimeTDependency(nS0Dep);

        n1.setTimeZeroDependency(new NodeDependency(new ArrayList<>(), new ArrayList<>(), new double[][]{{0,6,0,4}}));
        assertNotEquals(n1, n2);
        n1.setTimeZeroDependency(nS0Dep);

        NodeDependency testDependency = new NodeDependency(Collections.singletonList(smuggling), new ArrayList<>() , new double[][]{{0.7, 0.3}});
        NodeDependency testDependency2 = new NodeDependency(new ArrayList<>(), Collections.singletonList(smuggling), new double[][]{{0.7, 0.3}});

        n1.setTimeTDependency(testDependency);
        assertNotEquals(n1, n2);

        n1.setTimeTDependency(testDependency2);
        assertNotEquals(n1, n2);
        n1.setTimeTDependency(nS0Dep);

        n1.setTimeZeroDependency(testDependency);
        assertNotEquals(n1, n2);

        n1.setTimeZeroDependency(testDependency2);
        assertNotEquals(n1, n2);
        n1.setTimeZeroDependency(nS0Dep);
    }
}
