package org.genericsystem.issuetracker.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.genericsystem.issuetracker.model.Description;
import org.genericsystem.issuetracker.model.Issue;
import org.genericsystem.issuetracker.model.IssuePriority;
import org.genericsystem.issuetracker.model.IssueStatut;
import org.genericsystem.issuetracker.model.IssueVersion;
import org.genericsystem.issuetracker.model.Version;
import org.genericsystem.issuetracker.qualifier.Provide;
import org.genericsystem.mutability.Generic;

public class IssueBean implements Serializable {

	private static final long serialVersionUID = 4142394683395145575L;

	@Inject
	@Provide
	private transient Issue issue;

	@Inject
	@Provide
	private transient Description description;

	@Inject
	@Provide
	private transient IssueStatut issueStatut;

	@Inject
	@Provide
	private transient IssuePriority issuePriority;

	@Inject
	@Provide
	private transient IssueVersion issueVersion;

	@Inject
	@Provide
	private transient Version version;

	@Inject
	private transient FilterBean filterBean;

	private transient List<String> selectedVersions;

	public void addIssue(String newIssueDescription) {
		issue.addGenerateInstance().setHolder(description, newIssueDescription);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Priority is required."));
	}

	public List<Generic> getIssuesByStatut() {
		return (filterBean.getPredicate(issueStatut) != null) ? issue.getAllInstances().get().filter(filterBean.getPredicate(issueStatut)).collect(Collectors.toList()) : issue.getAllInstances().get().collect(Collectors.toList());
	}

	public void deleteIssue(Generic issue) {
		issue.remove();
	}

	public ElStringWrapper getLink(Generic instance, Generic relation) {
		return new ElStringWrapper() {

			@Override
			public void setValue(String value) {
				Generic searchedTarget = relation.getTargetComponent();
				if (searchedTarget == null)
					instance.setHolder(relation, value);
				else
					instance.setHolder(relation, "link", searchedTarget.getInstance(value));
			}

			@Override
			public String getValue() {
				Generic link = instance.getLinks(relation).first();
				return (link != null) ? (link.getTargetComponent() != null) ? (String) link.getTargetComponent().getValue() : Objects.toString(instance.getValues(description).first()) : null;
			}
		};
	}

	public void selectIssueVersions(Generic selectedIssue) {
		selectedVersions = selectedIssue.getLinks(issueVersion).get().map(generic -> Objects.toString(generic.getValue())).collect(Collectors.toList());
	}

	public void addVersionsToIssue(Generic instance) {
		if (selectedVersions != null)
			for (String string : selectedVersions) {
				instance.setLink(issueVersion, string, version);
			}
		for (Generic generic : instance.getLinks(issueVersion).get().collect(Collectors.toList()))
			if (!selectedVersions.contains(generic.getValue()))
				instance.getLink(issueVersion, generic.getValue(), version).remove();

	}

	public interface ElStringWrapper {
		public String getValue();

		public void setValue(String value);
	}

	public Description getDescription() {
		return description;
	}

	public IssueStatut getIssueStatut() {
		return issueStatut;
	}

	public IssuePriority getIssuePriority() {
		return issuePriority;
	}

	public List<String> getSelectedVersions() {
		return selectedVersions;
	}

	public void setSelectedVersions(List<String> selectedVersions) {
		this.selectedVersions = selectedVersions;
	}

}
