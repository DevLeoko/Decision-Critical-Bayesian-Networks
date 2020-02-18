package io.dcbn.backend.graph.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dcbn.backend.authentication.models.LoginRequest;
import io.dcbn.backend.graph.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.io.StringReader;
import java.util.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GraphControllerTest {

    private static final String RESOURCE_PATH = "src/test/resources";

    @Autowired
    private MockMvc mockMvc;

    private String token;

    ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setUp() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("admin");
        String loginRequestString = mapper.writeValueAsString(loginRequest);

        MvcResult result = this.mockMvc.perform(post("/login").content(loginRequestString)).andExpect(status().isOk()).andReturn();

        token = mapper.readTree(new StringReader(result.getResponse().getContentAsString())).get("token").asText();
    }

    @AfterEach
    public void tearDown() throws Exception {
        List<Graph> graphs = getAllGraphs();
        for (Graph entry : graphs) {
            deleteGraphByName(entry.getName());
        }
    }

    private void deleteGraphByName(String name) throws Exception {
        this.mockMvc.perform(delete("/graphs/" + getIdByName(name))
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    private Graph getGraphById(long id) throws Exception {
        MvcResult result = this.mockMvc.perform(get("/graphs/" + id)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        return mapper.readValue(result.getResponse().getContentAsString(), Graph.class);
    }

    private long getIdByName(String name) throws Exception {
        MvcResult result = this.mockMvc.perform(get("/graphs?withStructure=false")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        List<Graph> graphs = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Graph>>(){});
        for (Graph entry : graphs) {
            if(entry.getName().equals(name)) {
                return entry.getId();
            }
        }
        return -1;
    }

    private long createGraph(Graph graph) throws Exception {
        String graphJson = mapper.writeValueAsString(graph);
        MvcResult result = this.mockMvc.perform(post("/graphs")
                .header("Authorization", "Bearer " + token)
                .content(graphJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        return Long.parseLong(result.getResponse().getContentAsString());
    }

    private List<Graph> getAllGraphs() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/graphs?withStructure=false")
            .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andReturn();
        return mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Graph>>(){});
    }

    private void replaceGraphWithId(long id, Graph graph) throws Exception {
        String graphJson = mapper.writeValueAsString(graph);
        this.mockMvc.perform(put("/graphs/" + id)
            .header("Authorization", "Bearer " + token)
            .content(graphJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    }

    private void renameGraph(long id) throws Exception {
        this.mockMvc.perform(post("/graphs/" + id + "/name")
                .header("Authorization", "Bearer " + token)
                .content("graph2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private Graph getGraphWithNotExistentEvidenceFormula() {
        Node nodeOne = new Node("nodeOne", null, null, "xffff", "doenstda s",
                StateType.BOOLEAN, new Position(0.0,0.0));
        Node nodeTwo = new Node("nodeTwo", null, null, "xffff", null,
                StateType.BOOLEAN, new Position(0.0,0.0));

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
        Graph g = new Graph(0, "testGraph", 10, Arrays.asList(nodeOne, nodeTwo));
        System.out.println(g.toString());
        return new Graph(0, "testGraph", 10, Arrays.asList(nodeOne, nodeTwo));
    }

    private Graph getGraphWithCycle() {
        Node nodeOne = new Node("nodeOne", null, null, "xffff", null,
                StateType.BOOLEAN, new Position(0.0, 0.0));

        List<Node> nodeOneParentsList = new ArrayList<>();
        nodeOneParentsList.add(nodeOne);
        double[][] probabilities = {{0.8, 0.2}, {0.6, 0.4}};
        NodeDependency nodeOne0Dep = new NodeDependency(nodeOneParentsList,
                new ArrayList<>(), probabilities);
        NodeDependency nodeOneTDep = new NodeDependency(nodeOneParentsList, new ArrayList<>(),
                probabilities);
        nodeOne.setTimeZeroDependency(nodeOne0Dep);
        nodeOne.setTimeTDependency(nodeOneTDep);

        return new Graph(0, "graph1", 10, Collections.singletonList(nodeOne));
    }

    @Test
    public void renameGraphNotFoundTest() throws Exception {
        this.mockMvc.perform(post("/graphs/" + 999 + "/name")
                .header("Authorization", "Bearer " + token)
                .content("testtest")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void renameGraphNameAlreadyExistsTest() throws Exception {
        String graphName = "graph1";
        Graph graph = new Graph(graphName, 5, Collections.emptyList());
        long id = createGraph(graph);

        String graphNameTwo = "graph2";
        Graph graphTwo = new Graph(graphNameTwo, 5, Collections.emptyList());
        createGraph(graphTwo);

        this.mockMvc.perform(post("/graphs/" + id + "/name")
                .header("Authorization", "Bearer " + token)
                .content(graphNameTwo)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void renameGraphTest() throws Exception {
        String graphName = "graph1";
        Graph graph = new Graph(graphName, 5, Collections.emptyList());
        long id = createGraph(graph);

        renameGraph(id);
        assertEquals(graph, getGraphById(id));
    }

    @Test
    public void createGraphTest() throws Exception {
        String graphName = "graph1";
        Graph graph = new Graph(graphName, 5, Collections.emptyList());
        createGraph(graph);
    }

    @Test
    public void getGraphsTest() throws Exception {
        String graphName = "graph1";
        Graph graph = new Graph(graphName, 5, Collections.emptyList());
        createGraph(graph);

        List<Graph> graphs = getAllGraphs();
        assertTrue(graphs.get(0).equals(graph) || graphs.get(1).equals(graph));
    }

    @Test
    public void getGraphByIdTest() throws Exception {
        String graphName = "graph1";
        Graph graph = new Graph(graphName, 5, Collections.emptyList());
        createGraph(graph);

        Graph resultGraph = getGraphById(getIdByName(graphName));
        assertEquals(graph, resultGraph);
    }

    @Test
    public void deleteGraphTest() throws Exception {
        String graphName = "graph1";
        Graph graph = new Graph(graphName, 5, Collections.emptyList());
        createGraph(graph);

        deleteGraphByName(graphName);

        List<Graph> graphs = getAllGraphs();

        assertFalse(graphs.contains(graph));
    }

    @Test
    public void deleteGraphNotFound() throws Exception {
        this.mockMvc.perform(delete("/graphs/" + 999)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateGraphTest() throws Exception {
        String graphName = "graph1";
        Graph graph = new Graph(graphName, 5, Collections.emptyList());
        createGraph(graph);

        long id = getIdByName(graphName);
        graph = new Graph("graph2", 10, Collections.emptyList());

        replaceGraphWithId(id, graph);

        Graph resultGraph = getGraphById(id);
        assertNotEquals(graph, resultGraph);
        assertEquals("graph2", resultGraph.getName());
        assertEquals(10, resultGraph.getTimeSlices());
    }

    @Test
    public void createGraphHasCycleTest() throws Exception {
        Graph graph = getGraphWithCycle();

        String graphJson = mapper.writeValueAsString(graph);
        this.mockMvc.perform(post("/graphs")
                .header("Authorization", "Bearer " + token)
                .content(graphJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createGraphEvidenceFormulaDoesntExistTest() throws Exception {
        Graph graph = getGraphWithNotExistentEvidenceFormula();

        String graphJson = mapper.writeValueAsString(graph);
        this.mockMvc.perform(post("/graphs")
                .header("Authorization", "Bearer " + token)
                .content(graphJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateGraphHasCycleTest() throws Exception {
        Graph graph = new Graph(0,"graph1", 5, Collections.emptyList());
        long id = createGraph(graph);
        graph = getGraphWithCycle();
        String graphJson = mapper.writeValueAsString(graph);
        this.mockMvc.perform(put("/graphs/" + id)
                .header("Authorization", "Bearer " + token)
                .content(graphJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void updateGraphEvidenceFormulaDoesntExistTest() throws Exception {
        Graph graph = new Graph(0,"graph1", 5, Collections.emptyList());
        long id = createGraph(graph);
        graph = getGraphWithNotExistentEvidenceFormula();
        String graphJson = mapper.writeValueAsString(graph);
        this.mockMvc.perform(put("/graphs/" + id)
                .header("Authorization", "Bearer " + token)
                .content(graphJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void importTest() {
        File genieFile = new File(RESOURCE_PATH + "/networks/genie/smuggling2.xdsl");
    }
}
