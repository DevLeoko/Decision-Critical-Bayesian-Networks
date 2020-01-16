package de.fraunhofer.iosb.iad.maritime.datamodel;

import java.util.ArrayList;
import java.util.List;

public class Node {
	private String uuid;
	private List<Node> parents;
	private List<State> states;

	public Node(String uuid) {
		this.uuid = uuid;
		this.children = new ArrayList<>();
		this.states = new ArrayList<>();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public List<Node> getChildren() {
		return children;
	}

	public List<State> getStates() {
		return states;
	}

}
