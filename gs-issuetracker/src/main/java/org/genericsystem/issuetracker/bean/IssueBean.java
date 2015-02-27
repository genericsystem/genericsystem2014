package org.genericsystem.issuetracker.bean;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.cdi.Engine;
import org.genericsystem.issuetracker.model.Description;
import org.genericsystem.issuetracker.model.Issue;
import org.genericsystem.issuetracker.model.IssuePriority;
import org.genericsystem.issuetracker.qualifier.Provide;
import org.genericsystem.mutability.Generic;

@Named
@RequestScoped
public class IssueBean {
	private static final Logger log = Logger.getAnonymousLogger();

	@Inject
	private Engine engine;

	@Inject
	@Provide
	private Issue issue;

	@Inject
	@Provide
	private Description description;

	@Inject
	@Provide
	private IssuePriority issuePriority;

	private String newIssueName;
	private String newIssueDescription;

	public List<Generic> getIssues() {
		return ((Generic) issue).getAllInstances().get().collect(Collectors.toList());
	}

	public String addIssue() {
		((Generic) issue).setInstance(newIssueName).setHolder(description, newIssueDescription);
		return "#";
	}

	public String deleteIssue(Generic issue) {
		issue.remove();
		return "#";
	}

	public String flush() {
		engine.getCurrentCache().flush();
		return "#";
	}

	public String clear() {
		engine.getCurrentCache().clear();
		return "#";
	}

	public ElStringWrapper getDescription(Generic instance) {
		return new ElStringWrapper() {

			@Override
			public void setValue(String value) {
				instance.setHolder(description, value);
			}

			@Override
			public String getValue() {
				return Objects.toString(instance.getValues(description).first());
			}
		};
	}

	public ElGenericWrapper getPriority(Generic instance) {
		return new ElGenericWrapper() {

			@Override
			public void setValue(Generic priority) {
				instance.setLink(issuePriority, "link", priority);
			}

			@Override
			public Generic getValue() {
				Generic link = instance.getLinks(issuePriority).first();
				return (link != null) ? link.getTargetComponent() : null;
			}
		};
	}

	public interface ElStringWrapper {
		public String getValue();

		public void setValue(String value);
	}

	public interface ElGenericWrapper {
		public Generic getValue();

		public void setValue(Generic priority);
	}

	public String getNewIssueName() {
		return newIssueName;
	}

	public void setNewIssueName(String newIssueName) {
		this.newIssueName = newIssueName;
	}

	public String getNewIssueDescription() {
		return newIssueDescription;
	}

	public void setNewIssueDescription(String newIssueDescription) {
		this.newIssueDescription = newIssueDescription;
	}

}
