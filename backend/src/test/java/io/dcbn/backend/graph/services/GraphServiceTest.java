package io.dcbn.backend.graph.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GraphServiceTest {

  GraphService service;

  long user1 = new Random().nextLong();
  long user2 = new Random().nextLong();
  long user3 = new Random().nextLong();
  long user4 = new Random().nextLong();
  long user5 = new Random().nextLong();
  long graph1 = new Random().nextLong();
  long graph2 = new Random().nextLong();
  long graph3 = new Random().nextLong();
  long graph4 = new Random().nextLong();
  long graph5 = new Random().nextLong();

  @BeforeEach
  void setUp() {
    service = new GraphService(null);
  }

  @Test
//Test simple lockGraph
  void testLockGraph() {
    service.lockGraph(graph1, user1);
    service.lockGraph(graph2, user2);
    service.lockGraph(graph3, user3);
    service.lockGraph(graph4, user4);
    service.lockGraph(graph5, user5);
    //all five locks should be set
    assertTrue(service.getLock().containsKey(graph1) & service.getLock().containsKey(graph2) &
        service.getLock().containsKey(graph3) & service.getLock().containsKey(graph4) &
        service.getLock().containsKey(graph5));
  }

  @Test
//Test simple unlockGraph
  void testUnlockGraph() {
    service.lockGraph(graph1, user1);
    service.lockGraph(graph2, user2);
    service.lockGraph(graph3, user3);
    service.lockGraph(graph4, user4);
    service.lockGraph(graph5, user5);

    service.unlockGraph(graph2, user2);
    service.unlockGraph(graph4, user4);
    //Graph 2 and Graph 4 should not be locked
    assertTrue(service.getLock().containsKey(graph1) & !(service.getLock().containsKey(graph2)) &
        service.getLock().containsKey(graph3) & !(service.getLock().containsKey(graph4)) &
        service.getLock().containsKey(graph5));
  }

  @Test
    //Same user trying to lock 2 Graphs
  void testDoubleLock() {
    service.lockGraph(graph1, user1);
    service.lockGraph(graph2, user1);
    //Only graph2 should be locked
    assertTrue(!service.getLock().containsKey(graph1) & service.getLock().containsKey(graph2));
  }

  @Test
  void testExpectedException() {
    service.lockGraph(graph1, user1);

    assertThrows(IllegalArgumentException.class, () -> service.lockGraph(graph1, user2));

  }
}