package de.fraunhofer.iosb.iad.maritime.datamodel;

public class Vessel {

	private long timestamp;

	// estimate time of arrival
	private long eta;

	private String uuid;

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
  }

  public Vessel(String uuid, long timestamp) {
		this.uuid = uuid;
		this.timestamp = timestamp;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public long getEta() {
		return eta;
	}

	public void setEta(long eta) {
		this.eta = eta;
	}

	public String getUuid() {
		return uuid;
	}

	public Long getImo() {
		return imo;
	}

	public void setImo(Long imo) {
		this.imo = imo;
	}

	public Long getMmsi() {
		return mmsi;
	}

	public void setMmsi(Long mmsi) {
		this.mmsi = mmsi;
	}

	public String getCallsign() {
		return callsign;
	}

	public void setCallsign(String callsign) {
		this.callsign = callsign;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public VesselType getVesselType() {
		return vesselType;
	}

	public void setVesselType(VesselType vesselType) {
		this.vesselType = vesselType;
	}

	public Double getCog() {
		return cog;
	}

	public void setCog(Double cog) {
		this.cog = cog;
	}

	public Double getSpeed() {
		return speed;
	}

	public void setSpeed(Double speed) {
		this.speed = speed;
	}

	public Double getHeading() {
		return heading;
	}

	public void setHeading(Double heading) {
		this.heading = heading;
	}

	public Double getLength() {
		return length;
	}

	public void setLength(Double length) {
		this.length = length;
	}

	public Double getWidth() {
		return width;
	}

	public void setWidth(Double width) {
		this.width = width;
	}

	public Double getDraught() {
		return draught;
	}

	public void setDraught(Double draught) {
		this.draught = draught;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getAltitude() {
		return altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

}