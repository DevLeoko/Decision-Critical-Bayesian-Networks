package io.dcbn.backend.core;

import io.dcbn.backend.maritimedatamodel.AreaOfInterest;
import io.dcbn.backend.maritimedatamodel.Vessel;
import io.dcbn.backend.maritimedatamodel.VesselType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
@NoArgsConstructor
public class VesselCache {

    @Getter
    private Map<VesselType, Set<Vessel>> vesselCache;

    public void insert(){

    }
}
