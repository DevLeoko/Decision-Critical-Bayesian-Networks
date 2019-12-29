package io.dcbn.backend.evidenceFormula.repository;

import io.dcbn.backend.evidenceFormula.model.EvidenceFormula;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvidenceFormulaRepository extends CrudRepository<EvidenceFormula, String> {

  boolean existsByName(String name);

}
