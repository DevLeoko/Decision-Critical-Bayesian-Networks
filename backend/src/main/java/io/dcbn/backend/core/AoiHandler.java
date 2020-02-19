package io.dcbn.backend.core;

import de.fraunhofer.iosb.iad.maritime.datamodel.AreaOfInterest;
import io.dcbn.backend.core.activemq.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Handles a new AreaOfInterest
 */
@Service
public class AoiHandler {

    private final AoiCache aoiCache;
    private final Producer producer;

    @Autowired
    public AoiHandler(AoiCache aoiCache, Producer producer) {
        this.aoiCache = aoiCache;
        this.producer = producer;
    }

    /**
     * Takes an Aoi and inserts it into the AoiCache
     *
     * @param aoi Aoi to be inserted
     */
    public void handleAoi(AreaOfInterest aoi) {
        aoiCache.insert(aoi.getName(), aoi);
    }
}