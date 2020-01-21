package io.dcbn.backend.inference;

import eu.amidst.core.inference.ImportanceSampling;
import eu.amidst.core.inference.ImportanceSamplingRobust;
import eu.amidst.core.inference.InferenceAlgorithm;
import lombok.Getter;

public enum Algorithm {

    IMPORTANCE_SAMPLING(new ImportanceSampling()),
    IMPORTANCE_SAMPLING_ROBUST(new ImportanceSamplingRobust());

    @Getter
    private final InferenceAlgorithm algorithm;


    Algorithm(InferenceAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

}
