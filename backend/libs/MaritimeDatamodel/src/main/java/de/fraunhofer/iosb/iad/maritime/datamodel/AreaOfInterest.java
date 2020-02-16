package de.fraunhofer.iosb.iad.maritime.datamodel;

import com.vividsolutions.jts.geom.Geometry;
import lombok.Data;

@Data
public class AreaOfInterest {
	private String uuid;
	private String name;
	private AreaOfInterestType type;
	private Geometry geometry;

	public AreaOfInterest(String uuid, Geometry geometry) {
		this.uuid = uuid;
		this.geometry = geometry;
	}

}