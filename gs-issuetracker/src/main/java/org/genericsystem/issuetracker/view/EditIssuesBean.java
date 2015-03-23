package org.genericsystem.issuetracker.view;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.issuetracker.bean.IssueBean;
import org.genericsystem.issuetracker.bean.IssueBean.ElStringWrapper;
import org.genericsystem.issuetracker.bean.IssueSelectedBean;
import org.genericsystem.issuetracker.bean.PriorityBean;
import org.genericsystem.issuetracker.bean.StatutBean;
import org.genericsystem.mutability.Generic;

@Named
@RequestScoped
public class EditIssuesBean {

	@Inject
	private IssueSelectedBean issueSelectionBean;

	@Inject
	private IssueBean issueBean;

	@Inject
	private PriorityBean priorityBean;

	@Inject
	private StatutBean statutBean;

	private String newIssueDescription;

	public String addIssue() {
		issueBean.addIssue(newIssueDescription);
		return "#";
	}

	public String delete(Generic issue) {
		issueSelectionBean.setSelectedIssue(null);
		issueBean.deleteIssue(issue);
		return "#";
	}

	public String getValue(Generic generic) {
		return (String) generic.getValue();
	}

	public ElStringWrapper getDescription(Generic instance) {
		return issueBean.getLink(instance, issueBean.getDescription());
	}

	public ElStringWrapper getPriority(Generic instance) {
		return issueBean.getLink(instance, issueBean.getIssuePriority());
	}

	public ElStringWrapper getStatut(Generic instance) {
		return issueBean.getLink(instance, issueBean.getIssueStatut());
	}

	public List<Generic> getList() {
		return issueBean.getIssuesByStatut();
	}

	public String setSelected(Generic selectedIssue) {
		issueSelectionBean.selectIssueVersions(selectedIssue);
		return "#";
	}

	public List<String> getPriorities() {
		return priorityBean.getPriorities();
	}

	public List<String> getStatuts() {
		return statutBean.getStatuts();
	}

	public String getNewIssueDescription() {
		return newIssueDescription;
	}

	public void setNewIssueDescription(String newIssueDescription) {
		this.newIssueDescription = newIssueDescription;
	}
}
