package org.genericsystem.issuetracker.view;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.issuetracker.bean.CommentBean;
import org.genericsystem.issuetracker.bean.CommentSelectionBean;
import org.genericsystem.issuetracker.bean.IssueBean.ElStringWrapper;
import org.genericsystem.issuetracker.bean.IssueSelectionBean;
import org.genericsystem.mutability.Generic;

@Named
@RequestScoped
public class CommentViewBean {

	@Inject
	private IssueSelectionBean issueSelectionBean;

	@Inject
	private CommentSelectionBean commentSelectionBean;

	@Inject
	private CommentBean commentBean;

	private Generic selectedComment;
	private Generic selectedIssue;
	private String newComment;

	public List<Generic> getList() {
		Generic instance = issueSelectionBean.getSelectedIssue();
		return commentBean.getComments(instance);
	}

	public ElStringWrapper getComment(Generic instance) {
		return commentBean.getComment(instance, selectedComment);
	}

	public String delete(Generic comment) {
		return commentBean.deleteComment(comment);
	}

	public String getNewComment() {
		return newComment;
	}

	public void setNewComment(String newComment) {
		this.newComment = newComment;
	}

	public Generic getSelected() {
		selectedComment = commentSelectionBean.getSelectedComment();
		return selectedComment;
	}

	public String setSelected(Generic selectedComment) {
		this.selectedComment = commentSelectionBean.setSelectedComment(selectedComment);
		return "#";
	}

	public String getSearchedComment() {
		return commentSelectionBean.getSearchedComment();
	}

	public void setSearchedComment(String searchedComment) {
		commentSelectionBean.setSearchedComment(searchedComment);
	}

	public Generic getSelectedIssue() {
		selectedIssue = issueSelectionBean.getSelectedIssue();
		return selectedIssue;
	}

	public void setSelectedIssue(Generic selectedIssue) {
		this.selectedIssue = issueSelectionBean.setSelectedIssue(selectedIssue);
	}

}
