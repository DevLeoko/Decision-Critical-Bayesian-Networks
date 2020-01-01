package io.dcbn.backend.evidenceFormula.controller;

import io.dcbn.backend.evidenceFormula.model.EvidenceFormula;
import io.dcbn.backend.evidenceFormula.repository.EvidenceFormulaRepository;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
  public EvidenceFormula getEvidenceFormulaByName(@PathVariable String name) {
    return repository.findByName(name)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @PostMapping("/evidence-formulas")
  @PreAuthorize("hasRole('ADMIN')")
  public void createEvidenceFormula(
      @Valid @RequestBody EvidenceFormula evidenceFormula) {
    if (repository.existsByName(evidenceFormula.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
    repository.save(evidenceFormula);
  }

  @DeleteMapping("/evidence-formulas/{name}")
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteEvidenceFormulaByName(@PathVariable String name) {
    if (!repository.existsByName(name)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    repository.deleteByName(name);
  }

  @PutMapping("/evidence-formulas/{name}")
  @PreAuthorize("hasRole('ADMIN')")
  public void updateEvidenceFormulaByName(@PathVariable String name,
      @Valid @RequestBody EvidenceFormula evidenceFormula) {
    EvidenceFormula oldEvidenceFormula = repository.findByName(name)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (!name.equals(evidenceFormula.getName())) {
      if (repository.existsByName(evidenceFormula.getName())) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
      }
    }

    evidenceFormula.setId(oldEvidenceFormula.getId());
    repository.save(evidenceFormula);
  }

  @PostMapping("/evidence-formulas/{name}/evaluate")
  @PreAuthorize("hasRole('ADMIN')")
  public boolean evaluateEvidenceFormulaByName(@PathVariable String name,
      HttpServletRequest request) {
    throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
  }
}
