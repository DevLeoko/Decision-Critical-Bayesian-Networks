package io.dcbn.backend.evidenceFormula.repository;

import io.dcbn.backend.evidenceFormula.model.EvidenceFormula;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvidenceFormulaRepository extends CrudRepository<EvidenceFormula, Long> {

  boolean existsByName(String name);

  Optional<EvidenceFormula> findByName(String name);

  void deleteByName(String name);

}
