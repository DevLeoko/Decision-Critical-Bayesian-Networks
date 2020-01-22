package io.dcbn.backend.evidenceFormula.services.visitors;

import de.fraunhofer.iosb.iad.maritime.datamodel.AreaOfInterest;
import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import io.dcbn.backend.evidenceFormula.services.QuadFunction;
import java.util.List;
import java.util.Set;
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
  private final QuadFunction<List<Object>, Set<Vessel>, Set<AreaOfInterest>, Integer, Object> function;

}
