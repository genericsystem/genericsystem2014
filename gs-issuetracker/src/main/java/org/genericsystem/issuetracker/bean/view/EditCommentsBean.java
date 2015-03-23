package org.genericsystem.issuetracker.bean.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.issuetracker.bean.AbstractBean.ElStringWrapper;
import org.genericsystem.issuetracker.bean.CommentBean;
import org.genericsystem.mutability.Generic;

@Named
@RequestScoped
public class EditCommentsBean {
	private static final Logger log = Logger.getAnonymousLogger();

	@Inject
	private IssueSelectedBean issueSelectedBean;

	@Inject
	private CommentSelectedBean commentSelectedBean;

	@Inject
	private CommentBean commentBean;

	public List<String> getAllComments() {
		return commentBean.getAllComments();
	}

	public List<Generic> getList() {
		if (issueSelectedBean.getSelectedIssue() == null)
			return new ArrayList<Generic>();
		return commentBean.getCommentsByIssue(issueSelectedBean.getSelectedIssue());
	}

	public ElStringWrapper getComment(Generic issue) {
		// return commentBean.updateHolder(issue, null);
		// return commentBean.updateLink(issue, issueComment, commentSelectedBean.getSelected());
		return commentBean.getComment(issue, commentSelectedBean.getSelected());
	}

	public Serializable getValue(Generic comment) {
		return comment.getTargetComponent().getValue();
	}

	public String delete(Generic comment) {
		commentSelectedBean.setSelected(null);
		commentBean.deleteComment(comment);
		return "#";
	}

	public Generic getSelected() {
		return commentSelectedBean.getSelected();
	}

	public void setSelected(Generic selectedComment) {
		commentSelectedBean.setSelected(selectedComment);
	}

	public Generic getSelectedIssue() {
		if (issueSelectedBean.getSelectedIssue() != null)
			log.info("EditCommentBean ; getSelectedIssue : " + issueSelectedBean.getSelectedIssue().getValue());
		return issueSelectedBean.getSelectedIssue();
	}

	public void setSelectedIssue(Generic selectedIssue) {
		issueSelectedBean.setSelectedIssue(selectedIssue);
	}
}
