package io.dcbn.backend.core;

import de.fraunhofer.iosb.iad.maritime.datamodel.AreaOfInterest;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class AoiCache {

    @Getter
    private Map<String, AreaOfInterest> aoiCache = new HashMap<String, AreaOfInterest>();

    public void insert(String name, AreaOfInterest aoi){
        if(aoiCache.containsKey(name)) {
            throw new IllegalArgumentException("Area of interest with the given name already exists!");
        }
        else {aoiCache.put(name, aoi);}
    }
}