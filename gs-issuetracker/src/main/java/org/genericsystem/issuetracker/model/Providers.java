package org.genericsystem.issuetracker.model;

import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.genericsystem.cdi.Engine;
import org.genericsystem.issuetracker.qualifier.Provide;

@ApplicationScoped
public class Providers {
	private static final Logger log = Logger.getAnonymousLogger();

	@Inject
	private transient Engine engine;

	@Produces
	@Provide
	public Issue getIssue() {
		log.info("Providers ; getIssue ; engine : " + engine);
		log.info("Providers ; getIssue ; issue : " + engine.find(Issue.class));
		return engine.find(Issue.class);
	}

	@Produces
	@Provide
	public Description getDescription() {
		log.info("Providers ; getIssue ; engine : " + engine);
		log.info("Providers ; getIssue ; description : " + engine.find(Description.class));
		return engine.find(Description.class);
	}

	@Produces
	@Provide
	public IssuePriority getIssuePriority() {
		log.info("Providers ; getIssue ; engine : " + engine);
		log.info("Providers ; getIssue ; issuePriority : " + engine.find(IssuePriority.class));
		return engine.find(IssuePriority.class);
	}

}
