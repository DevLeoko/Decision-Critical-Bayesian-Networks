package io.dcbn.backend.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {

    private Position ZERO_POSITION = new Position(0.0, 0.0);

    private Graph graph1;
    private Graph graph2;
    private Node node1;

    @BeforeEach
    public void setUp() {
        NodeDependency nd1 = new NodeDependency(Collections.emptyList(), Collections.emptyList(), new double[][]{{0.6, 0.4}});
        node1 = new Node("smuggling", nd1, nd1, "",
                null, StateType.BOOLEAN, ZERO_POSITION);
        node1.setId(0);

        NodeDependency nd2 = new NodeDependency(Collections.emptyList(), Collections.emptyList(), new double[][]{{0.6, 0.4}});
        Node node2 = new Node("smuggling", nd2, nd2, "",
                null, StateType.BOOLEAN, ZERO_POSITION);
        node2.setId(0);

        graph1 = new Graph(0, "testGraph", 5, Collections.singletonList(node1));
        graph2 = new Graph(0, "testGraph", 5, Collections.singletonList(node2));
    }

    @Test
    public void equalsTest() {
        assertEquals(graph1, graph2);
    }

    @Test public void notEqualsTest() {
        assertEquals(graph1, graph2);

        graph1.setId(1);
        assertNotEquals(graph1, graph2);
        graph1.setId(0);

        graph1.setName("blablablup");
        assertNotEquals(graph1, graph2);
        graph1.setName("testGraph");

        graph1.setTimeSlices(20);
        assertNotEquals(graph1, graph2);
        graph1.setTimeSlices(5);

        graph1.setNodes(Collections.emptyList());
        assertNotEquals(graph1, graph2);
        graph1.setNodes(Collections.singletonList(node1));
    }
}
