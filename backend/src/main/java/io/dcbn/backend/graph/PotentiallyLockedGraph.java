package io.dcbn.backend.graph;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PotentiallyLockedGraph {
    private Graph graph;
    private boolean locked;
}
