package org.genericsystem.issuetracker.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.genericsystem.issuetracker.bean.IssueBean.ElStringWrapper;
import org.genericsystem.issuetracker.model.Comment;
import org.genericsystem.issuetracker.model.IssueComment;
import org.genericsystem.issuetracker.qualifier.Provide;
import org.genericsystem.mutability.Generic;

@Named
@RequestScoped
public class CommentBean {
	private static final Logger log = Logger.getAnonymousLogger();

	@Inject
	@Provide
	private IssueComment issueComment;

	@Inject
	@Provide
	private Comment comment;

	@Inject
	private IssueSelectionBean issueSelectionBean;

	private String newComment;

	private List<Generic> getCommentsByIssue(Generic instance) {
		return instance.getLinks(issueComment).get().collect(Collectors.toList());
	}

	public List<Generic> getComments() {
		Generic instance = issueSelectionBean.getSelectedIssue();
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

	public String deleteComment(Generic comment) {
		comment.remove();
		return "#";
	}

	public String getNewComment() {
		return newComment;
	}

	public void setNewComment(String newComment) {
		log.info("CommentBean ; setNewComment ; commentValue : " + newComment);
		this.newComment = newComment;
	}

}
