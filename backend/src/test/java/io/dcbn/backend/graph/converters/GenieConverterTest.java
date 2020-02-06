package io.dcbn.backend.graph.converters;

import io.dcbn.backend.graph.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenieConverterTest {

    private static final String RESOURCE_PATH = "src/test/resources";
    private static final int NUM_TIME_SLICES = 5;
    private static final Position ZERO_POSITION = new Position(0.0, 0.0);


    private Graph graph;
    private AmidstGraphAdapter adapter;
    private File genieFile;
    private GenieConverter genieConverter;

    @BeforeEach
    public void setUp() {
        Node smuggling = new Node("smuggling", null, null, "#e5f6f7", null, StateType.BOOLEAN,
                new Position(100.0, 100.0));
        Node nullSpeed = new Node("nullSpeed", null, null, "#e5f6f7",
                "nullSpeed", StateType.BOOLEAN,  new Position(300.0, 300.0));
        Node inTrajectoryArea = new Node("inTrajectoryArea", null, null, "#e5f6f7",
                "inTrajectory", StateType.BOOLEAN,  new Position(500.0, 500.0));
        Node isInReportedArea = new Node("isInReportedArea", null, null, "#e5f6f7",
                "inArea", StateType.BOOLEAN,  new Position(1000.0, 1000.0));

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

        graph = new Graph(0, "smuggling", NUM_TIME_SLICES,
                Arrays.asList(smuggling, nullSpeed, inTrajectoryArea, isInReportedArea));
        adapter = new AmidstGraphAdapter(graph);
        genieFile = new File(RESOURCE_PATH + "/networks/genie/Test2.xdsl");
        genieConverter = new GenieConverter();


    }

    @Test
    public void testGenieToDcbn() throws IOException, SAXException, ParserConfigurationException {
        Graph convertedGraph = genieConverter.fromGenieToDcbn(new FileInputStream(genieFile));
        AmidstGraphAdapter convertedAdapter = new AmidstGraphAdapter(convertedGraph);
//        System.out.println(adapter.getDbn());
//        System.out.println("---------------------");
//        System.out.println(convertedAdapter.getDbn());
        assertTrue(adapter.getDbn().equalDBNs(convertedAdapter.getDbn(), 0));
    }

    @Test
    public void testDcbnToGenie() throws ParserConfigurationException, TransformerException, IOException, SAXException {
//        InputStream targetStream = new FileInputStream(genieFile);
//        File file = genieConverter.fromDcbnToGenie(genieConverter.fromGenieToDcbn(targetStream));
//        File tempDir = new File("target/tempTestFiles/");
//        tempDir.mkdir();
//        File destination = new File("target/tempTestFiles/" + file.getName());
//        file.renameTo(destination);
//        assertTrue(file.getName().endsWith(".xdsl"));
    }
}
