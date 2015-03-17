package org.genericsystem.issuetracker.bean;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.issuetracker.model.Issue;
import org.genericsystem.issuetracker.qualifier.Provide;
import org.genericsystem.mutability.Generic;

@Named
@SessionScoped
public class IssueSelectionBean implements Serializable {
	private static final long serialVersionUID = -6707062384436255711L;

	@Inject
	@Provide
	private transient Issue issue;

	private transient Generic searchedIssue;

	public String getSearchedIssue() {
		return (searchedIssue != null) ? (String) searchedIssue.getValue() : "---";
	}

	public void setSearchedIssue(String searchedIssue) {
		this.searchedIssue = issue.setInstance(searchedIssue);
	}

	public Generic getSelectedIssue() {
		return searchedIssue;
	}

	public Generic setSelectedIssue(Generic searchedIssue) {
		this.searchedIssue = searchedIssue;// TODO ?
		return this.searchedIssue;
	}

}
