package io.dcbn.backend.inference;

import eu.amidst.core.inference.ImportanceSampling;
import eu.amidst.core.inference.ImportanceSamplingRobust;
import eu.amidst.core.inference.InferenceAlgorithm;
import java.util.function.Supplier;

/**
 * This enum contains the available algorithms in amidst that can be used to run the {@link InferenceManager}.
 */
public enum Algorithm {

    IMPORTANCE_SAMPLING(ImportanceSampling::new),
    IMPORTANCE_SAMPLING_ROBUST(ImportanceSamplingRobust::new);

    private final Supplier<InferenceAlgorithm> algorithm;

    Algorithm(Supplier<InferenceAlgorithm> algorithm) {
        this.algorithm = algorithm;
    }

    public InferenceAlgorithm getAlgorithm() {
      return algorithm.get();
    }

}
