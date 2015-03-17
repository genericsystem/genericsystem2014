package org.genericsystem.issuetracker.view;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.issuetracker.bean.IssueBean;
import org.genericsystem.issuetracker.bean.IssueBean.ElStringWrapper;
import org.genericsystem.issuetracker.bean.IssueSelectionBean;
import org.genericsystem.issuetracker.bean.PriorityBean;
import org.genericsystem.issuetracker.bean.StatutBean;
import org.genericsystem.issuetracker.model.IssuePriority;
import org.genericsystem.issuetracker.model.IssueStatut;
import org.genericsystem.issuetracker.qualifier.Provide;
import org.genericsystem.mutability.Generic;

@Named
@RequestScoped
public class IssueViewBean {

	@Inject
	private IssueBean issueBean;

	@Inject
	private IssueSelectionBean issueSelectionBean;

	@Inject
	private PriorityBean priorityBean;

	@Inject
	private StatutBean statutBean;

	private String newIssueDescription;

	@Inject
	@Provide
	private IssuePriority issuePriority;

	@Inject
	@Provide
	private IssueStatut issueStatut;

	public String addIssue() {
		return issueBean.addIssue(newIssueDescription);
	}

	public String delete(Generic issue) {
		return issueBean.deleteIssue(issue);
	}

	public ElStringWrapper getDescription(Generic instance) {
		return issueBean.getDescription(instance);
	}

	public ElStringWrapper getLink(Generic instance, Generic target) {
		return issueBean.getLink(instance, target);
	}

	public List<Generic> getList() {
		return issueBean.getIssuesByStatut();
	}

	public Generic getSelected() {
		return issueSelectionBean.getSelectedIssue();
	}

	public String setSelected(Generic selectedIssue) {
		issueSelectionBean.setSelectedIssue(selectedIssue);
		return "#";
	}

	public String getSearchedIssue() {
		return issueSelectionBean.getSearchedIssue();
	}

	public void setSearchedIssue(String searchedIssue) {
		issueSelectionBean.setSearchedIssue(searchedIssue);
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

	public IssuePriority getIssuePriority() {
		return issuePriority;
	}

	public void setIssuePriority(IssuePriority issuePriority) {
		this.issuePriority = issuePriority;
	}

	public IssueStatut getIssueStatut() {
		return issueStatut;
	}

	public void setIssueStatut(IssueStatut issueStatut) {
		this.issueStatut = issueStatut;
	}

}
