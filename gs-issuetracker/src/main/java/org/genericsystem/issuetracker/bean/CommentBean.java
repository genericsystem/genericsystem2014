package org.genericsystem.issuetracker.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.genericsystem.issuetracker.bean.IssueBean.ElStringWrapper;
import org.genericsystem.issuetracker.model.Comment;
import org.genericsystem.issuetracker.model.Issue;
import org.genericsystem.issuetracker.model.IssueComment;
import org.genericsystem.issuetracker.qualifier.Provide;
import org.genericsystem.mutability.Generic;

public class CommentBean implements Serializable {
	private static final long serialVersionUID = 5563656957808416784L;

	@Inject
	@Provide
	private transient Issue issue;

	@Inject
	@Provide
	private transient IssueComment issueComment;

	@Inject
	@Provide
	private transient Comment comment;

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
				if (instance.setLink(issueComment, "link", comment.setInstance(value)) == null)
					instance.setLink(issueComment, "link", comment.setInstance(value));
				else if (selectedComment != null)
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

	public void deleteComment(Generic comment) {
		comment.remove();
	}

}
