package io.dcbn.backend.maritimedatamodel;

import java.util.HashMap;
import java.util.Map;

public class Network {
    private String uuid;
    private long timeSliceSize;
    private Map<String, Node> nodes;

    public Network(String uuid, long timeSliceSize) {
        this.uuid = uuid;
        this.timeSliceSize = timeSliceSize;
        this.nodes = new HashMap<>();
    }

    public long getTimeSliceSize() {
        return timeSliceSize;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Map<String, Node> getNodes() {
        return nodes;
    }

}