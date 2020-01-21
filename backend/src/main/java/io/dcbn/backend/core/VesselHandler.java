package io.dcbn.backend.core;

import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VesselHandler {

    private final VesselCache vesselCache;

    @Autowired
    public VesselHandler(VesselCache vesselCache){
        this.vesselCache = vesselCache;
    }

    public void handleVessel(Vessel vessel) {
        vesselCache.insert(vessel);
        // TODO: Add inference manager call
    }
}
