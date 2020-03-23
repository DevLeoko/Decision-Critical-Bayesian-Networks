package io.dcbn.backend.graph;

import lombok.Data;

@Data
public class PotentiallyLockedGraph {
    private final Graph graph;
    private final boolean locked;
}
