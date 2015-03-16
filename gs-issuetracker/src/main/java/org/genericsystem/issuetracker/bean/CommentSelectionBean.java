package org.genericsystem.issuetracker.bean;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.issuetracker.model.Comment;
import org.genericsystem.issuetracker.qualifier.Provide;
import org.genericsystem.mutability.Generic;

@Named
@SessionScoped
public class CommentSelectionBean implements Serializable {
	private static final long serialVersionUID = -7086725227684166363L;

	@Inject
	@Provide
	private transient Comment comment;

	private transient Generic searchedComment;

	public String getSearchedComment() {
		return (searchedComment != null) ? (String) searchedComment.getTargetComponent().getValue() : "---";
	}

	public void setSearchedComment(String searchedComment) {
		this.searchedComment = comment.setInstance(searchedComment);
	}

	public Generic getSelectedComment() {
		return searchedComment;
	}

	public String setSelectedComment(Generic searchedComment) {
		this.searchedComment = searchedComment;
		return "#";
	}

}
