package org.genericsystem.issuetracker.bean;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.cdi.Engine;
import org.genericsystem.issuetracker.model.Description;
import org.genericsystem.issuetracker.model.Issue;
import org.genericsystem.issuetracker.model.IssueStatut;
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
	private IssueStatut issueStatut;

	@Inject
	private FilterBean filterBean;

	public List<Generic> getIssuesByStatut() {
		return (filterBean.getPredicate(issueStatut) != null) ? issue.getAllInstances().get().filter(filterBean.getPredicate(issueStatut)).collect(Collectors.toList()) : issue.getAllInstances().get().collect(Collectors.toList());
	}

	public String addIssue(String newIssueDescription) {
		issue.addGenerateInstance().setHolder(description, newIssueDescription);
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

	public ElStringWrapper getLink(Generic instance, Generic target) {
		return new ElStringWrapper() {

			@Override
			public void setValue(String value) {
				Generic searchedPriority = target.getTargetComponent().getInstance(value);
				instance.setLink(target, "link", searchedPriority);
			}

			@Override
			public String getValue() {
				Generic link = instance.getLinks(target).first();
				return (link != null) ? (String) link.getTargetComponent().getValue() : null;
			}
		};
	}

	public interface ElStringWrapper {
		public String getValue();

		public void setValue(String value);
	}

}
