package io.dcbn.backend.core;

import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import de.fraunhofer.iosb.iad.maritime.datamodel.VesselType;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Cache for all current Vessels. Vessels that came before the last time step get deleted.
 */
@Service
@NoArgsConstructor
public class VesselCache {

    @Value("${time.steps}")
    private int timeSteps;

    private Map<String, Vessel[]> cache;

    /**
     * Constructor to manually set timeSteps
     *
     * @param timeSteps amount of timeSteps
     */
    public VesselCache(int timeSteps) {
        this.timeSteps = timeSteps;
        cache = new HashMap<>();
    }

    /**
     * Returns an array of vessels matching the given uuid. Each array entry represents one time slice with 0 being the
     * most recent time slice.
     *
     * @param uuid uuid of the vessel
     * @return An array with Vessels matching the uuid. each array entry represents one time slice.
     */
    public Vessel[] getVesselsByUuid(String uuid) {
        return cache.get(uuid);
    }

    /**
     * Returns all vessel uuids that are currently in the cache
     *
     * @return all vessel uuids that are currently in the cache
     */
    public Set<String> getAllVesselUuids() {
        return cache.keySet();
    }

    /**
     * Returns all vessels that are in the given time slice
     *
     * @param timeSlice Time slice
     * @return Set of all vessels in the given time slice.
     */
    public Set<Vessel> getAllVesselsInTimeSlice(int timeSlice) {
        if(timeSlice < 0 || timeSlice > timeSteps - 1) {
            throw new IllegalArgumentException("time slice must be between 0 and 'timeSteps' - 1");
        }

        return cache.values().stream()
                .map(vesselArr -> vesselArr[timeSlice])
                .collect(Collectors.toSet());
    }

    /**
     * Returns all vessels of the given type that are in the given time slice
     *
     * @param timeSlice Time slice
     * @param type Vesseltype
     * @return all vessels of the given type that are in the given time slice
     */
    public Set<Vessel> getAllVesselsInTimeSliceByType(int timeSlice, VesselType type) {
        if(timeSlice < 0 || timeSlice > timeSteps - 1) {
            throw new IllegalArgumentException("time slice must be between 0 and 'timeSteps' - 1");
        }

        return cache.values().stream()
                .filter(vesselsArr -> vesselsArr[0].getVesselType().equals(type))
                .map(vesselArr -> vesselArr[timeSlice])
                .collect(Collectors.toSet());
    }

    /**
     * Inserts a vessel into the cache. If there's no other vessel with the same uuid in the cache all time slices will
     * be filled with this vessel but with a isFiller flag. Vessel must be not null
     *
     * @param vessel Vessel to be inserted
     */
    public void insert(Vessel vessel) {
        if (vessel == null) {
            throw new IllegalArgumentException("Vessel cannot be null!");
        }

        String uuid = vessel.getUuid();

        // Insert vessel if its not currently in the cache map
        if (!cache.containsKey(uuid)) {
            cache.put(uuid, new Vessel[timeSteps]);
            // Fill all time slices of the cache with the given ship
            for (int i = 0; i < cache.get(uuid).length; i++) {
                if (i == 0) {
                    cache.get(uuid)[0] = vessel;
                } else {
                    cache.get(uuid)[i] = Vessel.copy(vessel);
                    cache.get(uuid)[i].setFiller(true);
                }
            }
            return;
        }

        cache.get(uuid)[0] = vessel;
    }

    /**
     * Shifts every Vessel instance of each vessel in the time-slice-array one to the right
     * and write null to the current time slice (position 0 in the array)
     */
    @Scheduled(fixedRateString = "${time.step.length}")
    public void updateTimeSlices() {
        for (Map.Entry<String, Vessel[]> entry : cache.entrySet()) {
            for (int i = entry.getValue().length - 1; i > 0; i--) {
                entry.getValue()[i] = entry.getValue()[i - 1];
            }
            entry.getValue()[0] = Vessel.copy(entry.getValue()[1]);
            entry.getValue()[0].setFiller(true);
        }
    }

    /**
     * Checks if there are vessels in the cache which have not been updated
     * within the last time steps and removes them from the cache
     */
    @Scheduled(fixedRateString = "${cache.refresh.time}")
    public void refreshCache() {
        List<String> toDelete = new ArrayList<>();
        for (Map.Entry<String, Vessel[]> entry : cache.entrySet()) {
            for (int i = 0; i < entry.getValue().length; i++) {
                if (!entry.getValue()[i].isFiller()) {
                    break;
                } else if (i == entry.getValue().length - 1) {
                    toDelete.add(entry.getKey());
                }
            }
        }

        cache.keySet().removeAll(toDelete);
    }
}
