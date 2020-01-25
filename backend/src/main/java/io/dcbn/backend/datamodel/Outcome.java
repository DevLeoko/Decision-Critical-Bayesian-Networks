package io.dcbn.backend.datamodel;

import de.fraunhofer.iosb.iad.maritime.datamodel.AreaOfInterest;
import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Outcome {
	private String uuid;
	private long timestamp;
	//private Graph correlatedNetwork;

	private List<Vessel> correlatedVessels;
	private List<AreaOfInterest> correlatedAOIs;
}