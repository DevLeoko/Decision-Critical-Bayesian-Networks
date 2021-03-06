package io.dcbn.backend.core;

import de.fraunhofer.iosb.iad.maritime.datamodel.AreaOfInterest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Handles a new AreaOfInterest
 */
@Service
public class AoiHandler {

    private final AoiCache aoiCache;

    @Autowired
    public AoiHandler(AoiCache aoiCache) {
        this.aoiCache = aoiCache;
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