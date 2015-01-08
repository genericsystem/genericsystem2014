package org.genericsystem.issuetracker;

public class Issue {

	private String id;

	private String descriptif;

	private String priority;

	private String type;

	public Issue() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescriptif() {
		return descriptif;
	}

	public void setDescriptif(String descriptif) {
		this.descriptif = descriptif;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "id " + id + " descriptif " + descriptif + " priority " + priority + " type " + type;
	}

}
