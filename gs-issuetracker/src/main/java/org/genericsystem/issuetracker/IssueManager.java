package org.genericsystem.issuetracker;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.genericsystem.cdi.CacheRequestProvider;
import org.genericsystem.cdi.CacheSessionProvider;
import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/issueManager")
public class IssueManager {

	protected static Logger log = LoggerFactory.getLogger(IssueManager.class);

	@Inject
	private CacheRequestProvider cacheRequestProvider;

	@Inject
	private CacheSessionProvider cacheSessionProvider;

	@POST
	@Path("createIssue")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createIssue(Issue issue) {
		Engine engine = cacheRequestProvider.getCurrentCache().getRoot();
		Generic issueGeneric = engine.setInstance("Issue");
		Generic descriptifAtt = issueGeneric.setAttribute("descriptif");
		Generic priorityAtt = issueGeneric.setAttribute("priority");
		Generic typeAtt = issueGeneric.setAttribute("type");

		Generic myIssue = issueGeneric.setInstance(issue.getId());
		myIssue.setHolder(descriptifAtt, issue.getDescriptif());
		myIssue.setHolder(priorityAtt, issue.getPriority());
		myIssue.setHolder(typeAtt, issue.getType());
		cacheRequestProvider.getCurrentCache().flush();
		return Response.status(200).build();
	}

	@GET
	@Path("getIssues")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getIssues() {
		Engine engine = cacheSessionProvider.getCurrentCache().getRoot();
		List<Issue> issues = new ArrayList<Issue>();
		Generic instance = engine.getInstance("Issue");
		if (instance != null)
			for (Generic issueGeneric : instance.getInstances()) {
				Issue issue = new Issue();
				issue.setId((String) issueGeneric.getValue());
				issue.setDescriptif(getHolderValue(issueGeneric, "descriptif"));
				issue.setPriority(getHolderValue(issueGeneric, "priority"));
				issue.setType(getHolderValue(issueGeneric, "type"));
				issues.add(issue);
			}
		IssueWrapper issueWrapper = new IssueWrapper();
		issueWrapper.setList(issues);
		return Response.status(200).entity(issueWrapper).build();
	}

	private String getHolderValue(Generic instance, String attributeName) {
		Generic attribute = instance.getAttributes().get().filter(x -> x.getValue().equals(attributeName)).findFirst().get();
		return (String) instance.getHolders(attribute).first().getValue();
	}

}
