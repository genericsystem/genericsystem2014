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

	private List<Generic> getCommentsByIssue(Generic instance) {
		return instance.getLinks(issueComment).get().collect(Collectors.toList());
	}

	public List<Generic> getComments(Generic instance) {
		return (instance != null) ? getCommentsByIssue(instance) : new ArrayList<>();
	}

	public ElStringWrapper getComment(Generic instance, Generic selectedComment) {
		return new ElStringWrapper() {

			@Override
			public void setValue(String value) {
				instance.setLink(issueComment, "link", comment.setInstance(value));
				selectedComment.getTargetComponent().updateValue(value);
			}

			@Override
			public String getValue() {
				String returnString = null;
				if (selectedComment != null)
					returnString = Objects.toString(selectedComment.getTargetComponent().getValue());
				return (returnString != null) ? returnString : "";
			}
		};
	}

	public String deleteComment(Generic comment) {
		comment.remove();
		return "#";
	}

}
