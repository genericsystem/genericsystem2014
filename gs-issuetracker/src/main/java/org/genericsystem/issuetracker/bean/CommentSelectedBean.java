package org.genericsystem.issuetracker.bean;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.issuetracker.bean.IssueBean.ElStringWrapper;
import org.genericsystem.mutability.Generic;

@Named
@SessionScoped
public class CommentSelectedBean implements Serializable {
	private static final long serialVersionUID = -7086725227684166363L;

	@Inject
	private IssueSelectedBean issueSelectedBean;

	@Inject
	private CommentBean commentBean;

	private transient Generic selectedComment;

	public ElStringWrapper getComment(Generic instance) {
		return commentBean.getComment(instance, selectedComment);
	}

	public String getSearchedComment() {
		return (selectedComment != null) ? (String) selectedComment.getTargetComponent().getValue() : "---";
	}

	public Generic getSelected() {
		return selectedComment;
	}

	public Generic setSelected(Generic selectedComment) {
		this.selectedComment = selectedComment;
		return this.selectedComment;
	}

	public Generic getSelectedIssue() {
		return issueSelectedBean.getSelectedIssue();
	}

	public void setSelectedIssue(Generic selectedIssue) {
		issueSelectedBean.setSelectedIssue(selectedIssue);
	}

}
