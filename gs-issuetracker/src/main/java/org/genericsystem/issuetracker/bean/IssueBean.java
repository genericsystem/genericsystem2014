package org.genericsystem.issuetracker.bean;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.cdi.Engine;
import org.genericsystem.issuetracker.annotation.InjectedClass;
import org.genericsystem.issuetracker.model.Description;
import org.genericsystem.issuetracker.model.Issue;
import org.genericsystem.issuetracker.model.IssuePriority;
import org.genericsystem.issuetracker.model.IssueStatut;
import org.genericsystem.issuetracker.model.Priority;
import org.genericsystem.issuetracker.model.Statut;
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

	private String newIssueDescription;
	private String searchedStatut;

	public List<Generic> getIssues() {
		return issue.getAllInstances().get().collect(Collectors.toList());
	}

	public List<Generic> getIssuesByStatut() {
		return issue.getAllInstances().get().filter(generic -> generic.getLinks(issueStatut).get().anyMatch(link -> link.getTargetComponent().getValue().equals(this.searchedStatut))).collect(Collectors.toList());
	}

	public String addIssue() {
		issue.addInstance().setHolder(description, newIssueDescription);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Priority is required."));
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

	public String getSearchedStatut() {
		return searchedStatut;
	}

	public void setSearchedStatut(String searchedStatut) {
		this.searchedStatut = searchedStatut;
	}

}
