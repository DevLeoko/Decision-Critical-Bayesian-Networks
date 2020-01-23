package io.dcbn.backend.core;

import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class VesselCache {

    @Value("${time.steps}")
    private int timeSteps;

    private Map<String, Vessel[]> vesselCache;

    public VesselCache() {
        vesselCache = new HashMap<>();
    }

    public Vessel[] getVesselsByUuid(String uuid) throws IllegalArgumentException {
        return vesselCache.get(uuid);
    }

    public void insert(Vessel vessel){
        String uuid = vessel.getUuid();

        // Insert vessel if its not currently in the cache map
        if (!vesselCache.containsKey(uuid)) {
            vesselCache.put(uuid, new Vessel[timeSteps]);
        }

        vesselCache.get(uuid)[0] = vessel;
    }

    /*
    Shift every Vessel instance of each vessel in the time-slice-array one to the right
    and write null to the current time slice (position 0 in the array)
     */
    @Scheduled(fixedRateString = "${time.step.length}")
    private void updateCache() {
        for (Map.Entry<String, Vessel[]> entry : vesselCache.entrySet()) {
            for (int i = entry.getValue().length - 1; i > 0; i--) {
                entry.getValue()[i] = entry.getValue()[i - 1];
            }
            entry.getValue()[0] = entry.getValue()[1].clone();
            entry.getValue()[0].setFiller(true);
        }
    }

    // Checks if there are vessels in the cache which have not been updated
    // within the last time steps and removes them from the cache
    @Scheduled(fixedRateString = "${cache.refresh.time}")
    private void refreshCache() {
        List<String> toDelete = new ArrayList<>();
        for (Map.Entry<String, Vessel[]> entry : vesselCache.entrySet()) {
            for(int i = 0; i < entry.getValue().length; i++) {
                if (!entry.getValue()[i].isFiller()) {
                    break;
                } else if(i == entry.getValue().length - 1) {
                    toDelete.add(entry.getKey());
                }
            }
        }

        vesselCache.keySet().removeAll(toDelete);
    }
}