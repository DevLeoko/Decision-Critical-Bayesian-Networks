package de.fraunhofer.iosb.iad.maritime.datamodel;

import com.vividsolutions.jts.geom.Geometry;

public class AreaOfInterest {
	private String uuid;
	private String name;
	private AreaOfInterestType type;
	private Geometry geometry;

	public AreaOfInterest(String uuid, Geometry geometry) {
		this.uuid = uuid;
		this.geometry = geometry;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AreaOfInterestType getType() {
		return type;
	}

	public void setType(AreaOfInterestType type) {
		this.type = type;
	}

	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

}