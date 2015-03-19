package org.genericsystem.issuetracker.view;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.issuetracker.bean.CommentBean;
import org.genericsystem.issuetracker.bean.CommentSelectedBean;
import org.genericsystem.issuetracker.bean.IssueBean.ElStringWrapper;
import org.genericsystem.issuetracker.bean.IssueSelectedBean;
import org.genericsystem.mutability.Generic;

@Named
@RequestScoped
public class EditCommentsBean {

	@Inject
	private IssueSelectedBean issueSelectionBean;

	@Inject
	private CommentSelectedBean commentSelectionBean;

	@Inject
	private CommentBean commentBean;

	public List<Generic> getList() {
		return commentBean.getComments(issueSelectionBean.getSelectedIssue());
	}

	public ElStringWrapper getComment(Generic instance) {
		return commentBean.getComment(instance, commentSelectionBean.getSelected());
	}

	public String getValue(Generic generic) {
		return (String) generic.getTargetComponent().getValue();
	}

	public String delete(Generic comment) {
		commentSelectionBean.setSelected(null);
		commentBean.deleteComment(comment);
		return "#";
	}

	public Generic getSelected() {
		return commentSelectionBean.getSelected();
	}

	public String setSelected(Generic selectedComment) {
		commentSelectionBean.setSelected(selectedComment);
		return "#";
	}

	public Generic getSelectedIssue() {
		return issueSelectionBean.getSelectedIssue();
	}

	public void setSelectedIssue(Generic selectedIssue) {
		issueSelectionBean.setSelectedIssue(selectedIssue);
	}
}
