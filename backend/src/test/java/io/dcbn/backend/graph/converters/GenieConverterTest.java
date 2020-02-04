package io.dcbn.backend.graph.converters;

import io.dcbn.backend.graph.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class GenieConverterTest {

    private static final String RESOURCE_PATH = "src/test/resources";
    private static final int NUM_TIME_SLICES = 5;
    private static final Position ZERO_POSITION = new Position(0, 0);


    private Graph graph;
    private AmidstGraphAdapter adapter;
    private File genieFile;
    private GenieConverter genieConverter;

    @BeforeEach
    public void setUp() {
        Node smuggling = new Node("Smuggling", null, null, "#e5f6f7", null, StateType.BOOLEAN,
                new Position(100, 100));
        Node nullSpeed = new Node("Null Speed", null, null, "#e5f6f7",
                "nullSpeed", StateType.BOOLEAN, new Position(300, 300));
        Node inTrajectoryArea = new Node("In Trajectory Area", null, null, "#e5f6f7",
                "inTrajectory", StateType.BOOLEAN, new Position(500, 500));
        Node isInReportedArea = new Node("Is in Reported Area", null, null, "#e5f6f7",
                "inArea", StateType.BOOLEAN, new Position(1000, 1000));

        List<Node> smugglingParentsList = Arrays.asList(nullSpeed, inTrajectoryArea, isInReportedArea);
        double[][] probabilities = {{0.8, 0.2}, {0.6, 0.4}, {0.4, 0.6}, {0.4, 0.6}, {0.2, 0.8},
                {0.2, 0.8}, {0.001, 0.999}, {0.001, 0.999}};
        NodeDependency smuggling0Dep = new NodeDependency(smugglingParentsList,
                new ArrayList<>(), probabilities);
        NodeDependency smugglingTDep = new NodeDependency(smugglingParentsList, new ArrayList<>(),
                probabilities);
        smuggling.setTimeZeroDependency(smuggling0Dep);
        smuggling.setTimeTDependency(smugglingTDep);

        NodeDependency nS0Dep = new NodeDependency(new ArrayList<>(), new ArrayList<>(),
                new double[][]{{0.7, 0.3}});
        NodeDependency nSTDep = new NodeDependency(new ArrayList<>(), new ArrayList<>(),
                new double[][]{{0.7, 0.3}});
        nullSpeed.setTimeZeroDependency(nS0Dep);
        nullSpeed.setTimeTDependency(nSTDep);

        NodeDependency iTA0Dep = new NodeDependency(new ArrayList<>(), new ArrayList<>(),
                new double[][]{{0.8, 0.2}});
        NodeDependency iTATDep = new NodeDependency(new ArrayList<>(), new ArrayList<>(),
                new double[][]{{0.8, 0.2}});
        inTrajectoryArea.setTimeZeroDependency(iTA0Dep);
        inTrajectoryArea.setTimeTDependency(iTATDep);

        NodeDependency iIRA0Dep = new NodeDependency(new ArrayList<>(), new ArrayList<>(),
                new double[][]{{0.8, 0.2}});
        NodeDependency iIRATDep = new NodeDependency(new ArrayList<>(), new ArrayList<>(),
                new double[][]{{0.8, 0.2}});
        isInReportedArea.setTimeZeroDependency(iIRA0Dep);
        isInReportedArea.setTimeTDependency(iIRATDep);

        graph = new Graph(0, "Smuggling", NUM_TIME_SLICES,
                Arrays.asList(nullSpeed, inTrajectoryArea, isInReportedArea, smuggling));
        adapter = new AmidstGraphAdapter(graph);
        genieFile = new File(RESOURCE_PATH + "/networks/genie/smuggling2.xdsl");
        genieConverter = new GenieConverter();


    }

    @Test
    public void testGenieToDcbn() throws IOException, SAXException, ParserConfigurationException {
        Graph convertedGraph = genieConverter.fromGenieToDcbn(new FileInputStream(genieFile));
        AmidstGraphAdapter convertedAdapter = new AmidstGraphAdapter(convertedGraph);
        assertEquals(adapter.getDbn().toString(), convertedAdapter.getDbn().toString());
    }

    @Test
    public void testDcbnToGenie() throws ParserConfigurationException, TransformerException, IOException, SAXException {
        new File("target/tempTestFiles").mkdir();
        File convertedXMLFile = new File("target/tempTestFiles/generatedXML.xdsl");
        BufferedWriter writer = new BufferedWriter(new FileWriter(convertedXMLFile));
        writer.write(genieConverter.fromDcbnToGenie(graph));
        writer.close();
        //Copying the original File
        File copiedOriginalXml = new File("target/tempTestFiles/copiedOriginalXml.xdsl");
        try (
                InputStream in = new BufferedInputStream(
                        new FileInputStream(genieFile));
                OutputStream out = new BufferedOutputStream(
                        new FileOutputStream(copiedOriginalXml))) {

            byte[] buffer = new byte[1024];
            int lengthRead;
            while ((lengthRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, lengthRead);
                out.flush();
            }
        }

        Diff diff = DiffBuilder.compare(copiedOriginalXml)
                .withTest(convertedXMLFile)
                .checkForSimilar()
                .ignoreComments() // [2]
                .ignoreWhitespace()
                .normalizeWhitespace()// [4]
                .withComparisonFormatter(new DefaultComparisonFormatter()) // [6]
                .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndAttributes("id")))
                .build();
        assertFalse(diff.hasDifferences(), diff.toString());
    }
}
