package io.dcbn.backend.graph.converters;

import io.dcbn.backend.graph.Graph;
import io.dcbn.backend.graph.NodeDependency;
import io.dcbn.backend.graph.Position;
import io.dcbn.backend.graph.StateType;
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

        //Creating all nodes to avoid reference problems
        for (int i = 0; i < nodesFile.size(); i++) {
            Node node = nodesFile.get(i);
            String nodeID = node.getAttributes().getNamedItem(ID).getNodeValue();
            io.dcbn.backend.graph.Node dcbnNode = new io.dcbn.backend.graph.Node(nodeID, null,
                    null, null, null, null, ZERO_POSITION);
            dcbnNodes.add(dcbnNode);
        }

        for (int i = 0; i < nodesFile.size(); i++) {
            Node node = nodesFile.get(i);
            String nodeID = node.getAttributes().getNamedItem(ID).getNodeValue();
            Node nodeAttribute = getNodeWithID(nodesAttributes, nodeID);
            //------Setting the state for this node-------
            List<Node> states = extractChildren(node, "state");
            //copy states to array
            List<String> statesNameList = new ArrayList<>();
            int index = 0;
            states.forEach(state -> statesNameList.add(state.getAttributes().getNamedItem(ID).getNodeValue()));
            String[] statesNameArray = new String[states.size()];
            statesNameList.toArray(statesNameArray);
            StateType stateType;
            if (statesNameArray[0].equals("true") && statesNameArray[1].equals("false")) {
                stateType = StateType.BOOLEAN;
            } else {
                throw new IllegalArgumentException("The DBN in the document contains nodes that have different states" +
                        " as the only ones supported by the backend");
            }

            //-------Setting the probabilities for the node dependencies---------
            //Creating the probability array for Time 0
            double[][] probabilitiesT0 = extractProbabilities(node, statesNameArray.length);
            //Creating the probability array for Time T
            Node dynamic = extractChildren(root, "dynamic").get(0);
            Node dynamicNode = getNodeWithID(dynamic.getChildNodes(), nodeID);
            double[][] probabilitiesTT;
            if (dynamicNode == null) {
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
                if (extractParentNodes(dynamicNode, dcbnNodes) == null) {
                    parentsTm1 = EMPTY_NODE_LIST;
                } else {
                    parentsTm1 = extractParentNodes(dynamicNode, dcbnNodes);
                }
            }

            //-----------Creating the node dependencies-------------
            NodeDependency timeZeroDependency = new NodeDependency(parents, EMPTY_NODE_LIST, probabilitiesT0);
            NodeDependency timeTDependency = new NodeDependency(parents, parentsTm1, probabilitiesTT);

            //-----Getting color of the node----------
            String color = extractChildren(nodeAttribute, "interior")
                    .get(0).getAttributes().getNamedItem("color").getNodeValue();

            //--------Getting the position------------
            String[] positionsString = extractChildren(nodeAttribute, "position")
                    .get(0).getTextContent().split(" ");
            int[] positionInt = new int[positionsString.length];
            for(int j = 0; j < positionsString.length; j++) {
                positionInt[j] = Integer.parseInt(positionsString[j]);
            }
            Position position = new Position((positionInt[0] + positionInt[2]) / 2.0, ((positionInt[1] + positionInt[3]) / 2.0));

            //---------Creating the Node-----------
            io.dcbn.backend.graph.Node dcbnNode = findDcbnNodeByName(dcbnNodes, nodeID);
            dcbnNode.setTimeZeroDependency(timeZeroDependency);
            dcbnNode.setTimeTDependency(timeTDependency);
            dcbnNode.setColor(color);
            dcbnNode.setStateType(stateType);
            dcbnNode.setPosition(position);
        }
        int timeSlices = Integer.parseInt(root.getElementsByTagName("dynamic").item(0)
                .getAttributes().getNamedItem("numslices").getNodeValue());
        //--------Setting the real names for the nodes---------
        for (int i = 0; i < nodesFile.size(); i++) {
            Node node = nodesFile.get(i);
            String nodeID = node.getAttributes().getNamedItem(ID).getNodeValue();
            Node nodeAttribute = getNodeWithID(nodesAttributes, nodeID);
            String name = extractChildren(nodeAttribute, "name").get(0).getTextContent();
            findDcbnNodeByName(dcbnNodes, nodeID).setName(name);
        }
        String name = root.getElementsByTagName("genie").item(0).getAttributes().getNamedItem("name").getNodeValue();
        return new Graph(name, timeSlices, dcbnNodes);
    }

    /**
     * Returns the {@link Node} with the given id.
     *
     * @param nodeList the List of node to search into.
     * @param id       the id to search after.
     * @return the {@link Node} with the given id.
     */
    private Node getNodeWithID(NodeList nodeList, String id) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).hasAttributes()
                    && nodeList.item(i).getAttributes().getNamedItem(ID).getNodeValue().equals(id)) {
                return nodeList.item(i);
            }
        }
        return null;
    }

    /**
     * Returns the children of the {@link Node} that have the given tag. returns null if no children was found.
     *
     * @param node     the {@link Node} to inspect.
     * @param nodeName the tag of the {@link Node}.
     * @return the children of the {@link Node} that have the given tag. returns null if no children was found.
     */
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

    /**
     * This method extracts the text content of the "probabilities" child node of the given {@link Node}
     * and parses it to a corresponding double[][].
     *
     * @param node   the {@link Node} to inspect
     * @param states the number of states the {@link io.dcbn.backend.graph.Node} can have.
     * @return a corresponding double[][] of the probabilities.
     */
    private double[][] extractProbabilities(Node node, int states) {
        List<Node> probabilities = extractChildren(node, "probabilities");
        String[] arrayOfProbs = probabilities.get(0).getTextContent().split(" ");
        double[][] condiProbTable = new double[arrayOfProbs.length / states][states];
        int indexOfOldArray = 0;
        for (int j = 0; j < condiProbTable.length; j++) {
            for (int k = 0; k < condiProbTable[j].length; k++) {
                condiProbTable[j][k] = Double.parseDouble(arrayOfProbs[indexOfOldArray]);
                indexOfOldArray++;
            }
        }
        return condiProbTable;
    }

    /**
     * It returns the {@link List<io.dcbn.backend.graph.Node>} of parents described in the "parent" sub node of the given {@link Node}.
     *
     * @param node the {@link Node} to inspect.
     * @param existingDcbnNodes a {@link List<io.dcbn.backend.graph.Node>} of the already existing {@link io.dcbn.backend.graph.Node} objects.
     * @return
     */
    private List<io.dcbn.backend.graph.Node> extractParentNodes(Node node, List<io.dcbn.backend.graph.Node> existingDcbnNodes) {
        if (extractChildren(node, "parents").size() != 0) {
            String[] parentsT0TTString = extractChildren(node, "parents")
                    .get(0).getTextContent().split(" ");
            List<io.dcbn.backend.graph.Node> parentNodes = new ArrayList<>();
            //going backwards for the correct setting of the probabilities
            for (int i = parentsT0TTString.length - 1; i >= 0; i--) {
                parentNodes.add(findDcbnNodeByName(existingDcbnNodes, parentsT0TTString[i]));
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
