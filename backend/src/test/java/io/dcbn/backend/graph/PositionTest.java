package io.dcbn.backend.graph;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class PositionTest {

    @Test
    public void equalsTest() {
        Position p1 = new Position(0.0, 0.1);
        Position p2 = new Position(0.0, 0.1);
        assertEquals(p1, (p2));
    }

    @Test
    public void EqualsDeltaTest() {
        Position p1 = new Position(0.0, 0.1);
        Position p2 = new Position(0.001, 0.099);
        assertTrue(p1.equalsApproximately(p2));
    }

    @Test
    public void notEqualsTest() {
        Position p1 = new Position(0.0, 0.1);
        Position p2 = new Position(0.1, 0.0);
        assertNotEquals(p1, (p2));
    }
}
