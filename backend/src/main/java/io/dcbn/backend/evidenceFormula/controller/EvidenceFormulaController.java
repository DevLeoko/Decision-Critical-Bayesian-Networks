package io.dcbn.backend.evidenceFormula.controller;

import com.google.common.base.Strings;
import io.dcbn.backend.evidenceFormula.model.EvidenceFormula;
import io.dcbn.backend.evidenceFormula.repository.EvidenceFormulaRepository;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EvidenceFormulaController {

  private final EvidenceFormulaRepository repository;

  @Autowired
  public EvidenceFormulaController(EvidenceFormulaRepository repository) {
    this.repository = repository;
  }

  @GetMapping("/evidence-formulas")
  @PreAuthorize("hasRole('MODERATOR')")
  public Iterable<EvidenceFormula> getEvidenceFormulas() {
    return repository.findAll();
  }

  @GetMapping("/evidence-formulas/{name}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<EvidenceFormula> getEvidenceFormulaByName(@PathVariable String name) {
    Optional<EvidenceFormula> evidenceFormula = repository.findById(name);
    return evidenceFormula.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @PostMapping("/evidence-formulas")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> createEvidenceFormula(
      @Valid @RequestBody EvidenceFormula evidenceFormula) {
    if (repository.existsByName(evidenceFormula.getName())) {
      return ResponseEntity.badRequest().build();
    }
    if (Strings.isNullOrEmpty(evidenceFormula.getName())) {
      return ResponseEntity.badRequest().build();
    }
    repository.save(evidenceFormula);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/evidence-formulas/{name}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteEvidenceFormulaByName(@PathVariable String name) {
    if (!repository.existsByName(name)) {
      return ResponseEntity.notFound().build();
    }
    repository.deleteById(name);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/evidence-formulas/{name}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> updateEvidenceFormulaByName(@PathVariable String name,
      @Valid @RequestBody EvidenceFormula evidenceFormula) {
    Optional<EvidenceFormula> optional = repository.findById(name);
    if (!optional.isPresent()) {
      return ResponseEntity.notFound().build();
    }
    EvidenceFormula oldEvidenceFormula = optional.get();

    oldEvidenceFormula.setName(evidenceFormula.getName());
    oldEvidenceFormula.setFormula(evidenceFormula.getFormula());
    repository.save(oldEvidenceFormula);

    return ResponseEntity.noContent().build();
  }

  @PostMapping("/evidence-formulas/{name}/evaluate")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Boolean> evaluateEvidenceFormulaByName(@PathVariable String name,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }
}
