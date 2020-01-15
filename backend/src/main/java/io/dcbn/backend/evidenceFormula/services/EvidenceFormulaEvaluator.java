package io.dcbn.backend.evidenceFormula.services;

import com.fasterxml.jackson.databind.JsonNode;
import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import io.dcbn.backend.evidenceFormula.model.EvidenceFormula;

public class EvidenceFormulaEvaluator {

  public boolean evaluate(JsonNode json, EvidenceFormula evidenceFormula) {
    throw new UnsupportedOperationException("Not implemented!"); // TODO
  }

  public boolean evaluate(Vessel vessel, EvidenceFormula evidenceFormula) {
    throw new UnsupportedOperationException("Not implemented!"); // TODO
  }

}
