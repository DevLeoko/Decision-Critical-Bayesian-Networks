package io.dcbn.backend.datamodel;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import de.fraunhofer.iosb.iad.maritime.datamodel.AreaOfInterest;
import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;

public class JsonMapper {
	public static AreaOfInterest fromJsonToAreaOfInterest(String json) {
		Object o = JsonReader.jsonToJava(json);
		if (o instanceof AreaOfInterest)
			return (AreaOfInterest) o;
		return null;
	}

	public static Vessel fromJsonToVessel(String json) {
		Object o = JsonReader.jsonToJava(json);
		if (o instanceof Vessel)
			return (Vessel) o;
		return null;
	}

	public static Outcome fromJsonToOutcome(String json) {
		Object o = JsonReader.jsonToJava(json);
		if (o instanceof Outcome)
			return (Outcome) o;
		return null;
	}

	public static String toJson(Outcome outcome) {
		return JsonWriter.objectToJson(outcome);
	}

	public static String toJson(Vessel vessel) {
		return JsonWriter.objectToJson(vessel);
	}

	public static String toJson(AreaOfInterest aoi) {
		return JsonWriter.objectToJson(aoi);
	}
}
