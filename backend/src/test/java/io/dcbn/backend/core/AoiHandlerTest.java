package io.dcbn.backend.core;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import de.fraunhofer.iosb.iad.maritime.datamodel.AreaOfInterest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AoiHandlerTest {

    private AoiHandler handler;
    private AoiCache cache;

    @BeforeEach
    public void setUp() {
        cache = new AoiCache();
        handler = new AoiHandler(cache);
    }

    @Test
    public void handleTest() {
        AreaOfInterest aoi = new AreaOfInterest("aoi1", new Point(null, new GeometryFactory()));
        aoi.setName("AoiName");
        handler.handleAoi(aoi);

        assertTrue(cache.getAllAois().contains(aoi));
    }
}
