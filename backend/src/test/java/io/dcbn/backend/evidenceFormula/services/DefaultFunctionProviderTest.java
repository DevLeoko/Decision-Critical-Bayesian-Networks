package io.dcbn.backend.evidenceFormula.services;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import de.fraunhofer.iosb.iad.maritime.datamodel.AreaOfInterest;
import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import de.fraunhofer.iosb.iad.maritime.datamodel.VesselType;
import io.dcbn.backend.core.AoiCache;
import io.dcbn.backend.core.VesselCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultFunctionProviderTest {

    DefaultFunctionProvider provider;
    VesselCache vesselCache;
    AoiCache aoiCache;
    Vessel vessel;

    @BeforeEach
    public void setUp() {
        this.vesselCache = new VesselCache(5);
        vessel = new Vessel("coord00", 0);
        vessel.setLongitude(0.0);
        vessel.setLatitude(0.0);
        vessel.setVesselType(VesselType.CARGO);
        vesselCache.insert(vessel);
        Vessel vessel2 = new Vessel("coord30", 0);
        vessel2.setLongitude(3.0);
        vessel2.setLatitude(0.0);
        vessel2.setVesselType(VesselType.CARGO);
        vesselCache.insert(vessel2);
        Vessel vessel3 = new Vessel("coord20", 0);
        vessel3.setLongitude(2.0);
        vessel3.setLatitude(0.0);
        vessel3.setVesselType(VesselType.FISHING_VESSEL);
        vesselCache.insert(vessel3);

        this.aoiCache = new AoiCache();
        Coordinate[] coords = new Coordinate[5];
        coords[0] = new Coordinate(0,0);
        coords[1] = new Coordinate(5,0);
        coords[2] = new Coordinate(5,5);
        coords[3] = new Coordinate(0,5);
        coords[4] = new Coordinate(0,0);
        CoordinateSequence coordSequence = new CoordinateArraySequence(coords);
        LinearRing lRing = new LinearRing(coordSequence, new GeometryFactory());
        Polygon pol = new Polygon(lRing, null, new GeometryFactory());
        aoiCache.insert("aoi1", new AreaOfInterest("aoi1", pol));

        Coordinate[] coords2 = new Coordinate[5];
        coords2[0] = new Coordinate(20,20);
        coords2[1] = new Coordinate(25,20);
        coords2[2] = new Coordinate(25,25);
        coords2[3] = new Coordinate(20,25);
        coords2[4] = new Coordinate(20,20);
        CoordinateSequence coordSequence2 = new CoordinateArraySequence(coords2);
        LinearRing lRing2 = new LinearRing(coordSequence2, new GeometryFactory());
        Polygon pol2 = new Polygon(lRing2, null, new GeometryFactory());
        aoiCache.insert("aoi2", new AreaOfInterest("aoi2", pol2));

        provider = new DefaultFunctionProvider(vesselCache, aoiCache);
        provider.currentVessel = new Vessel("Vessle1", 0);
    }

    @Test
    public void inAreaTest() {
        vessel.setLatitude(1.0);
        vessel.setLongitude(1.0);
        provider.setCurrentVessel(vessel);
        List<Object> params = new ArrayList<>();
        params.add("aoi1");
        assertTrue((boolean) provider.call("inArea", params));
        params.clear();
        params.add("aoi2");
        assertFalse((boolean) provider.call("inArea", params));
    }

    @Test
    public void distanceToNearestTest() {
        provider.setCurrentVessel(vessel);
        provider.setCurrentTimeSlice(0);
        List<Object> params = new ArrayList<>();
        assertEquals(2.0, (double) provider.call("distanceToNearest", params));
    }

    @Test
    public void distanceToNearestTypeTest() {
        provider.setCurrentVessel(vessel);
        provider.setCurrentTimeSlice(0);
        List<Object> params = new ArrayList<>();
        params.add(VesselType.CARGO.toString());
        assertEquals(3.0, (double) provider.call("distanceToNearestType", params));
    }
}
