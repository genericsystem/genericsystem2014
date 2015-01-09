package org.genericsystem.issuetracker.bean;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.issuetracker.IssueDTO;
import org.genericsystem.issuetracker.crud.IssueCRUD;
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

	@Inject
	private IssueCRUD issueCrud;

	public List<IssueDTO> getIssues() {
		return issueCrud.getIssues();
	}

	public String createIssue() {
		IssueDTO issueDTO = new IssueDTO();
		issueDTO.setId(id);
		issueDTO.setDescriptif(descriptif);
		issueDTO.setPriority(priority);
		issueDTO.setType(type);
		issueCrud.createIssue(issueDTO);
		return "#";
	}

	// private final String BASE_URL = "http://localhost:8080/gs-issuetracker/rest/issueManager";
	//
	// public List<Issue> getIssues() {
	// Client client = ClientBuilder.newClient();
	// WebTarget myResource = client.target(BASE_URL + "/getIssues");
	// try {
	// return myResource.request(MediaType.APPLICATION_JSON).get(IssueWrapper.class).getList();
	// } catch (Exception e) {
	// log.error(e.getMessage(), e);
	// }
	// return new ArrayList<Issue>();
	// }
	//
	// public String createIssue() {
	// Client client = ClientBuilder.newClient();
	// WebTarget myResource = client.target(BASE_URL + "/createIssue");
	// try {
	// Issue issue = new Issue();
	// issue.setId(id);
	// issue.setDescriptif(descriptif);
	// issue.setPriority(priority);
	// issue.setType(type);
	// myResource.request(MediaType.APPLICATION_JSON).buildPost(Entity.entity(issue, MediaType.APPLICATION_JSON)).invoke();
	// } catch (Exception e) {
	// log.error(e.getMessage(), e);
	// }
	// return "#";
	// }

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
