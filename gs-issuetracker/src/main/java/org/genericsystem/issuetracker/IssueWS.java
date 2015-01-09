package org.genericsystem.issuetracker;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.genericsystem.issuetracker.crud.IssueCRUD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/issueManager")
public class IssueWS {

	protected static Logger log = LoggerFactory.getLogger(IssueWS.class);

	@Inject
	private IssueCRUD issueCrud;

	@POST
	@Path("createIssue")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createIssue(IssueDTO issue) {
		issueCrud.createIssue(issue);
		return Response.status(200).build();
	}

	@GET
	@Path("getIssues")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getIssues() {
		return Response.status(200).entity(issueCrud.getIssues()).build();
	}

}
