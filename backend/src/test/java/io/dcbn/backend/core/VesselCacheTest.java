package io.dcbn.backend.core;

import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class VesselCacheTest {

    private final int timeSteps = 5;

    private VesselCache vesselCache;
    private Vessel vessel;
    private Vessel vesselTwo;

    @BeforeEach
    public void setUp() {
        vesselCache = new VesselCache(timeSteps);
        vessel = new Vessel("Vessel1", System.currentTimeMillis());
        vesselTwo = new Vessel("Vessel2", System.currentTimeMillis());
    }

    @Test
    void getAllUuidsWithEmptyCacheTest() {
        assertTrue(vesselCache.getAllVesselUuids().isEmpty());
    }

    @Test
    void getAllUuidsTest() {
        vesselCache.insert(vessel);
        vesselCache.insert(vesselTwo);
        Set<String> uuids = vesselCache.getAllVesselUuids();
        assertTrue(uuids.contains(vessel.getUuid()));
        assertTrue(uuids.contains(vesselTwo.getUuid()));
        assertEquals(2, uuids.size());
    }

    @Test
    public void insertOneVesselTest() {
        vesselCache.insert(vessel);
        assertEquals("Vessel1", vesselCache.getVesselsByUuid(vessel.getUuid())[0].getUuid());
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

        assertEquals(0.5, vesselCache.getVesselsByUuid(vessel.getUuid())[0].getSpeed(), 1e-5);
    }

    @Test
    public void insertTwoInSameTimeSliceTest() {
        vessel.setSpeed(0.0);
        vesselCache.insert(vessel);
        vessel = Vessel.copy(vessel);
        vessel.setSpeed(1.0);
        vesselCache.insert(vessel);

        for (int i = 0; i < vesselCache.getVesselsByUuid(vessel.getUuid()).length; i++) {
            if (i == 0) {
                assertEquals(1.0, vesselCache.getVesselsByUuid(vessel.getUuid())[0].getSpeed(), 1e-5);
                assertFalse(vesselCache.getVesselsByUuid(vessel.getUuid())[0].isFiller());
            } else {
                assertEquals(0.0, vesselCache.getVesselsByUuid(vessel.getUuid())[i].getSpeed(), 1e-5);
                assertTrue(vesselCache.getVesselsByUuid(vessel.getUuid())[i].isFiller());
            }
        }
    }

    @Test
    public void numberOfTimeStepsTest() {
        vesselCache.insert(vessel);
        assertEquals(timeSteps, vesselCache.getVesselsByUuid(vessel.getUuid()).length);
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
            assertEquals(i, vesselCache.getVesselsByUuid(vessel.getUuid())[i].getSpeed(), 1e-5);
        }

        vesselCache.updateTimeSlices();
        vessel = Vessel.copy(vessel);
        vessel.setSpeed(10.0);
        vesselCache.insert(vessel);
        assertEquals(10.0, vesselCache.getVesselsByUuid(vessel.getUuid())[0].getSpeed(), 1e-5);
        assertEquals(3.0, vesselCache.getVesselsByUuid(vessel.getUuid())[timeSteps - 1].getSpeed(), 1e-5);
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
        assertEquals(1.0, vesselCache.getVesselsByUuid(vessel.getUuid())[1].getSpeed(), 1e-5);
        assertFalse(vesselCache.getVesselsByUuid(vessel.getUuid())[1].isFiller());
        assertEquals(1.0, vesselCache.getVesselsByUuid(vessel.getUuid())[0].getSpeed(), 1e-5);
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
