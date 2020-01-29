package io.dcbn.backend.graph.converters;

import io.dcbn.backend.graph.*;
import lombok.NoArgsConstructor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@NoArgsConstructor
public class GenieConverter {

    private static final String ID = "id";
    private static final List<io.dcbn.backend.graph.Node> EMPTY_NODE_LIST = new ArrayList<>();
    private static final Position ZERO_POSITION = new Position(0, 0);


    public Graph fromGenieToDcbn(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);
        doc.getDocumentElement().normalize();
        Element root = doc.getDocumentElement();
        List<Node> nodesFile = extractChildren(root.getElementsByTagName("nodes").item(0), "cpt");
        NodeList nodesAttributes = root.getElementsByTagName("node");
        List<io.dcbn.backend.graph.Node> dcbnNodes = new ArrayList<>();


        for(int i = 0; i < nodesFile.size(); i++) {
            Node node = nodesFile.get(i);
            String nodeName = node.getAttributes().getNamedItem(ID).getNodeValue();
            Node nodeAttribute = getNodeWithID(nodesAttributes, nodeName);

            //------Setting the state for this node-------
            List<Node> states = extractChildren(node, "state");
            //copy states to array
            List<String> statesNameList = new ArrayList<>();
            int index = 0;
            states.forEach(state -> statesNameList.add(state.getAttributes().getNamedItem(ID).getNodeValue()));
            String[] statesNameArray = new String[states.size()];
            statesNameList.toArray(statesNameArray);
            //finding out what StateType it belongs to
            //TODO Dynamically add enum values? (Interface?)

            //-------Setting the probabilities for the node dependencies---------
            //Creating the probability array for Time 0
            double[][] probabilitiesT0 = extractProbabilities(node, statesNameArray.length);
            //Creating the probability array for Time T
            Node dynamic = extractChildren(root, "dynamic").get(0);
            Node dynamicNode = getNodeWithID(dynamic.getChildNodes(), nodeName);
            double[][] probabilitiesTT;
            if(dynamicNode == null) {
                probabilitiesTT = probabilitiesT0;
            } else {
                probabilitiesTT = extractProbabilities(dynamicNode, statesNameArray.length);
            }
            //----------Creating the Parents--------------
            List<io.dcbn.backend.graph.Node> parents;
            if (extractParentNodes(node, dcbnNodes) == null) {
                parents = EMPTY_NODE_LIST;
            } else {
                parents = extractParentNodes(node, dcbnNodes);
            }
            List<io.dcbn.backend.graph.Node> parentsTm1;
            if (dynamicNode == null) {
                parentsTm1 = EMPTY_NODE_LIST;
            } else {
                 parentsTm1 = extractParentNodes(dynamicNode, dcbnNodes);
            }

            //-----------Creating the node dependencies-------------
            NodeDependency timeZeroDependency = new NodeDependency(0, parents, EMPTY_NODE_LIST, probabilitiesT0); //TODO id
            NodeDependency timeTDependency = new NodeDependency(0, parents, parentsTm1, probabilitiesTT); // TODO id

            //-----Getting color of the node----------
            String color = extractChildren(nodeAttribute, "interior").get(0).getAttributes().getNamedItem("color").getNodeValue();

            //---------Creating the Node-----------
            dcbnNodes.add(new io.dcbn.backend.graph.Node(0, nodeName, timeZeroDependency, timeTDependency, color, null, StateType.BOOLEAN, ZERO_POSITION)); //TODO id and StateType and Position

        }
        int timeSlices = Integer.parseInt(root.getElementsByTagName("dynamic").item(0).getAttributes().getNamedItem("numslices").getNodeValue());
        return new Graph(0, file.getName().replace(".xdsl", ""), timeSlices, dcbnNodes);
    }

    /**
     * returns the {@link Node} with the given id
     * @param nodeList the List of node to search into
     * @param id the id to search after
     * @return the {@link Node} with the given id
     */
    private Node getNodeWithID(NodeList nodeList, String id) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).hasAttributes() && nodeList.item(i).getAttributes().getNamedItem(ID).getNodeValue().equals(id)) {
                return nodeList.item(i);
            }
        }
        return null;
    }

    private List<Node> extractChildren(Node node, String nodeName) {
        NodeList children = node.getChildNodes();
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeName().equals(nodeName)) {
                nodes.add(children.item(i));
            }
        }
        return nodes;
    }

    private double[][] extractProbabilities(Node node, int states) {
        List<Node> probabilities = extractChildren(node, "probabilities");
        String[] arrayOfProbs = probabilities.get(0).getTextContent().split(" ");
        double[][] condiProbTable = new double[arrayOfProbs.length / states][states];
        int indexOfOldArray = 0;
        for(int j = 0; j < condiProbTable.length; j++) {
            for(int k = 0; k < condiProbTable[j].length; k++) {
                condiProbTable[j][k] = Double.parseDouble(arrayOfProbs[indexOfOldArray]);
                indexOfOldArray++;
            }
        }
        return condiProbTable;
    }

    private List<io.dcbn.backend.graph.Node> extractParentNodes(Node node, List<io.dcbn.backend.graph.Node> existingDcbnNodes) {
        if (extractChildren(node, "parents").size() != 0) {
            String[] parentsT0TTString = extractChildren(node, "parents")
                    .get(0).getTextContent().split(" ");
            List<io.dcbn.backend.graph.Node> parentNodes = new ArrayList<>();
            for (String nodeName : parentsT0TTString) {
                parentNodes.add(findDcbnNodeByName(existingDcbnNodes, nodeName));
            }
            return parentNodes;
        }
        return null;
    }

    private io.dcbn.backend.graph.Node findDcbnNodeByName(List<io.dcbn.backend.graph.Node> nodes, String name) {
        List<io.dcbn.backend.graph.Node> nodeToReturn = nodes.stream()
                .filter(node -> node.getName().equals(name)).collect(Collectors.toList());
        return nodeToReturn.get(0);
    }
}
