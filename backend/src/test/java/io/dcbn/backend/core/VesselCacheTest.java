package io.dcbn.backend.core;

import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import de.fraunhofer.iosb.iad.maritime.datamodel.VesselType;
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
    public void getAllVesselsInTimeSliceExceptionTest() {
        assertThrows(IllegalArgumentException.class,
                () -> vesselCache.getAllVesselsInTimeSlice(-1));
        assertThrows(IllegalArgumentException.class,
                () -> vesselCache.getAllVesselsInTimeSlice(5));
    }

    @Test
    public void getAllVesselsInTimeSliceByTypeTest() {
        vessel.setSpeed(0.0);
        vessel.setVesselType(VesselType.FISHING_VESSEL);
        vesselCache.insert(vessel);
        vesselTwo.setVesselType(VesselType.CARGO);
        vesselTwo.setSpeed(0.0);
        vesselCache.insert(vesselTwo);
        vesselCache.updateTimeSlices();
        vessel = Vessel.copy(vessel);
        vesselTwo = Vessel.copy(vesselTwo);
        vessel.setSpeed(1.0);
        vesselCache.insert(vessel);
        vesselTwo.setSpeed(2.0);
        vesselCache.insert(vesselTwo);

        Set<Vessel> vesselSet = vesselCache.getAllVesselsInTimeSliceByType(0, VesselType.CARGO);
        assertTrue(vesselSet.contains(vesselTwo));
        assertEquals(1, vesselSet.size());

        for(Vessel vessel : vesselSet) {
            assertEquals(VesselType.CARGO, vessel.getVesselType());
            assertEquals(2.0, vessel.getSpeed());
        }

        vesselSet = vesselCache.getAllVesselsInTimeSliceByType(1, VesselType.FISHING_VESSEL);
        assertEquals(1, vesselSet.size());

        for(Vessel vessel : vesselSet) {
            assertEquals(VesselType.FISHING_VESSEL, vessel.getVesselType());
            assertEquals(0.0, vessel.getSpeed());
        }
    }

    @Test
    public void getAllVesselsInTimeSliceTest() {
        vessel.setSpeed(0.0);
        vesselCache.insert(vessel);
        vesselTwo.setSpeed(0.0);
        vesselCache.insert(vesselTwo);
        vesselCache.updateTimeSlices();
        vessel = Vessel.copy(vessel);
        vesselTwo = Vessel.copy(vesselTwo);
        vessel.setSpeed(1.0);
        vesselCache.insert(vessel);
        vesselTwo.setSpeed(2.0);
        vesselCache.insert(vesselTwo);

        Set<Vessel> vesselSet = vesselCache.getAllVesselsInTimeSlice(0);
        assertTrue(vesselSet.contains(vessel));
        assertTrue(vesselSet.contains(vesselTwo));
        assertEquals(2, vesselSet.size());

        for(Vessel vessel : vesselSet) {
            if(vessel.getUuid().equals("Vessel1")) {
                assertEquals(1.0, vessel.getSpeed());
            } else if (vessel.getUuid().equals("Vessel2")) {
                assertEquals(2.0, vessel.getSpeed());
            }
        }

        vesselSet = vesselCache.getAllVesselsInTimeSlice(1);
        assertEquals(2, vesselSet.size());

        for(Vessel vessel : vesselSet) {
            if(vessel.getUuid().equals("Vessel1")) {
                assertEquals(0.0, vessel.getSpeed());
            } else if (vessel.getUuid().equals("Vessel2")) {
                assertEquals(0.0, vessel.getSpeed());
            }
        }
    }

    @Test
    public void getAllUuidsWithEmptyCacheTest() {
        assertTrue(vesselCache.getAllVesselUuids().isEmpty());
    }

    @Test
    public void getAllUuidsTest() {
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
