package de.fraunhofer.iosb.iad.maritime.datamodel;

import java.util.ArrayList;
import java.util.List;

public class State {
	private List<Double> probabilities;

	public State() {
		this.probabilities = new ArrayList<>();
	}

	public List<Double> getProbabilities() {
		return probabilities;
	}

	public void setProbabilities(List<Double> probabilities) {
		this.probabilities = probabilities;
	}
}
