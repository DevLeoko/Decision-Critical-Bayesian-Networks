package io.dcbn.backend.core;

import de.fraunhofer.iosb.iad.maritime.datamodel.AreaOfInterest;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Cache for all current AreaOfInterests.
 */
@Service
@NoArgsConstructor
public class AoiCache {

    /**
     * Actual cache HashMap
     */
    private Map<String, AreaOfInterest> aoiCache = new HashMap<>();

    /**
     * Takes an AreaOfInterest and its name and inserts it into the cache if no Aoi with the given name already exists
     * and if neither the name nor the Aoi are null.
     *
     * @param name Name of the Aoi
     * @param aoi AreaOfInterest to be inserted
     */
    public void insert(String name, AreaOfInterest aoi) {
        if (aoi == null || name == null) {
            throw new IllegalArgumentException("Name and Aoi must not be null!");
        } else if (aoiCache.containsKey(name)) {
            throw new IllegalArgumentException("Area of interest with the given name already exists!");
        } else {
            aoiCache.put(name, aoi);
        }
    }

    /**
     * Returns the Aoi matching the given name.
     *
     * @param name The name of the Aoi
     * @return the aoi matching the name
     */
    public AreaOfInterest getAoi(String name) {
        return aoiCache.get(name);
    }

    /**
     * Returns all Aois that are in the cache
     *
     * @return all Aois in that are in the cache
     */
    public Collection<AreaOfInterest> getAllAois() {
        return aoiCache.values();
    }
}