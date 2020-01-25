package io.dcbn.backend.evidenceFormula.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.dcbn.backend.evidenceFormula.model.EvidenceFormula;
import io.dcbn.backend.evidenceFormula.repository.EvidenceFormulaRepository;
import io.dcbn.backend.evidenceFormula.services.EvidenceFormulaEvaluator;
import io.dcbn.backend.evidenceFormula.services.exceptions.EvaluationException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
  private final EvidenceFormulaEvaluator evaluator;

  @Autowired
  public EvidenceFormulaController(
      EvidenceFormulaRepository repository,
      EvidenceFormulaEvaluator evaluator) {
    this.repository = repository;
    this.evaluator = evaluator;
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
    EvidenceFormula evidenceFormula = repository.findByName(name).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    ObjectMapper mapper = new JsonMapper();
    try {
      JsonNode node = mapper.readValue(request.getReader(), JsonNode.class);
      return evaluator.evaluate(node, evidenceFormula);
    } catch (EvaluationException e) {
      throw e;
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @ExceptionHandler(EvaluationException.class)
  public EvaluationException handleException(EvaluationException ex) {
    return ex;
  }

}
