package io.dcbn.backend.evidenceFormula.services;

import de.fraunhofer.iosb.iad.maritime.datamodel.AreaOfInterest;
import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import io.dcbn.backend.evidenceFormula.services.exceptions.EvaluationException;
import io.dcbn.backend.evidenceFormula.services.exceptions.ParameterSizeMismatchException;
import io.dcbn.backend.evidenceFormula.services.exceptions.SymbolNotFoundException;
import io.dcbn.backend.evidenceFormula.services.exceptions.TypeMismatchException;
import io.dcbn.backend.evidenceFormula.services.visitors.FunctionWrapper;
import lombok.Setter;

import java.util.*;

/**
 * This provides the DSL Evaluation with the required predefined functions.
 */
public class FunctionProvider {

    protected Map<String, FunctionWrapper> functions;

    protected Set<Vessel> correlatedVessels;
    protected Set<AreaOfInterest> correlatedAois;

    @Setter
    protected int currentTimeSlice;
    @Setter
    protected Vessel currentVessel;

    /**
     * Creates a new FunctionProvider without any predefined functions.
     */
    public FunctionProvider() {
        this(new HashMap<>());
    }

    /**
     * Creates a new FunctionProvider with the given predefined functions.
     *
     * @param functions the map containing the predefined functions.
     */
    public FunctionProvider(Map<String, FunctionWrapper> functions) {
        this.functions = functions;
        correlatedVessels = new HashSet<>();
        correlatedAois = new HashSet<>();
    }

    /**
     * Calls the predefined function under the given name.
     *
     * @param name       the name of the function to call.
     * @param parameters the parameters of the function.
     * @return the return value of the called function.
     * @throws SymbolNotFoundException        when no function with the given name is found.
     * @throws ParameterSizeMismatchException when the number of parameters that the function is
     *                                        called with does not match the number of expected
     *                                        parameters.
     * @throws TypeMismatchException          when the parameters of the function call do not match
     *                                        the functions expected parameters.
     */
    public Object call(String name, List<Object> parameters) throws EvaluationException {
        if (!functions.containsKey(name)) {
            throw new SymbolNotFoundException(name);
        }

        FunctionWrapper wrapper = functions.get(name);
        List<Class<?>> expectedTypes = wrapper.getExpectedParameterTypes();

        if (parameters.size() != expectedTypes.size()) {
            throw new ParameterSizeMismatchException(name, expectedTypes.size(), parameters.size());
        }

        for (int i = 0; i < parameters.size(); ++i) {
            if (!expectedTypes.get(i).isInstance(parameters.get(i))) {
                throw new TypeMismatchException(expectedTypes.get(i), parameters.get(i).getClass());
            }
        }

        return wrapper.getFunction().apply(parameters);
    }

    /**
     * Resets the correlated vessel and area of interest sets.
     */
    public void reset() {
        correlatedVessels.clear();
        correlatedAois.clear();
    }

    /**
     * Returns an unmodifiable set of vessels which where included by the previously evaluated functions.
     *
     * @return an unmodifiable set of vessels which where included by the previously evaluated functions.
     */
    public Set<Vessel> getCorrelatedVessels() {
        return correlatedVessels;
    }

    /**
     * Returns an unmodifiable set of areas of interest which where included by the previously evaluated functions.
     *
     * @return an unmodifiable set of areas of interest which where included by the previously evaluated functions.
     */
    public Set<AreaOfInterest> getCorrelatedAois() {
        return correlatedAois;
    }

}
