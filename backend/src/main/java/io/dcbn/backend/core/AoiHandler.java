package io.dcbn.backend.core;

import de.fraunhofer.iosb.iad.maritime.datamodel.AreaOfInterest;
import io.dcbn.backend.core.activemq.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AoiHandler {

    private final AoiCache aoiCache;
    private final Producer producer;

    @Autowired
    public AoiHandler(AoiCache aoiCache, Producer producer) {
        this.aoiCache = aoiCache;
        this.producer = producer;
    }

    public void handleAoi(AreaOfInterest aoi) {
        aoiCache.insert(aoi.getName(), aoi);
        System.out.println(aoiCache.getAllAois());
    }
}