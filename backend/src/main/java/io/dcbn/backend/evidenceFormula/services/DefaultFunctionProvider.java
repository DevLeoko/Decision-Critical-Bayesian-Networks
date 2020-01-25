package io.dcbn.backend.evidenceFormula.services;

import de.fraunhofer.iosb.iad.maritime.datamodel.AreaOfInterest;
import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import io.dcbn.backend.evidenceFormula.services.visitors.FunctionWrapper;

import java.util.*;

public class DefaultFunctionProvider extends FunctionProvider {

    private Object inArea(List<Object> parameters, Set<Vessel> ignored, Set<AreaOfInterest> correlatedAois, int timeSlice) {
        correlatedAois.add(new AreaOfInterest("TEST_AREA", null));
        return "TEST_AREA".equals(parameters.get(0));
    }

    public DefaultFunctionProvider() {
        Map<String, FunctionWrapper> functions = new HashMap<>();
        functions.put("inArea", new FunctionWrapper(Collections.singletonList(String.class), this::inArea));
        super.functions = functions;
    }
}
