package io.dcbn.backend.graph;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Getter;

import java.util.Arrays;

/**
 * This enum contains all state types (The possible states a node can have with the given {@link StateType} value.
 */
public enum StateType {

    BOOLEAN("true", "false"),
    INVERTED_BOOLEAN("false","true");

    @Getter
    private final String[] states;


    StateType(String... states) {
        this.states = states;
    }

    public int getIndexOfState(String state) {
        for (int i = 0; i < this.states.length; i++) {
            if (state.equals(this.states[i])) {
                return i;
            }
        }
        return -1;
    }

    @JsonValue
    @JsonRawValue
    public String toJson() throws JsonProcessingException {
        return "{\"states\": " + new JsonMapper().writeValueAsString(states) + "}";
    }

    @JsonCreator
    public static StateType fromArray(@JsonProperty("states") String[] states) {
        for (StateType type : StateType.values()) {
            if (Arrays.equals(type.states, states)) {
                return type;
            }
        }
        return null;
    }
}
