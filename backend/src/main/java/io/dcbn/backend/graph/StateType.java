package io.dcbn.backend.graph;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;


public enum StateType {

    BOOLEAN("true", "false");

    @Getter
    @JsonValue
    private final String[] states;


    StateType(String... states) {
        this.states = states;
    }

}
