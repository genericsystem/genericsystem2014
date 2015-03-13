package org.genericsystem.issuetracker.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.issuetracker.bean.IssueBean.ElStringWrapper;
import org.genericsystem.issuetracker.model.Comment;
import org.genericsystem.issuetracker.model.Issue;
import org.genericsystem.issuetracker.model.IssueComment;
import org.genericsystem.issuetracker.qualifier.Provide;
import org.genericsystem.mutability.Generic;

@Named
@RequestScoped
public class CommentBean {

	@Inject
	@Provide
	private Issue issue;

	@Inject
	@Provide
	private IssueComment issueComment;

	@Inject
	@Provide
	private Comment comment;

	@Inject
	private IssueSelectionBean issueSelectionBean;

	public List<String> getCommentsByIssue(Generic instance) {
		return instance.getLinks(issueComment).get().map(generic -> Objects.toString(generic.getTargetComponent().getValue())).collect(Collectors.toList());
	}

	public List<String> getComments() {
		Generic instance = issue.getInstance(issueSelectionBean.getSearchedIssue());
		return (instance != null) ? getCommentsByIssue(instance) : new ArrayList<>();
	}

	public ElStringWrapper addComment(Generic instance) {
		return new ElStringWrapper() {

			@Override
			public void setValue(String value) {
				instance.setLink(issueComment, "link", comment.setInstance(value));
			}

			@Override
			public String getValue() {
				return "";
			}
		};
	}

	public String deleteComment(String commentValue) {
		Generic commentToDelete = comment.getInstance(commentValue);
		(commentToDelete.getLink(issueComment, commentToDelete)).remove();
		commentToDelete.remove();
		return "#";
	}
}
