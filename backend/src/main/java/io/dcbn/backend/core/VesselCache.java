package io.dcbn.backend.core;

import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import de.fraunhofer.iosb.iad.maritime.datamodel.VesselType;
import io.dcbn.backend.config.DcbnConfig;
import lombok.Getter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class VesselCache {

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
            vesselCache.get(vesselType).put(uuid, new Vessel[DcbnConfig.TIME_STEPS]);
        }

        vesselCache.get(vesselType).get(uuid)[0] = vessel;
    }

    /*
    Shift every Vessel instance of each vessel in the time-slice-array one to the right
    and write null to the current time slice (position 0 in the array)
     */
    @Scheduled(fixedRate = DcbnConfig.TIME_STEP_LENGTH)
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
