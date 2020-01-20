package io.dcbn.backend.inference;

import eu.amidst.core.inference.ImportanceSampling;
import eu.amidst.core.inference.ImportanceSamplingRobust;
import eu.amidst.core.inference.InferenceAlgorithm;
import lombok.Getter;

public enum Algorithm {;

    @Getter
    private final InferenceAlgorithm algorithm;

    ImportanceSampling IMPORTANCE_SAMPLING() {
        return new ImportanceSampling();
    };

    ImportanceSamplingRobust IMPORTANCE_SAMPLING_ROBUST() {
        return new ImportanceSamplingRobust();
    };


    Algorithm(InferenceAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

}
