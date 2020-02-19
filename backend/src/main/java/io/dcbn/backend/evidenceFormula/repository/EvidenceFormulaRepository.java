package io.dcbn.backend.evidenceFormula.repository;

import io.dcbn.backend.evidenceFormula.model.EvidenceFormula;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EvidenceFormulaRepository extends CrudRepository<EvidenceFormula, Long> {

    boolean existsByName(String name);
    Optional<EvidenceFormula> findByName(String name);

}
