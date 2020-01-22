package io.dcbn.backend.core;

import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VesselCacheTest {

    private final int timeSteps = 5;

    VesselCache vesselCache;
    Vessel vessel;

    @BeforeEach
    public void setUp() {
        vesselCache = new VesselCache(timeSteps);
        vessel = new Vessel(System.currentTimeMillis(), "Vessel1");
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
            } else {
                assertNull(vesselCache.getVesselsByUuid(vessel.getUuid())[i]);
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

        assertNull(vesselCache.getVesselsByUuid(vessel.getUuid())[2]);
        assertFalse(vesselCache.getVesselsByUuid(vessel.getUuid())[1].isFiller());
        assertTrue(vesselCache.getVesselsByUuid(vessel.getUuid())[0].isFiller());
    }
}
