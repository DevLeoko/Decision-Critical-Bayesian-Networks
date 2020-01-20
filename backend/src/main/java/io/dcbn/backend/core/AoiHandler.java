package io.dcbn.backend.core;

import de.fraunhofer.iosb.iad.maritime.datamodel.AreaOfInterest;
import io.dcbn.backend.core.activemq.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AoiHandler {

    private final AoiCache aoiCache;

    @Autowired
    public AoiHandler(AoiCache aoiCache){
        this.aoiCache = aoiCache;
    }

    public void handleAoi(AreaOfInterest aoi) {
        try {
            aoiCache.insert(aoi.getName(), aoi);
        } catch(IllegalArgumentException e) {
            Producer.sendErrorMessage(e.getMessage());
        }
    }
}
