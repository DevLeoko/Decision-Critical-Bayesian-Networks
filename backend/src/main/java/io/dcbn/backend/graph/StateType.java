package io.dcbn.backend.graph;

import lombok.Getter;


public enum StateType {

    BOOLEAN("true", "false");

    @Getter
    private final String[] states;


    StateType(String... states) {
        this.states = states;
    }
}
