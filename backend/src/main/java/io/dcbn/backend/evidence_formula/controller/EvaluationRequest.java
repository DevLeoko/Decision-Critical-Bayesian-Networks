package io.dcbn.backend.evidence_formula.controller;

import io.dcbn.backend.evidence_formula.model.EvidenceFormula;
import lombok.Data;

import java.util.Map;

@Data
public class EvaluationRequest {

    private final EvidenceFormula formula;
    private final Map<String, Object> parameters;

}
