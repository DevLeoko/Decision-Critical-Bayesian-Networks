package de.fraunhofer.iosb.iad.maritime.datamodel;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;

@Data
public class Vessel implements Cloneable {

	private final long timestamp;

	private boolean isFiller;

	// estimate time of arrival
	private long eta;
	private final String uuid;

	private Long imo;
	private Long mmsi;
	private String callsign;
	private String name;
	private VesselType vesselType;

	// course over ground
	private Double cog;

	// in knots
	private Double speed;

	private Double heading;
	private Double length;
	private Double width;
	private Double draught;

	private String destination;

	private Double latitude;
	private Double longitude;
	private Double altitude;

	public Vessel() {
	  uuid = null;
	  timestamp = -1;
  }

	public Vessel(String uuid, long timestamp) {
		this.uuid = uuid;
		this.timestamp = timestamp;
		this.isFiller = false;
	}

	@SneakyThrows
	public static Vessel copy (Vessel toCopy) {
		ObjectMapper objectMapper = new ObjectMapper();
		Vessel deepCopy = objectMapper.readValue(objectMapper.writeValueAsString(toCopy), Vessel.class);
		return deepCopy;
	}
}