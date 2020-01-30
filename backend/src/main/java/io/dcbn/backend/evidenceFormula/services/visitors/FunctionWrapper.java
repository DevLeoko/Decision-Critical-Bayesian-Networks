package io.dcbn.backend.evidenceFormula.services.visitors;

import io.dcbn.backend.evidenceFormula.services.SingleFunction;
import lombok.Data;

import java.util.List;

/**
 * Represents a function in our evidence formula DSL.
 */
@Data
public class FunctionWrapper {

    /**
     * A list containing the types of parameters the function expects.
     */
    private final List<Class<?>> expectedParameterTypes;

    /**
     * A function from {@code List<Object>} to {@code Object}.
     * This has to be so generic because the parameters or return types of functions could be anything.
     */
    private final SingleFunction<List<Object>, Object> function;


}
