package io.dcbn.backend.evidenceformula.controller;

import io.dcbn.backend.evidenceformula.model.EvidenceFormula;
import lombok.Data;

import java.util.Map;

@Data
public class EvaluationRequest {

    private final EvidenceFormula formula;
    private final Map<String, Object> parameters;

}
