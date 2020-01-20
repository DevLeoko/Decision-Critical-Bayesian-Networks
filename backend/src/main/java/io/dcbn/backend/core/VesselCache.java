package io.dcbn.backend.core;

import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import de.fraunhofer.iosb.iad.maritime.datamodel.VesselType;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class VesselCache {

    @Value("${time.steps}")
    private int timeSteps;

    @Getter
    private Map<VesselType, Map<String, Vessel[]>> vesselCache;

    public VesselCache() {
        vesselCache = new HashMap<VesselType, Map<String, Vessel[]>>();
    }

    public void insert(Vessel vessel){
        VesselType vesselType = vessel.getVesselType();
        String uuid = vessel.getUuid();
        // Insert vesselType if its not currently in the cache map
        if (!vesselCache.containsKey(vesselType)) {
            vesselCache.put(vesselType, new HashMap<String, Vessel[]>());
        }
        // Insert vessel if its not currently in the cache map
        if (!vesselCache.get(vesselType).containsKey(uuid)) {
            vesselCache.get(vesselType).put(uuid, new Vessel[timeSteps]);
        }

        vesselCache.get(vesselType).get(uuid)[0] = vessel;
    }

    /*
    Shift every Vessel instance of each vessel in the time-slice-array one to the right
    and write null to the current time slice (position 0 in the array)
     */
    @Scheduled(fixedRateString = "${time.step.length}")
    private void updateCache() {
        for (Map.Entry<VesselType, Map<String, Vessel[]>> entry : vesselCache.entrySet()) {
            for (Map.Entry<String, Vessel[]> vesselMap : entry.getValue().entrySet()){
                for (int i = vesselMap.getValue().length - 1; i > 0; i--) {
                    vesselMap.getValue()[i] = vesselMap.getValue()[i - 1];
                }
                vesselMap.getValue()[0] = null;
            }
        }
    }
}