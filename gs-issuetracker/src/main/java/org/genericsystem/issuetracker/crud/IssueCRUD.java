package org.genericsystem.issuetracker.crud;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.genericsystem.cdi.Engine;
import org.genericsystem.issuetracker.IssueDTO;
import org.genericsystem.issuetracker.crud.Issue.Descriptif;
import org.genericsystem.issuetracker.crud.Issue.Priority;
import org.genericsystem.issuetracker.crud.Issue.Type;
import org.genericsystem.mutability.Generic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IssueCRUD {

	protected static Logger log = LoggerFactory.getLogger(IssueCRUD.class);

	@Inject
	private Engine engine;

	public void createIssue(IssueDTO issueDTO) {
		Generic issue = engine.find(Issue.class);
		Generic descriptifAtt = engine.find(Descriptif.class);
		Generic priorityAtt = engine.find(Priority.class);
		Generic typeAtt = engine.find(Type.class);

		Generic myIssue = issue.setInstance(issueDTO.getId());
		myIssue.setHolder(descriptifAtt, issueDTO.getDescriptif());
		myIssue.setHolder(priorityAtt, issueDTO.getPriority());
		myIssue.setHolder(typeAtt, issueDTO.getType());
		engine.getCurrentCache().flush();
	}

	public List<IssueDTO> getIssues() {
		List<IssueDTO> issues = new ArrayList<IssueDTO>();
		Generic issueType = engine.find(Issue.class);
		if (issueType != null)
			for (Generic issueGeneric : issueType.getInstances()) {
				IssueDTO issue = new IssueDTO();
				issue.setId((String) issueGeneric.getValue());
				issue.setDescriptif(getHolderValue(issueGeneric, Descriptif.class));
				issue.setPriority(getHolderValue(issueGeneric, Priority.class));
				issue.setType(getHolderValue(issueGeneric, Type.class));
				issues.add(issue);
			}
		return issues;
	}

	private String getHolderValue(Generic instance, Serializable attributeName) {
		return (String) instance.getHolders(instance.getAttribute(attributeName)).first().getValue();
	}

}
