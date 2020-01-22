package io.dcbn.backend.core;

import de.fraunhofer.iosb.iad.maritime.datamodel.AreaOfInterest;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@NoArgsConstructor
public class AoiCache {

    private Map<String, AreaOfInterest> aoiCache = new HashMap<>();

    public void insert(String name, AreaOfInterest aoi){
        if (aoi == null || name == null) {
            throw new IllegalArgumentException("Name and Aoi must not be null!");
        } else if (aoiCache.containsKey(name)) {
            throw new IllegalArgumentException("Area of interest with the given name already exists!");
        } else {
            aoiCache.put(name, aoi);
        }
    }

    public AreaOfInterest getAoi(String name) {
        return aoiCache.get(name);
    }

    public Collection<AreaOfInterest> getAllAois() {
        return aoiCache.values();
    }
}