package org.genericsystem.issuetracker.model;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.genericsystem.cdi.Engine;
import org.genericsystem.issuetracker.qualifier.Provide;

@ApplicationScoped
public class Providers {

	@Inject
	private transient Engine engine;

	@Produces
	@Provide
	public Issue getIssue() {
		return engine.find(Issue.class);
	}

	@Produces
	@Provide
	public Description getDescription() {
		return engine.find(Description.class);
	}

	@Produces
	@Provide
	public IssuePriority getIssuePriority() {
		return engine.find(IssuePriority.class);
	}

	@Produces
	@Provide
	public Priority getPriority() {
		return engine.find(Priority.class);
	}

	@Produces
	@Provide
	public IssueStatut getIssueStatut() {
		return engine.find(IssueStatut.class);
	}

	@Produces
	@Provide
	public Statut getStatut() {
		return engine.find(Statut.class);
	}

	@Produces
	@Provide
	public IssueComment getIssueComment() {
		return engine.find(IssueComment.class);
	}

	@Produces
	@Provide
	public Comment getComment() {
		return engine.find(Comment.class);
	}
}
