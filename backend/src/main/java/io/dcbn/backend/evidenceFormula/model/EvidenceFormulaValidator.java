package io.dcbn.backend.evidenceFormula.model;

import com.fasterxml.jackson.databind.json.JsonMapper;
import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import de.fraunhofer.iosb.iad.maritime.datamodel.VesselType;
import io.dcbn.backend.evidenceFormula.services.EvidenceFormulaEvaluator;
import io.dcbn.backend.evidenceFormula.services.exceptions.EvaluationException;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Service
public class EvidenceFormulaValidator implements ConstraintValidator<FormulaConstraint, EvidenceFormula> {

    private final EvidenceFormulaEvaluator evaluator;
    private Vessel defaultVessel;

    public EvidenceFormulaValidator(EvidenceFormulaEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public void initialize(FormulaConstraint constraintAnnotation) {
        defaultVessel = new Vessel("uuid", 0);
        defaultVessel.setLongitude(0.0);
        defaultVessel.setLatitude(0.0);
        defaultVessel.setSpeed(0.0);
        defaultVessel.setFiller(false);
        defaultVessel.setCog(0.0);
        defaultVessel.setAltitude(0.0);
        defaultVessel.setCallsign("callsign");
        defaultVessel.setDestination("destination");
        defaultVessel.setEta(0);
        defaultVessel.setDraught(0.0);
        defaultVessel.setHeading(0.0);
        defaultVessel.setImo(0L);
        defaultVessel.setMmsi(0L);
        defaultVessel.setLength(0.0);
        defaultVessel.setName("name");
        defaultVessel.setVesselType(VesselType.FISHING_VESSEL);
        defaultVessel.setWidth(0.0);
    }

    @SneakyThrows
    @Override
    public boolean isValid(EvidenceFormula evidenceFormula,
                           ConstraintValidatorContext constraintValidatorContext) {
        try {
            evaluator.evaluate(defaultVessel, evidenceFormula);
            return true;
        } catch (EvaluationException ex) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(new JsonMapper().writeValueAsString(ex)).addConstraintViolation();
            return false;
        }
    }
}
