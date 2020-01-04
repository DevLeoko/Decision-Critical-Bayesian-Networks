package io.dcbn.backend.graph;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;


public enum StateType {

    BOOLEAN(Arrays.asList("true", "false"));

    @Getter
    private final List<String> state;

    StateType(List<String> state) {
        this.state = state;
    }
}
