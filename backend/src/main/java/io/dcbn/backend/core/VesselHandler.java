package io.dcbn.backend.core;

import io.dcbn.backend.maritimedatamodel.AreaOfInterest;
import io.dcbn.backend.maritimedatamodel.Vessel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VesselHandler {

    private final VesselCache vesselCache;

    @Autowired
    public VesselHandler(VesselCache vesselCache){
        this.vesselCache = vesselCache;
    }

    public void handleVessel(Vessel vessel){
        vesselCache.insert(vessel);
    }
}
