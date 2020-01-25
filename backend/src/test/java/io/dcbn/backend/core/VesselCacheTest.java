package io.dcbn.backend.core;

import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class VesselCacheTest {

    private final int timeSteps = 5;

    VesselCache vesselCache;
    Vessel vessel;
    Vessel vesselTwo;

    @BeforeEach
    public void setUp() {
        vesselCache = new VesselCache(timeSteps);
        vessel = new Vessel("Vessel1", System.currentTimeMillis());
        vesselTwo = new Vessel("Vessel2", System.currentTimeMillis());
    }

    @Test void getAllUuidsWithEmptyCacheTest() {
        assert(vesselCache.getAllVesselUuids().size() == 0);
    }

    @Test void getAllUuidsTest() {
        vesselCache.insert(vessel);
        vesselCache.insert(vesselTwo);
        Set<String> uuids = vesselCache.getAllVesselUuids();
        assert(uuids.contains(vessel.getUuid()));
        assert(uuids.contains(vesselTwo.getUuid()));
        assert(uuids.size() == 2);
    }

    @Test
    public void insertOneVesselTest() {
        vesselCache.insert(vessel);
        assert(vesselCache.getVesselsByUuid(vessel.getUuid())[0].getUuid().equals("Vessel1"));
    }

    @Test
    public void insertThreeInSameTimeSliceTest() {
        vessel.setSpeed(0.0);
        vesselCache.insert(vessel);
        vessel = Vessel.copy(vessel);
        vessel.setSpeed(1.0);
        vesselCache.insert(vessel);
        vessel = Vessel.copy(vessel);
        vessel.setSpeed(0.5);
        vesselCache.insert(vessel);

        assert(vesselCache.getVesselsByUuid(vessel.getUuid())[0].getSpeed() == 0.5);
    }

    @Test
    public void insertTwoInSameTimeSliceTest() {
        vessel.setSpeed(0.0);
        vesselCache.insert(vessel);
        vessel = Vessel.copy(vessel);
        vessel.setSpeed(1.0);
        vesselCache.insert(vessel);

        for (int i = 0; i < vesselCache.getVesselsByUuid(vessel.getUuid()).length; i++) {
            if(i == 0) {
                assert(vesselCache.getVesselsByUuid(vessel.getUuid())[0].getSpeed() == 1.0);
                assertFalse(vesselCache.getVesselsByUuid(vessel.getUuid())[0].isFiller());
            } else {
                assert(vesselCache.getVesselsByUuid(vessel.getUuid())[i].getSpeed() == 0.0);
                assertTrue(vesselCache.getVesselsByUuid(vessel.getUuid())[i].isFiller());
            }
        }
    }

    @Test
    public void numberOfTimeStepsTest() {
        vesselCache.insert(vessel);
        assert(vesselCache.getVesselsByUuid(vessel.getUuid()).length == timeSteps);
    }

    @Test
    public void insertNullTest() {
        assertThrows(IllegalArgumentException.class, () -> vesselCache.insert(null));
    }

    @Test
    public void getNullUuidTest() {
        assertNull(vesselCache.getVesselsByUuid("hello"));
    }

    @Test
    public void updateTimeSlicesTest() {
        vessel.setSpeed(4.0);
        vesselCache.insert(vessel);
        vesselCache.updateTimeSlices();
        vessel = Vessel.copy(vessel);
        vessel.setSpeed(3.0);
        vesselCache.insert(vessel);
        vesselCache.updateTimeSlices();
        vessel = Vessel.copy(vessel);
        vessel.setSpeed(2.0);
        vesselCache.insert(vessel);
        vesselCache.updateTimeSlices();
        vessel = Vessel.copy(vessel);
        vessel.setSpeed(1.0);
        vesselCache.insert(vessel);
        vesselCache.updateTimeSlices();
        vessel = Vessel.copy(vessel);
        vessel.setSpeed(0.0);
        vesselCache.insert(vessel);

        for (int i = 0; i < vesselCache.getVesselsByUuid(vessel.getUuid()).length; i++) {
            assert(vesselCache.getVesselsByUuid(vessel.getUuid())[i].getSpeed() == (double) i);
        }

        vesselCache.updateTimeSlices();
        vessel = Vessel.copy(vessel);
        vessel.setSpeed(10.0);
        vesselCache.insert(vessel);
        assert(vesselCache.getVesselsByUuid(vessel.getUuid())[0].getSpeed() == 10.0);
        assert(vesselCache.getVesselsByUuid(vessel.getUuid())[timeSteps - 1].getSpeed() == 3.0);
    }

    @Test
    public void refreshCacheAndDeleteTest() {
        assertNull(vesselCache.getVesselsByUuid(vessel.getUuid()));
        vessel.setSpeed(1.0);
        vesselCache.insert(vessel);
        assertNotNull(vesselCache.getVesselsByUuid(vessel.getUuid()));
        for (int i = 0; i < timeSteps; i++) {
            vesselCache.updateTimeSlices();
        }
        vesselCache.refreshCache();
        assertNull(vesselCache.getVesselsByUuid(vessel.getUuid()));
    }

    @Test
    public void fillerFlagTest() {
        vessel.setSpeed(1.0);
        vesselCache.insert(vessel);
        vesselCache.updateTimeSlices();
        assert(vesselCache.getVesselsByUuid(vessel.getUuid())[1].getSpeed() == 1.0);
        assertFalse(vesselCache.getVesselsByUuid(vessel.getUuid())[1].isFiller());
        assert(vesselCache.getVesselsByUuid(vessel.getUuid())[0].getSpeed() == 1.0);
        assertTrue(vesselCache.getVesselsByUuid(vessel.getUuid())[0].isFiller());
    }

    @Test
    public void refreshCacheAndDontDeleteTest() {
        assertNull(vesselCache.getVesselsByUuid(vessel.getUuid()));
        vessel.setSpeed(1.0);
        vesselCache.insert(vessel);
        assertNotNull(vesselCache.getVesselsByUuid(vessel.getUuid()));
        vesselCache.updateTimeSlices();
        vesselCache.refreshCache();

        assertTrue(vesselCache.getVesselsByUuid(vessel.getUuid())[2].isFiller());
        assertFalse(vesselCache.getVesselsByUuid(vessel.getUuid())[1].isFiller());
        assertTrue(vesselCache.getVesselsByUuid(vessel.getUuid())[0].isFiller());
    }
}
