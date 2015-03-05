package org.genericsystem.issuetracker.bean;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.cdi.Engine;
import org.genericsystem.issuetracker.annotation.InjectedClass;
import org.genericsystem.issuetracker.model.Description;
import org.genericsystem.issuetracker.model.Issue;
import org.genericsystem.issuetracker.model.IssueNumber;
import org.genericsystem.issuetracker.model.IssuePriority;
import org.genericsystem.issuetracker.model.IssueStatut;
import org.genericsystem.issuetracker.model.Priority;
import org.genericsystem.issuetracker.model.Statut;
import org.genericsystem.issuetracker.qualifier.Provide;
import org.genericsystem.mutability.Generic;

@Named
@RequestScoped
public class IssueBean {

	@Inject
	private Engine engine;

	@Inject
	@Provide
	@InjectedClass(Issue.class)
	private Generic issue;

	@Inject
	@Provide
	@InjectedClass(Description.class)
	private Generic description;

	@Inject
	@Provide
	@InjectedClass(IssuePriority.class)
	private Generic issuePriority;

	@Inject
	@Provide
	@InjectedClass(Priority.class)
	private Generic priority;

	@Inject
	@Provide
	@InjectedClass(IssueStatut.class)
	private Generic issueStatut;

	@Inject
	@Provide
	@InjectedClass(Statut.class)
	private Generic statut;

	@Inject
	@Provide
	@InjectedClass(IssueNumber.class)
	private Generic issueNumber;

	private String newIssueDescription;

	public List<Generic> getIssues() {
		return issue.getAllInstances().get().collect(Collectors.toList());
	}

	public String addIssue() {
		Snapshot<Generic> instancesIssueNumber = issueNumber.getInstances();
		Integer lastIssueNumber = instancesIssueNumber.size();
		issueNumber.setInstance(lastIssueNumber + 1);
		String newIssueName = ((Issue) issue).getName() + lastIssueNumber;
		issue.setInstance(newIssueName).setHolder(description, newIssueDescription);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Priority is required on " + newIssueName));
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
				instance.setLink(issuePriority, "linkStatut", searchedPriority);
			}

			@Override
			public String getValue() {
				Generic link = instance.getLinks(issuePriority).first();
				return (link != null) ? (String) link.getTargetComponent().getValue() : null;
			}
		};
	}

	public ElStringWrapper getStatut(Generic instance) {
		return new ElStringWrapper() {

			@Override
			public void setValue(String value) {
				Generic searchedStatut = statut.getInstance(value);
				instance.setLink(issueStatut, "linkStatut", searchedStatut);
			}

			@Override
			public String getValue() {
				Generic link = instance.getLinks(issueStatut).first();
				return (link != null) ? (String) link.getTargetComponent().getValue() : null;
			}
		};
	}

	public interface ElStringWrapper {
		public String getValue();

		public void setValue(String value);
	}

	public String getNewIssueDescription() {
		return newIssueDescription;
	}

	public void setNewIssueDescription(String newIssueDescription) {
		this.newIssueDescription = newIssueDescription;
	}

}
