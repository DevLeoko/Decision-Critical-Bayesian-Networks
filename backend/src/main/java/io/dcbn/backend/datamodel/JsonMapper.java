package io.dcbn.backend.datamodel;

import com.cedarsoftware.util.io.JsonReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iosb.iad.maritime.datamodel.AreaOfInterest;
import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;

public class JsonMapper {

    private static ObjectMapper mapper = new ObjectMapper();

    public static AreaOfInterest fromJsonToAreaOfInterest(String json) {
        return (AreaOfInterest) JsonReader.jsonToJava(json);
    }

    public static Vessel fromJsonToVessel(String json) throws JsonProcessingException {
        return mapper.readValue(json, Vessel.class);
    }

    public static Outcome fromJsonToOutcome(String json) throws JsonProcessingException {
        return mapper.readValue(json, Outcome.class);
    }

    public static String toJson(Outcome outcome) throws JsonProcessingException {
        return mapper.writeValueAsString(outcome);
    }

    public static String toJson(Vessel vessel) throws JsonProcessingException {
        return mapper.writeValueAsString(vessel);
    }

    public static String toJson(AreaOfInterest aoi) throws JsonProcessingException {
        return mapper.writeValueAsString(aoi);
    }
}
