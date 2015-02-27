package org.genericsystem.issuetracker.bean;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.cdi.Engine;
import org.genericsystem.issuetracker.model.Description;
import org.genericsystem.issuetracker.model.Issue;
import org.genericsystem.issuetracker.model.IssuePriority;
import org.genericsystem.issuetracker.model.Priority;
import org.genericsystem.issuetracker.qualifier.Provide;
import org.genericsystem.mutability.Generic;

@Named
@RequestScoped
public class IssueBean {

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

	@Inject
	@Provide
	private Priority priority;

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

	public ElStringWrapper getPriority(Generic instance) {
		return new ElStringWrapper() {

			@Override
			public void setValue(String value) {
				Generic searchedPriority = priority.getInstance(value);
				instance.setLink(issuePriority, "link", searchedPriority);
			}

			@Override
			public String getValue() {
				Generic link = instance.getLinks(issuePriority).first();
				return (link != null) ? (String) link.getTargetComponent().getValue() : null;
			}
		};
	}

	public interface ElStringWrapper {
		public String getValue();

		public void setValue(String value);
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
