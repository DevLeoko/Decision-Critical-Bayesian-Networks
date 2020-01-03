package io.dcbn.backend.graph;

import java.util.Arrays;
import java.util.List;

public enum StateType {

    BOOLEAN(Arrays.asList("true", "false"));

    private final List<String> state;

    StateType(List<String> state) {
        this.state = state;
    }

    public List<String> getState() {
        return state;
    }
}
