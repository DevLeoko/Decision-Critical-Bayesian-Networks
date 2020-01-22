package io.dcbn.backend.evidenceFormula.services.visitors;

import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import de.fraunhofer.iosb.iad.maritime.datamodel.AreaOfInterest;
import io.dcbn.backend.evidenceFormula.services.QuadFunction;
import java.util.List;
import lombok.Data;

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
  private final QuadFunction<List<Object>, List<Vessel>, List<AreaOfInterest>, Integer, Object> function;

}
