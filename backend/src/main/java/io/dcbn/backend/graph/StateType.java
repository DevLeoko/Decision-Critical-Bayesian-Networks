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

    public int getIndexOfSate(String state) {
        String[] states = this.states;
        for (int i = 0; i < states.length; i++) {
            if (state.equals(states[i])) {
                return i;
            }
        }
        //TODO solve (maybe return -1 ant catch exception)
        return -1;
    }

}
