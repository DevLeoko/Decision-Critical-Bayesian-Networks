package io.dcbn.backend.evidence_formula.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import io.dcbn.backend.evidence_formula.model.EvidenceFormula;
import io.dcbn.backend.evidence_formula.repository.EvidenceFormulaRepository;
import io.dcbn.backend.evidence_formula.services.EvidenceFormulaEvaluator;
import io.dcbn.backend.evidence_formula.services.exceptions.EvaluationException;
import io.dcbn.backend.graph.Graph;
import io.dcbn.backend.graph.Node;
import io.dcbn.backend.graph.repositories.GraphRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.*;

@Transactional
@RestController
public class EvidenceFormulaController {

    private static final String NOT_FOUND = "No evidence formula found with this name!";
    private static final String ALREADY_EXISTS = "Evidence formula with this name exists already!";

    private final EvidenceFormulaRepository repository;
    private final EvidenceFormulaEvaluator evaluator;

    private final GraphRepository graphRepository;

    @Autowired
    public EvidenceFormulaController(
            EvidenceFormulaRepository repository,
            EvidenceFormulaEvaluator evaluator, GraphRepository graphRepository) {
        this.repository = repository;
        this.evaluator = evaluator;
        this.graphRepository = graphRepository;
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ALREADY_EXISTS);
        }
        repository.save(evidenceFormula);
    }

    @DeleteMapping("/evidence-formulas/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<String> deleteEvidenceFormulaById(@PathVariable long id) {
        EvidenceFormula formula = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND));

        List<String> changedGraphs = new ArrayList<>();
        for (Graph graph : graphRepository.findAll()) {
            for (Node node : graph.getNodes()) {
                if (formula.getName().equals(node.getEvidenceFormulaName())) {
                    node.setEvidenceFormulaName(null);
                    changedGraphs.add(graph.getName());
                }
            }
        }
        repository.deleteById(id);
        return changedGraphs;
    }

    @PutMapping("/evidence-formulas/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void updateEvidenceFormulaByName(@PathVariable long id,
                                            @Valid @RequestBody EvidenceFormula evidenceFormula) {
        EvidenceFormula oldEvidenceFormula = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND));


        if (!oldEvidenceFormula.getName().equals(evidenceFormula.getName())
                && repository.existsByName(evidenceFormula.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ALREADY_EXISTS);
        }

        evidenceFormula.setId(oldEvidenceFormula.getId());
        repository.save(evidenceFormula);
    }

    @PostMapping("/evidence-formulas/evaluate")
    @PreAuthorize("hasRole('ADMIN')")
    public boolean evaluateEvidenceFormula(@RequestBody EvaluationRequest evaluationRequest) {
        ObjectMapper mapper = new JsonMapper();
        try {
            Vessel vessel = mapper.convertValue(evaluationRequest.getParameters(), Vessel.class);
            return evaluator.evaluate(vessel, evaluationRequest.getFormula());
        } catch (EvaluationException e) {
            throw e;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler(EvaluationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public EvaluationException handleException(EvaluationException ex) {
        return ex;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(MethodArgumentNotValidException ex)
            throws JsonProcessingException {
        Map<String, Object> map = new JsonMapper().readValue(
                Objects.requireNonNull(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage()),
                new TypeReference<Map<String, Object>>() {
                });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
    }

}
