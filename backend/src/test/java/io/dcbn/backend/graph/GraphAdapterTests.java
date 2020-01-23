package io.dcbn.backend.graph;

import eu.amidst.core.distribution.Multinomial;
import eu.amidst.core.distribution.Multinomial_MultinomialParents;
import eu.amidst.core.variables.Assignment;
import eu.amidst.core.variables.HashMapAssignment;
import eu.amidst.core.variables.Variable;
import eu.amidst.dynamic.models.DynamicBayesianNetwork;
import eu.amidst.dynamic.models.DynamicDAG;
import eu.amidst.dynamic.variables.DynamicVariables;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This class is testing the AmidstGraphAdapter.
 */
public class GraphAdapterTests {

    private Node a;
    private Node b;
    private Node c;
    private Graph testGraph;
    private DynamicBayesianNetwork generatedDBN;
    private DynamicBayesianNetwork correctDBN;
    private final Position ZERO_POSITION = new Position(0, 0);


    /**
     * Creating the graph object and the DynamicBayesianNetwork object
     */
    @BeforeEach
    public void setUp() {
        NodeDependency nodeATimeZeroDependency = new NodeDependency(0, new ArrayList<>(), new ArrayList<>(), new double[][]{{0.3, 0.7}});
        NodeDependency nodeATimeTDependency = new NodeDependency(0, new ArrayList<>(), new ArrayList<>(), new double[][]{{0.5, 0.5}});
        a = new Node(0, "A", nodeATimeZeroDependency, nodeATimeTDependency, null, null, StateType.BOOLEAN,
                ZERO_POSITION);
        NodeDependency nodeBTimeZeroDependency = new NodeDependency(0, new ArrayList<>(), new ArrayList<>(), new double[][]{{0.2, 0.8}});
        NodeDependency nodeBTimeTDependency = new NodeDependency(0, new ArrayList<>(), new ArrayList<>(), new double[][]{{0.5, 0.5}});
        b = new Node(0, "B", nodeBTimeZeroDependency, nodeBTimeTDependency, null, null, StateType.BOOLEAN,
                ZERO_POSITION);

        NodeDependency nodeCTimeZeroDependency = new NodeDependency(0, Arrays.asList(a, b), new ArrayList<>(),
                new double[][]{{0.999, 0.001}, {0.6, 0.4}, {0.8, 0.2}, {0.2, 0.8}});

        c = new Node(0, "C", nodeCTimeZeroDependency, null, null, null, StateType.BOOLEAN,
                ZERO_POSITION);
        NodeDependency nodeCTimeTDependency = new NodeDependency(0, Arrays.asList(a, b), Collections.singletonList(c), new double[][]{{0.1, 0.9},
                {0.2, 0.8}, {0.3, 0.7}, {0.4, 0.6}, {0.5, 0.5}, {0.6, 0.4}, {0.7, 0.3}, {0.8, 0.2}});
        c.setTimeTDependency(nodeCTimeTDependency);
        Node[] nodes = new Node[]{a, b, c};
        List<Node> nodeList = new ArrayList<>();
        nodeList.add(a);
        nodeList.add(b);
        nodeList.add(c);
        testGraph = new Graph(0, "testGraph", 10, nodeList);
        AmidstGraphAdapter amidstGraphAdapter = new AmidstGraphAdapter(testGraph);
        generatedDBN = amidstGraphAdapter.getDbn();
        //----------------generating correct DBN--------------------
        DynamicVariables dynamicVariables = new DynamicVariables();
        Variable a = dynamicVariables.newMultinomialDynamicVariable("A", 2);
        Variable b = dynamicVariables.newMultinomialDynamicVariable("B", 2);
        Variable c = dynamicVariables.newMultinomialDynamicVariable("C", 2);
        Variable c_interface = c.getInterfaceVariable();


        DynamicDAG dynamicDAG = new DynamicDAG(dynamicVariables);

        dynamicDAG.getParentSetTime0(c).addParent(a);
        dynamicDAG.getParentSetTime0(c).addParent(b);

        dynamicDAG.getParentSetTimeT(c).addParent(a);
        dynamicDAG.getParentSetTimeT(c).addParent(b);
        dynamicDAG.getParentSetTimeT(c).addParent(c_interface);

        DynamicBayesianNetwork dbn = new DynamicBayesianNetwork(dynamicDAG);

        //SET PROBALILITIES AT TIME 0

        Multinomial multinomialA = dbn.getConditionalDistributionTime0(a);
        Multinomial multinomialB = dbn.getConditionalDistributionTime0(b);
        Multinomial_MultinomialParents multinomial_multinomialParentsC0 = dbn.getConditionalDistributionTime0(c);
        Multinomial_MultinomialParents multinomial_multinomialParentsCT = dbn.getConditionalDistributionTimeT(c);

        // TIME 0
        multinomialA.setProbabilities(new double[]{0.3, 0.7});
        multinomialB.setProbabilities(new double[]{0.2, 0.8});
        multinomial_multinomialParentsC0.getMultinomial(0).setProbabilities(new double[]{0.999, 0.001});
        multinomial_multinomialParentsC0.getMultinomial(1).setProbabilities(new double[]{0.6, 0.4});
        multinomial_multinomialParentsC0.getMultinomial(2).setProbabilities(new double[]{0.8, 0.2});
        multinomial_multinomialParentsC0.getMultinomial(3).setProbabilities(new double[]{0.2, 0.8});
        // TIME T
        Assignment assignment = new HashMapAssignment(3);
        assignment.setValue(a, 1);
        multinomial_multinomialParentsCT.getMultinomial(assignment);
        multinomial_multinomialParentsCT.getMultinomial(0).setProbabilities(new double[]{0.1, 0.9});
        multinomial_multinomialParentsCT.getMultinomial(1).setProbabilities(new double[]{0.2, 0.8});
        multinomial_multinomialParentsCT.getMultinomial(2).setProbabilities(new double[]{0.3, 0.7});
        multinomial_multinomialParentsCT.getMultinomial(3).setProbabilities(new double[]{0.4, 0.6});
        multinomial_multinomialParentsCT.getMultinomial(4).setProbabilities(new double[]{0.5, 0.5});
        multinomial_multinomialParentsCT.getMultinomial(5).setProbabilities(new double[]{0.6, 0.4});
        multinomial_multinomialParentsCT.getMultinomial(6).setProbabilities(new double[]{0.7, 0.3});
        multinomial_multinomialParentsCT.getMultinomial(7).setProbabilities(new double[]{0.8, 0.2});
        correctDBN = dbn;
    }

    /**
     * Comparing the structure and data of both DynamicBayesianNetwork
     */
    @Test
    public void testDBNAdapter() {
//        System.out.println(correctDBN.toString());
//        System.out.println("---------------------------------------------------------------");
//        System.out.println(generatedDBN.toString());
        Assertions.assertTrue(correctDBN.equalDBNs(generatedDBN, 0));
    }

    /**
     * Testing basic virtual evidence insertion.
     */
    @Test
    public void testVirtualEvidence() {
        ValueNode newA = new ValueNode("A", " ", StateType.BOOLEAN, ZERO_POSITION, new double[][]{{0.4, 0.6}});
        List<Node> nodeList = new ArrayList<>();
        nodeList.add(newA);
        nodeList.add(b);
        nodeList.add(c);
        testGraph = new Graph(0, "testGraph", 10, nodeList);
        AmidstGraphAdapter amidstGraphAdapter = new AmidstGraphAdapter(testGraph);
        generatedDBN = amidstGraphAdapter.getDbn();
        Multinomial multinomialAT0 = correctDBN.getConditionalDistributionTime0(amidstGraphAdapter.getVariableByName(newA.getName()));
        multinomialAT0.setProbabilities(newA.getValue()[0]);
        Multinomial multinomialAT = correctDBN.getConditionalDistributionTimeT(amidstGraphAdapter.getVariableByName(newA.getName()));
        multinomialAT.setProbabilities(newA.getValue()[0]);

//        System.out.println(correctDBN.toString());
//        System.out.println("---------------------------------------------------------------");
//        System.out.println(generatedDBN.toString());
        Assertions.assertTrue(correctDBN.equalDBNs(generatedDBN, 0));
    }

    /**
     * Testing insertion of a virtual evidence on a node with parent-dependencies
     */
    @Test
    public void testRemovedChildInsertVirEvi() {
        ValueNode newC = new ValueNode("C", "", StateType.BOOLEAN, ZERO_POSITION, new double[][]{{0.18, 0.82}});
        List<Node> nodeList = new ArrayList<>();
        nodeList.add(newC);
        testGraph = new Graph(0, "testGraph", 10, nodeList);
        AmidstGraphAdapter amidstGraphAdapter = new AmidstGraphAdapter(testGraph);
        generatedDBN = amidstGraphAdapter.getDbn();
        DynamicVariables dynamicVariables = new DynamicVariables();
        Variable c = dynamicVariables.newMultinomialDynamicVariable("C", 2);
        DynamicDAG dynamicDAG = new DynamicDAG(dynamicVariables);
        correctDBN = new DynamicBayesianNetwork(dynamicDAG);
        Multinomial multinomialCT0 = correctDBN.getConditionalDistributionTime0(correctDBN.getDynamicVariables().getVariableByName("C"));
        multinomialCT0.setProbabilities(newC.getValue()[0]);
        Multinomial multinomialCT = correctDBN.getConditionalDistributionTimeT(correctDBN.getDynamicVariables().getVariableByName("C"));
        multinomialCT.setProbabilities(newC.getValue()[0]);
        Assertions.assertTrue(correctDBN.equalDBNs(generatedDBN, 0));
    }
}
