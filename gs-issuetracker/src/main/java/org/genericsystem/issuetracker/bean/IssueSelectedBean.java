package org.genericsystem.issuetracker.bean;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.issuetracker.bean.IssueBean.ElStringWrapper;
import org.genericsystem.mutability.Generic;

@Named
@SessionScoped
public class IssueSelectedBean implements Serializable {
	private static final long serialVersionUID = -6707062384436255711L;

	@Inject
	private IssueBean issueBean;

	@Inject
	private PriorityBean priorityBean;

	@Inject
	private StatutBean statutBean;

	@Inject
	private VersionBean versionBean;

	private transient Generic selectedIssue;

	public String getSearchedIssue() {
		return (selectedIssue != null) ? (String) selectedIssue.getValue() : "---";
	}

	public List<String> getSelectedVersions() {
		return issueBean.getSelectedVersions();
	}

	public void setSelectedVersions(List<String> selectedVersions) {
		issueBean.setSelectedVersions(selectedVersions);
	}

	public void selectIssueVersions(Generic selectedIssue) {
		this.selectedIssue = selectedIssue;
		issueBean.selectIssueVersions(selectedIssue);
	}

	public void addVersionsToIssue(Generic instance) {
		issueBean.addVersionsToIssue(instance);
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

	public List<String> getPriorities() {
		return priorityBean.getPriorities();
	}

	public List<String> getStatuts() {
		return statutBean.getStatuts();
	}

	public List<String> getVersions() {
		return versionBean.getVersions();
	}

	public Generic getSelectedIssue() {
		return selectedIssue;
	}

	public void setSelectedIssue(Generic selectedIssue) {
		this.selectedIssue = selectedIssue;
	}

}
