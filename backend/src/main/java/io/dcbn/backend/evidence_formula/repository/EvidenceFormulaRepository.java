package io.dcbn.backend.evidence_formula.repository;

import io.dcbn.backend.evidence_formula.model.EvidenceFormula;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EvidenceFormulaRepository extends CrudRepository<EvidenceFormula, Long> {

    boolean existsByName(String name);
    Optional<EvidenceFormula> findByName(String name);

}
