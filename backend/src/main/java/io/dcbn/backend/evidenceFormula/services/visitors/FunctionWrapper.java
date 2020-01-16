package io.dcbn.backend.evidenceFormula.services.visitors;

import java.util.List;
import java.util.function.Function;
import lombok.Data;

@Data
public class FunctionWrapper {

  private final List<Class<?>> expectedParameterTypes;
  private final Function<List<Object>, Object> function;

}
