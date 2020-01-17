package io.dcbn.backend.core;

import io.dcbn.backend.maritimedatamodel.AreaOfInterest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@NoArgsConstructor
public class AoiCache {

    @Getter
    private Map<String, AreaOfInterest> aoiCache;

    public void insert(String name, AreaOfInterest aoi){
        if(aoiCache.containsKey(name)) {
            throw new IllegalArgumentException("Area of interest with the given name already exists!");
        }
        else {aoiCache.put(name, aoi);}
    }
}
