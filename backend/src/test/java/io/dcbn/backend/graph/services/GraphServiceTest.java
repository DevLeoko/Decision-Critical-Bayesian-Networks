package io.dcbn.backend.graph.services;

import io.dcbn.backend.authentication.models.DcbnUser;
import io.dcbn.backend.authentication.repositories.DcbnUserRepository;
import io.dcbn.backend.evidenceformula.repository.EvidenceFormulaRepository;
import io.dcbn.backend.graph.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GraphServiceTest {

    private GraphService service;
    private static DcbnUserRepository dcbnRepository;
    private static EvidenceFormulaRepository evidenceFormulaRepository;

    private static DcbnUser dcbnUser1 = mock(DcbnUser.class);

    private static DcbnUser dcbnUser2 = mock(DcbnUser.class);

    private static final Position ZERO_POSITION = new Position(0.0, 0.0);
    private static final int NUM_TIME_SLICES = 5;

    @BeforeAll
    public static void beforeAll() {
        dcbnRepository = mock(DcbnUserRepository.class);

        when(dcbnRepository.findByUsername("user1")).thenReturn(Optional.of(dcbnUser1));
        when(dcbnUser1.getId()).thenReturn((long) 0);

        when(dcbnRepository.findByUsername("user2")).thenReturn(Optional.of(dcbnUser2));
        when(dcbnUser2.getId()).thenReturn((long) 1);

        evidenceFormulaRepository = mock(EvidenceFormulaRepository.class);
    }

    @BeforeEach
    public void setUp() {
        service = new GraphService(dcbnRepository, evidenceFormulaRepository, null);
    }

    @Test
    public void graphAlreadyLockedTest() {
        service.updateLock(1, "user1");
        assertThrows(IllegalArgumentException.class, () -> service.updateLock(1, "user2"));
    }

    @Test
    public void extendLock() {
        service.updateLock(1, "user1");
        assertThrows(IllegalArgumentException.class, () -> service.updateLock(1, "user2"));
        service.updateLock(1, "user1");
        assertThrows(IllegalArgumentException.class, () -> service.updateLock(1, "user2"));
    }

    @Test
    public void hasCyclesTest() {
        Node nodeOne = new Node("nodeOne", null, null, "", null,
                StateType.BOOLEAN, ZERO_POSITION);
        Node nodeTwo = new Node("nodeTwo", null, null, "", null,
                StateType.BOOLEAN, ZERO_POSITION);

        List<Node> nodeOneParentsList = new ArrayList<>();
        nodeOneParentsList.add(nodeTwo);
        double[][] probabilities = {{0.8, 0.2}, {0.6, 0.4}};
        NodeDependency nodeOne0Dep = new NodeDependency(nodeOneParentsList,
                new ArrayList<>(), probabilities);
        NodeDependency nodeOneTDep = new NodeDependency(nodeOneParentsList, new ArrayList<>(),
                probabilities);
        nodeOne.setTimeZeroDependency(nodeOne0Dep);
        nodeOne.setTimeTDependency(nodeOneTDep);

        List<Node> nodeTwoParentsList = new ArrayList<>();
        nodeTwoParentsList.add(nodeOne);
        double[][] probabilitiesNodeTwo = {{0.8, 0.2}, {0.6, 0.4}};
        NodeDependency nodeTwo0Dep = new NodeDependency(nodeTwoParentsList,
                new ArrayList<>(), probabilitiesNodeTwo);
        NodeDependency nodeTwoTDep = new NodeDependency(nodeTwoParentsList, new ArrayList<>(),
                probabilitiesNodeTwo);
        nodeTwo.setTimeZeroDependency(nodeTwo0Dep);
        nodeTwo.setTimeTDependency(nodeTwoTDep);

        Graph graph = new Graph(0, "testGraph", NUM_TIME_SLICES,
                Arrays.asList(nodeOne, nodeTwo));

        assertTrue(service.hasCycles(graph));
    }

    @Test
    public void hasNoCyclesTest() {
        Node nodeOne = new Node("nodeOne", null, null, "", null,
                StateType.BOOLEAN, ZERO_POSITION);
        Node nodeTwo = new Node("nodeTwo", null, null, "", null,
                StateType.BOOLEAN, ZERO_POSITION);

        List<Node> nodeOneParentsList = new ArrayList<>();
        nodeOneParentsList.add(nodeTwo);
        double[][] probabilities = {{0.8, 0.2}, {0.6, 0.4}};
        NodeDependency nodeOne0Dep = new NodeDependency(nodeOneParentsList,
                new ArrayList<>(), probabilities);
        NodeDependency nodeOneTDep = new NodeDependency(nodeOneParentsList, new ArrayList<>(),
                probabilities);
        nodeOne.setTimeZeroDependency(nodeOne0Dep);
        nodeOne.setTimeTDependency(nodeOneTDep);

        double[][] probabilitiesNodeTwo = {{0.8, 0.2}, {0.6, 0.4}};
        NodeDependency nodeTwo0Dep = new NodeDependency(new ArrayList<>(),
                new ArrayList<>(), probabilitiesNodeTwo);
        NodeDependency nodeTwoTDep = new NodeDependency(new ArrayList<>(), new ArrayList<>(),
                probabilitiesNodeTwo);
        nodeTwo.setTimeZeroDependency(nodeTwo0Dep);
        nodeTwo.setTimeTDependency(nodeTwoTDep);

        Graph graph = new Graph(0, "testGraph", NUM_TIME_SLICES,
                Arrays.asList(nodeOne, nodeTwo));

        assertFalse(service.hasCycles(graph));
    }
}
