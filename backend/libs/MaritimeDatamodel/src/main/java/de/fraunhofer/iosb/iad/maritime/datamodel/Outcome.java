package de.fraunhofer.iosb.iad.maritime.datamodel;

import io.dcbn.backend.graph.Graph;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Outcome {
	private String uuid;
	private long timestamp;
	private Graph correlatedNetwork;

	private List<Vessel> correlatedVessels;
	private List<AreaOfInterest> correlatedAOIs;

}