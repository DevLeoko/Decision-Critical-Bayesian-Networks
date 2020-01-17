package io.dcbn.backend.core;

import io.dcbn.backend.core.activemq.Producer;
import io.dcbn.backend.maritimedatamodel.AreaOfInterest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
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
