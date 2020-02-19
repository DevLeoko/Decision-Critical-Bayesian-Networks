package io.dcbn.backend.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class NodeDependencyTest {

    private final Position ZERO_POSITION = new Position(0.0, 0.0);

    private Node smuggling;

    @BeforeEach
    public void setUp() {
        NodeDependency nodeDependency = new NodeDependency(Collections.emptyList(), Collections.emptyList(), new double[][]{{5.7, 5.3}});
        smuggling = new Node("smuggling", nodeDependency, nodeDependency, "", null, StateType.BOOLEAN,
                ZERO_POSITION);
    }

    @Test
    public void equalsTest() {
        NodeDependency n1 = new NodeDependency(Collections.emptyList(), Collections.emptyList(), new double[][]{{0.7, 0.3}});
        assertEquals(n1, n1);
    }

    @Test
    public void notEqualsTest() {
        NodeDependency n1 = new NodeDependency(Collections.emptyList(), Collections.emptyList(), new double[][]{{0.7, 0.3}});
        NodeDependency n2 = new NodeDependency(Collections.emptyList(), Collections.emptyList(), new double[][]{{0.7, 0.3}});
        assertEquals(n1, n2);

        n1.setParents(Collections.singletonList(smuggling));
        assertNotEquals(n1, n2);
        n1.setParents(Collections.emptyList());

        n1.setParentsTm1(Collections.singletonList(smuggling));
        assertNotEquals(n1, n2);
        n1.setParentsTm1(Collections.emptyList());

        n1.setProbabilities(new double[][]{{0.6, 0.4}});
        assertNotEquals(n1, n2);

    }
}
