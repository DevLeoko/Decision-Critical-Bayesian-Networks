package io.dcbn.backend.maritimedatamodel;

import java.util.ArrayList;
import java.util.List;

public class Outcome {
    private String uuid;
    private long timestamp;
    private Network correlatedNetwork;

    private List<Vessel> correlatedVessels;
    private List<AreaOfInterest> correlatedAOIs;

    public Outcome(String uuid, long timestamp) {
        this.uuid = uuid;
        this.timestamp = timestamp;
        this.correlatedVessels = new ArrayList<>();
        this.correlatedAOIs = new ArrayList<>();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Network getCorrelatedNetwork() {
        return correlatedNetwork;
    }

    public List<Vessel> getCorrelatedVessels() {
        return correlatedVessels;
    }

    public List<AreaOfInterest> getCorrelatedAOIs() {
        return correlatedAOIs;
    }

}
