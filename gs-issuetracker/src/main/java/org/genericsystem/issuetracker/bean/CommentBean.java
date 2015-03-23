package org.genericsystem.issuetracker.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.genericsystem.issuetracker.model.Comment;
import org.genericsystem.issuetracker.model.Issue;
import org.genericsystem.issuetracker.model.IssueComment;
import org.genericsystem.issuetracker.qualifier.Provide;
import org.genericsystem.mutability.Generic;

public class CommentBean extends AbstractBean implements Serializable {
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

	public List<String> getAllComments() {
		return comment.getInstances().get().map(generic -> Objects.toString(generic.getValue())).collect(Collectors.toList());
	}

	public List<Generic> getCommentsByIssue(Generic issue) {
		return issue.getLinks(issueComment).get().collect(Collectors.toList());
	}

	public void deleteComment(Generic comment) {
		comment.remove();
	}

	public ElStringWrapper getComment(Generic issue, Generic selectedComment) {
		return new ElStringWrapper() {

			@Override
			public void setValue(String value) {
				if (selectedComment == null)
					issue.setLink(issueComment, null, comment.setInstance(value));
				else
					selectedComment.getTargetComponent().updateValue(value);
			}

			@Override
			public String getValue() {
				return selectedComment != null ? Objects.toString(selectedComment.getTargetComponent().getValue()) : "";
			}

			@Override
			public List<String> getValues() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void setValues(List<String> selectedTargets) {
				// TODO Auto-generated method stub

			}
		};
	}

}
