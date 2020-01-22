package io.dcbn.backend.datamodel;

import de.fraunhofer.iosb.iad.maritime.datamodel.AreaOfInterest;
import de.fraunhofer.iosb.iad.maritime.datamodel.Vessel;
import io.dcbn.backend.graph.Graph;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Outcome {
	private String uuid;
	private long timestamp;
	private Graph correlatedNetwork;

	private List<Vessel> correlatedVessels;
	private List<AreaOfInterest> correlatedAOIs;

}