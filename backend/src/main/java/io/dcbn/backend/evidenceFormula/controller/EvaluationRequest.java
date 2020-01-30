package io.dcbn.backend.evidenceFormula.controller;

import io.dcbn.backend.evidenceFormula.model.EvidenceFormula;
import lombok.Data;

import java.util.Map;

@Data
public class EvaluationRequest {

    private final EvidenceFormula formula;
    private final Map<String, Object> parameters;

}
