package io.dcbn.backend.evidenceFormula.services;

import de.fraunhofer.iosb.iad.maritime.datamodel.AreaOfInterest;
import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import io.dcbn.backend.evidenceFormula.services.exceptions.ParameterSizeMismatchException;
import io.dcbn.backend.evidenceFormula.services.exceptions.SymbolNotFoundException;
import io.dcbn.backend.evidenceFormula.services.exceptions.TypeMismatchException;
import io.dcbn.backend.evidenceFormula.services.visitors.FunctionWrapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Setter;

public class FunctionProvider {

  protected Map<String, FunctionWrapper> functions;

  protected List<Vessel> correlatedVessels;
  protected List<AreaOfInterest> correlatedAois;

  @Setter
  protected int currentTimeSlice;

  public FunctionProvider() {
    this.functions = new HashMap<>();
    correlatedVessels = new ArrayList<>();
    correlatedAois = new ArrayList<>();
  }

  public FunctionProvider(Map<String, FunctionWrapper> functions) {
    this.functions = functions;
    correlatedVessels = new ArrayList<>();
    correlatedAois = new ArrayList<>();
  }

  public Object call(String name, List<Object> parameters) {
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

    return wrapper.getFunction().apply(parameters, correlatedVessels, correlatedAois,
        currentTimeSlice);
  }

  public void reset() {
    correlatedVessels.clear();
    correlatedAois.clear();
  }

  public List<Vessel> getCorrelatedVessels() {
    return Collections.unmodifiableList(correlatedVessels);
  }

  public List<AreaOfInterest> getCorrelatedAois() {
    return Collections.unmodifiableList(correlatedAois);
  }

}
