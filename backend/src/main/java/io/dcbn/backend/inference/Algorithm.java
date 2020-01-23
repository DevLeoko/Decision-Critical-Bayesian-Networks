package io.dcbn.backend.inference;

import eu.amidst.core.inference.ImportanceSampling;
import eu.amidst.core.inference.ImportanceSamplingRobust;
import eu.amidst.core.inference.InferenceAlgorithm;
import java.util.function.Supplier;

public enum Algorithm {

    IMPORTANCE_SAMPLING(() -> new ImportanceSampling()),
    IMPORTANCE_SAMPLING_ROBUST(() -> new ImportanceSamplingRobust());

    private final Supplier<InferenceAlgorithm> algorithm;

    Algorithm(Supplier<InferenceAlgorithm> algorithm) {
        this.algorithm = algorithm;
    }

    public InferenceAlgorithm getAlgorithm() {
      return algorithm.get();
    }

}
