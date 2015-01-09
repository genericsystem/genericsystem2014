package org.genericsystem.issuetracker;

import java.io.Serializable;

public class IssueDTO implements Serializable {

	private static final long serialVersionUID = -7700268747811447119L;

	private String id;

	private String descriptif;

	private String priority;

	private String type;

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

}
