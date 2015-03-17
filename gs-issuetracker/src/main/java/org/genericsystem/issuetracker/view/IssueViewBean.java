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

	private List<Generic> issues;
	private Generic selectedIssue;
	private List<String> priorities;
	private List<String> statuts;
	private String newIssueDescription;

	public String addIssue() {
		return issueBean.addIssue(newIssueDescription);
	}

	public String delete(Generic issue) {
		return issueBean.deleteIssue(issue);
	}

	public ElStringWrapper getDescription(Generic instance) {
		return issueBean.getDescription(instance);
	}

	public ElStringWrapper getStatut(Generic instance) {
		return issueBean.getStatut(instance);
	}

	public ElStringWrapper getPriority(Generic instance) {
		return issueBean.getPriority(instance);
	}

	public List<Generic> getList() {
		issues = issueBean.getIssuesByStatut();
		return issues;
	}

	public Generic getSelected() {
		selectedIssue = issueSelectionBean.getSelectedIssue();
		return selectedIssue;
	}

	public String setSelected(Generic selectedIssue) {
		this.selectedIssue = issueSelectionBean.setSelectedIssue(selectedIssue);
		return "#";
	}

	public String getSearchedIssue() {
		return issueSelectionBean.getSearchedIssue();
	}

	public void setSearchedIssue(String searchedIssue) {
		issueSelectionBean.setSearchedIssue(searchedIssue);
	}

	public List<String> getPriorities() {
		priorities = priorityBean.getPriorities();
		return priorities;
	}

	public List<String> getStatuts() {
		statuts = statutBean.getStatuts();
		return statuts;
	}

	public String getNewIssueDescription() {
		return newIssueDescription;
	}

	public void setNewIssueDescription(String newIssueDescription) {
		this.newIssueDescription = newIssueDescription;
	}

}
