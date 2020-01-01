package io.dcbn.backend.graph;

public enum StateType {

    BOOLEAN(new String[]{"true", "false"});

    private final String[] state;
    StateType(String[] state) {
        this.state = state;
    }

    public String[] getState() {
        return state;
    }
}
