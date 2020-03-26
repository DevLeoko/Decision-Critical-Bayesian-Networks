package io.dcbn.backend.graph.converters;

import io.dcbn.backend.graph.Graph;
import io.dcbn.backend.graph.NodeDependency;
import io.dcbn.backend.graph.Position;
import io.dcbn.backend.graph.StateType;
import io.dcbn.backend.utils.Pair;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is a converter for the Genie file format (.xdsl)
 */
@NoArgsConstructor
public class GenieConverter {

    private static final String ID = "id";
    private static final List<io.dcbn.backend.graph.Node> EMPTY_NODE_LIST = new ArrayList<>();
    private static final Position ZERO_POSITION = new Position(0.0, 0.0);
    private static final String DYNAMIC = "dynamic";
    private static final String COLOR = "color";
    private static final String PARENT = "parents";


    /**
     * This method takes a .xdsl file (in the genie format) and converts it to a {@link Graph}.
     *
     * @param file the .xdsl file (in the genie format).
     * @return the converted {@link Graph}.
     */
    public Graph fromGenieToDcbn(InputStream file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);
        doc.getDocumentElement().normalize();
        Element root = doc.getDocumentElement();
        List<Node> nodesFile = extractChildren(root.getElementsByTagName("nodes").item(0), "cpt");
        NodeList nodesAttributes = root.getElementsByTagName("node");
        List<io.dcbn.backend.graph.Node> dcbnNodes = new ArrayList<>();

        //Creating all nodes to avoid reference problems
        for (Node node : nodesFile) {
            String nodeID = node.getAttributes().getNamedItem(ID).getNodeValue();
            io.dcbn.backend.graph.Node dcbnNode = new io.dcbn.backend.graph.Node(nodeID, null,
                    null, null, null, null, ZERO_POSITION);
            dcbnNodes.add(dcbnNode);
        }

        for (Node node : nodesFile) {
            String nodeID = node.getAttributes().getNamedItem(ID).getNodeValue();
            Node nodeAttribute = getNodeWithID(nodesAttributes, nodeID);
            //------Setting the state for this node-------
            List<Node> states = extractChildren(node, "state");
            //copy states to array
            List<String> statesNameList = new ArrayList<>();
            states.forEach(state -> statesNameList.add(state.getAttributes().getNamedItem(ID).getNodeValue()));
            String[] statesNameArray = new String[states.size()];
            statesNameList.toArray(statesNameArray);
            StateType stateType;
            if (statesNameArray[0].equals("false") && statesNameArray[1].equals("true")) {
                stateType = StateType.INVERTED_BOOLEAN;
            } else if (statesNameArray.length == 2) {
                stateType = StateType.BOOLEAN;
            } else {
                throw new IllegalArgumentException("The DBN in the document contains nodes that have different states" +
                        " as the only ones supported by the backend");
            }

            //-------Setting the probabilities for the node dependencies---------
            //Creating the probability array for Time 0
            double[][] probabilitiesT0 = extractProbabilities(node, statesNameArray.length);
            //Creating the probability array for Time T
            Node dynamicNode;
            final List<Node> dynamicNodes = extractChildren(root, DYNAMIC);
            if (dynamicNodes.isEmpty()
                    || dynamicNodes.get(0).getChildNodes().toString().equals("[dynamic: null]")) {
                dynamicNode = null;
            } else {
                dynamicNode = getNodeWithID(dynamicNodes.get(0).getChildNodes(), nodeID);
            }
            double[][] probabilitiesTT;
            if (dynamicNode == null) {
                probabilitiesTT = probabilitiesT0;
            } else {
                probabilitiesTT = extractProbabilities(dynamicNode, statesNameArray.length);
            }
            //----------Creating the Parents--------------
            List<io.dcbn.backend.graph.Node> parents;
            extractParentNodes(node, dcbnNodes);
            parents = extractParentNodes(node, dcbnNodes);
            List<io.dcbn.backend.graph.Node> parentsTm1;
            if (dynamicNode == null) {
                parentsTm1 = EMPTY_NODE_LIST;
            } else {
                extractParentNodes(dynamicNode, dcbnNodes);
                parentsTm1 = extractParentNodes(dynamicNode, dcbnNodes);
            }

            //-----------Creating the node dependencies-------------
            NodeDependency timeZeroDependency = new NodeDependency(parents, EMPTY_NODE_LIST, probabilitiesT0);
            NodeDependency timeTDependency = new NodeDependency(parents, parentsTm1, probabilitiesTT);

            //-----Getting color of the node----------
            String color = extractChildren(nodeAttribute, "interior")
                    .get(0).getAttributes().getNamedItem(COLOR).getNodeValue();
            //--------Getting the position------------
            String[] positionsString = extractChildren(nodeAttribute, "position")
                    .get(0).getTextContent().split(" ");
            int[] positionInt = new int[positionsString.length];
            for (int j = 0; j < positionsString.length; j++) {
                positionInt[j] = Integer.parseInt(positionsString[j]);
            }
            Position position = new Position((positionInt[0] + positionInt[2]) / 2.0, ((positionInt[1] + positionInt[3]) / 2.0));

            //---------Creating the Node-----------
            io.dcbn.backend.graph.Node dcbnNode = findDcbnNodeByName(dcbnNodes, nodeID);
            dcbnNode.setTimeZeroDependency(timeZeroDependency);
            dcbnNode.setTimeTDependency(timeTDependency);
            dcbnNode.setColor("#" + color);
            dcbnNode.setStateType(stateType);
            dcbnNode.setPosition(position);
        }
        int timeSlices;
        Node dynamicItem = root.getElementsByTagName(DYNAMIC).item(0);
        if (dynamicItem == null) {
            timeSlices = 1;
        } else {
            timeSlices = Integer.parseInt(dynamicItem.getAttributes().getNamedItem("numslices").getNodeValue());
        }
        //--------Setting the real names for the nodes---------
        for (Node node : nodesFile) {
            String nodeID = node.getAttributes().getNamedItem(ID).getNodeValue();
            Node nodeAttribute = getNodeWithID(nodesAttributes, nodeID);
            String name = extractChildren(nodeAttribute, "name").get(0).getTextContent();
            findDcbnNodeByName(dcbnNodes, nodeID).setName(name);
        }
        String name = root.getElementsByTagName("genie").item(0).getAttributes().getNamedItem("name").getNodeValue();
        return new Graph(name, timeSlices, dcbnNodes);
    }

    public String fromDcbnToGenie(Graph graph) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        documentFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        document.setXmlVersion("1.0");

        //Creating structure of document
        Element root = document.createElement("smile");
        root.setAttribute("version", "1.0");
        root.setAttribute("id", "Network1");
        root.setAttribute("numsamples", "10000");
        root.setAttribute("discsamples", "10000");
        document.appendChild(root);
        Element nodesElement = document.createElement("nodes");
        root.appendChild(nodesElement);
        Element dynamicElement = document.createElement(DYNAMIC);
        dynamicElement.setAttribute("numslices", "" + graph.getTimeSlices());
        root.appendChild(dynamicElement);
        Element extensionsElement = document.createElement("extensions");
        root.appendChild(extensionsElement);
        Element genieElement = document.createElement("genie");
        genieElement.setAttribute("version", "1.0");
        genieElement.setAttribute("app", "GeNIe 2.2.2601.0 ACADEMIC");
        genieElement.setAttribute("name", graph.getName());
        genieElement.setAttribute("faultnameformat", "nodestate");
        extensionsElement.appendChild(genieElement);
        Element plateElement = document.createElement("plate");
        plateElement.setAttribute("leftwidth", "120");
        plateElement.setAttribute("rightwidth", "120");
        Position platePosition = findOutMaxPos(graph.getNodes());
        int plateX = platePosition.getX().intValue() + 200; //200 is default offset
        int plateY = platePosition.getY().intValue() + 200; //200 is default offset
        plateElement.setTextContent("0 0 " + plateX + " " + plateY);
        genieElement.appendChild(plateElement);

        List<io.dcbn.backend.graph.Node> sortedNodes = sortNodesAfterNumberOfParents(graph);

        for (io.dcbn.backend.graph.Node node : sortedNodes) {
            String nodeID = node.getName().replace(" ", "_");

            //------------Creating node attribute----------------
            Element nodeElement = document.createElement("node");
            nodeElement.setAttribute("id", nodeID);
            genieElement.appendChild(nodeElement);
            //Setting the name
            Element nameElement = document.createElement("name");
            nameElement.setTextContent(node.getName());
            nodeElement.appendChild(nameElement);
            //Setting the color
            Element interiorElement = document.createElement("interior");
            interiorElement.setAttribute(COLOR, node.getColor().replace("#", ""));
            nodeElement.appendChild(interiorElement);
            //Setting default outline color
            Element outlineElement = document.createElement("outline");
            outlineElement.setAttribute(COLOR, "000080");
            nodeElement.appendChild(outlineElement);
            //Setting default font settings
            Element fontElement = document.createElement("font");
            fontElement.setAttribute(COLOR, "000000");
            fontElement.setAttribute("name", "Arial");
            fontElement.setAttribute("size", "8");
            nodeElement.appendChild(fontElement);
            //Setting position with default size
            Element positionElement = document.createElement("position");
            double nodeX = node.getPosition().getX();
            double nodeY = node.getPosition().getY();
            //default sizes
            double xTopL = nodeX + 24;
            double xBotR = nodeX - 24;
            double yTopL = nodeY + 15;
            double yBotR = nodeY - 15;
            positionElement.setTextContent((int) xTopL + " " + (int) yTopL + " " + (int) xBotR + " " + (int) yBotR);
            nodeElement.appendChild(positionElement);
            //Setting default barchart settings
            Element barchartElement = document.createElement("barchart");
            barchartElement.setAttribute("active", "true");
            barchartElement.setAttribute("width", "128");
            barchartElement.setAttribute("height", "78");
            nodeElement.appendChild(barchartElement);

            //-------------Creating the Conditional Probability Tables--------------------
            //----For Time 0----
            NodeDependency timeZeroDependency = node.getTimeZeroDependency();
            Element cptElement = document.createElement("cpt");
            //Creating cpt element
            cptElement.setAttribute("id", nodeID);
            cptElement.setAttribute(DYNAMIC, "plate");
            nodesElement.appendChild(cptElement);
            //Setting states
            for (String state : node.getStateType().getStates()) {
                Element stateElement = document.createElement("state");
                stateElement.setAttribute("id", state);
                cptElement.appendChild(stateElement);
            }
            //Setting the probabilities
            createProbString(document, cptElement, timeZeroDependency);
            //Setting the parents
            StringBuilder parentsString = new StringBuilder();
            if (!timeZeroDependency.getParents().isEmpty()) {
                for (io.dcbn.backend.graph.Node parent : timeZeroDependency.getParents()) {
                    parentsString.append(parent.getName().replace(" ", "_")).append(" ");
                }
                Element parentsElement = document.createElement(PARENT);
                parentsElement.setTextContent(parentsString.toString());
                cptElement.appendChild(parentsElement);
            }
            //-----For Time T------
            NodeDependency timeTDependency = node.getTimeTDependency();
            if (!timeTDependency.getParentsTm1().isEmpty()) {
                //Creating the cpt element
                Element cptElementTime = document.createElement("cpt");
                cptElementTime.setAttribute("id", nodeID);
                cptElementTime.setAttribute("order", "1");
                dynamicElement.appendChild(cptElementTime);
                //Setting the parents
                StringBuilder parentsStringTime = new StringBuilder();
                for (io.dcbn.backend.graph.Node parent : timeTDependency.getParentsTm1()) {
                    parentsStringTime.append(parent.getName().replace(" ", "_")).append(" ");
                }
                Element parentsElementTime = document.createElement(PARENT);
                parentsElementTime.setTextContent(parentsStringTime.toString());
                cptElementTime.appendChild(parentsElementTime);
                //Setting the probabilities
                createProbString(document, cptElementTime, timeTDependency);
            }
        }

        StringWriter sw = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        transformer.transform(new DOMSource(document), new StreamResult(sw));
        return sw.toString();

    }

    /**
     * This method sorts a {@link List<io.dcbn.backend.graph.Node>} by the number
     * of parents (direct and indirectly linked)
     *
     * @param graph The graph containing the nodes
     * @return the {@link List<io.dcbn.backend.graph.Node>} sorted by the number
     * parents (direct and indirectly linked)
     */
    private List<io.dcbn.backend.graph.Node> sortNodesAfterNumberOfParents(Graph graph) {
        List<io.dcbn.backend.graph.Node> nodes = graph.getNodes();
        List<Pair<io.dcbn.backend.graph.Node, Integer>> sortedNodePair = new ArrayList<>();
        sortedNodePair.add(new Pair<>(nodes.get(0), getNumberOfParents(nodes.get(0))));
        for (int j = 1; j < nodes.size(); j++) {
            int numberOfParents = getNumberOfParents(nodes.get(j));
            boolean inserted = false;
            for (int i = 0; i < sortedNodePair.size(); i++) {
                if (sortedNodePair.get(i).getValue() > numberOfParents) {
                    sortedNodePair.add(i, new Pair<>(nodes.get(j), numberOfParents));
                    inserted = true;
                    break;
                }
            }
            if (!inserted) {
                sortedNodePair.add(new Pair<>(nodes.get(j), numberOfParents));
            }

        }
        List<io.dcbn.backend.graph.Node> sortedNodes = new ArrayList<>();
        for (Pair<io.dcbn.backend.graph.Node, Integer> pair : sortedNodePair) {
            sortedNodes.add(pair.getKey());
        }
        return sortedNodes;
    }

    /**
     * This method returns the number of parents (direct and indirect) of th given {@link io.dcbn.backend.graph.Node}
     *
     * @param node the {@link io.dcbn.backend.graph.Node}
     * @return returns the number of parents (direct and indirect)
     */
    private int getNumberOfParents(io.dcbn.backend.graph.Node node) {
        List<io.dcbn.backend.graph.Node> queue = new ArrayList<>();
        int numberOfParents = 0;
        queue.add(node);
        while (!queue.isEmpty()) {
            io.dcbn.backend.graph.Node actualNode = queue.get(0);
            List<io.dcbn.backend.graph.Node> parents = actualNode.getTimeTDependency().getParents();
            numberOfParents += parents.size();
            for (io.dcbn.backend.graph.Node parent : parents) {
                if (!queue.contains(parent)) {
                    queue.add(parent);
                }
            }

            queue.remove(0);
        }
        return numberOfParents;
    }

    /**
     * This returns a new {@link Position} with the maximum x and y value of the nodes {@link Position}
     *
     * @param nodes the {@link List<io.dcbn.backend.graph.Node>} to inspect
     * @return A new {@link Position} with the maximum x and y value of the nodes {@link Position}
     */
    private Position findOutMaxPos(List<io.dcbn.backend.graph.Node> nodes) {
        double maxX = 0;
        double maxY = 0;
        for (io.dcbn.backend.graph.Node node : nodes) {
            Position position = node.getPosition();
            if (position.getX() > maxX) {
                maxX = position.getX();
            }
            if (position.getY() > maxY) {
                maxY = position.getY();
            }
        }
        return new Position(maxX, maxY);
    }

    /**
     * Creates the probability string to insert to the {@link Document}.
     *
     * @param document        the {@link Document}
     * @param cptElement      the {@link Element} the probabilities belongs to.
     * @param timeTDependency the {@link NodeDependency} where the probabilities are defined.
     */
    private void createProbString(Document document, Element cptElement, NodeDependency timeTDependency) {
        StringBuilder probabilitiesStringTime = new StringBuilder();
        double[][] probabilitiesTime = timeTDependency.getProbabilities();
        for (double[] doubles : probabilitiesTime) {
            for (double aDouble : doubles) {
                probabilitiesStringTime.append(aDouble).append(" ");
            }
        }
        Element probabilitiesElementTime = document.createElement("probabilities");
        probabilitiesElementTime.setTextContent(probabilitiesStringTime.toString());
        cptElement.appendChild(probabilitiesElementTime);
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
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Import failed (ExtractChildren returned null, Node not found)");
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
     * @param node              the {@link Node} to inspect.
     * @param existingDcbnNodes a {@link List<io.dcbn.backend.graph.Node>} of the already existing {@link io.dcbn.backend.graph.Node} objects.
     * @return the {@link List<io.dcbn.backend.graph.Node>} of parents described in the "parent" sub node of the given {@link Node}.
     */
    private List<io.dcbn.backend.graph.Node> extractParentNodes(Node node, List<io.dcbn.backend.graph.Node> existingDcbnNodes) {
        if (!extractChildren(node, PARENT).isEmpty()) {
            String[] parentsT0TTString = extractChildren(node, PARENT)
                    .get(0).getTextContent().split(" ");
            List<io.dcbn.backend.graph.Node> parentNodes = new ArrayList<>();
            for (String s : parentsT0TTString) {
                parentNodes.add(findDcbnNodeByName(existingDcbnNodes, s));
            }
            return parentNodes;
        }
        return new ArrayList<>();
    }

    /**
     * Returns the {@link io.dcbn.backend.graph.Node} with the given name.
     *
     * @param nodes the {@link List<io.dcbn.backend.graph.Node>} to search into
     * @param name  the name to search after.
     * @return The {@link io.dcbn.backend.graph.Node} with the given name.
     */
    private io.dcbn.backend.graph.Node findDcbnNodeByName(List<io.dcbn.backend.graph.Node> nodes, String name) {
        List<io.dcbn.backend.graph.Node> nodeToReturn = nodes.stream()
                .filter(node -> node.getName().equals(name)).collect(Collectors.toList());
        return nodeToReturn.get(0);
    }
}
