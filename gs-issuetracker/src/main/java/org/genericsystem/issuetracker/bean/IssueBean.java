package org.genericsystem.issuetracker.bean;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.genericsystem.issuetracker.Issue;
import org.genericsystem.issuetracker.IssueWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@RequestScoped
public class IssueBean {

	protected static Logger log = LoggerFactory.getLogger(IssueBean.class);

	private String id;

	private String descriptif;

	private String priority;

	private String type;

	private final String BASE_URL = "http://localhost:8080/gs-issuetracker/rest/issueManager";

	public List<Issue> getIssues() {
		Client client = ClientBuilder.newClient();
		WebTarget myResource = client.target(BASE_URL + "/getIssues");
		try {
			return myResource.request(MediaType.APPLICATION_JSON).get(IssueWrapper.class).getList();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return new ArrayList<Issue>();
	}

	public String createIssue() {
		Client client = ClientBuilder.newClient();
		WebTarget myResource = client.target(BASE_URL + "/createIssue");
		try {
			Issue issue = new Issue();
			issue.setId(id);
			issue.setDescriptif(descriptif);
			issue.setPriority(priority);
			issue.setType(type);
			myResource.request(MediaType.APPLICATION_JSON).buildPost(Entity.entity(issue, MediaType.APPLICATION_JSON)).invoke();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return "#";
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

}
