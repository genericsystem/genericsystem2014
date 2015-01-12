package org.genericsystem.issuetracker;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.genericsystem.issuetracker.crud.IssueCRUD;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.util.GenericType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

@Test
public class IssueTest extends AbstractTest {
	protected static Logger log = LoggerFactory.getLogger(IssueTest.class);

	@Inject
	private IssueCRUD issueCRUD;

	public void createIssue() {
		IssueDTO issueDTO = new IssueDTO();
		issueDTO.setId("1");
		issueDTO.setDescriptif("Test");
		issueDTO.setPriority("ERROR");
		issueDTO.setType("BUG");
		issueCRUD.createIssue(issueDTO);
		List<IssueDTO> issues = issueCRUD.getIssues();
		assert issues.size() == 1;
		issueDTO = issues.get(0);
		assert issueDTO.getId().equals("1");
		assert issueDTO.getDescriptif().equals("Test");
		assert issueDTO.getPriority().equals("ERROR");
		assert issueDTO.getType().equals("BUG");
	}

	@SuppressWarnings("deprecation")
	public void createIssueWS() {
		String BASE_URL = "http://localhost:8080/gs-issuetracker/rest/issueManager";
		try {
			IssueDTO issueDTO = new IssueDTO();
			issueDTO.setId("1");
			issueDTO.setDescriptif("Test");
			issueDTO.setPriority("ERROR");
			issueDTO.setType("BUG");

			ClientRequest request = new ClientRequest(BASE_URL + "/createIssue");
			request.body(MediaType.APPLICATION_JSON, issueDTO);
			ClientResponse<String> response = request.post(String.class);

			assert response.getEntity() != null;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			assert false;
		}

		List<IssueDTO> issues = null;
		try {
			ClientRequest request = new ClientRequest(BASE_URL + "/getIssues");
			request.accept(MediaType.APPLICATION_JSON);
			ClientResponse<List<IssueDTO>> response = request.get(new GenericType<List<IssueDTO>>() {
			});
			assert response.getEntity() != null;
			issues = response.getEntity();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			assert false;
		}

		assert issues.size() == 1;
		IssueDTO issueDTO = issues.get(0);
		assert issueDTO.getId().equals("1");
		assert issueDTO.getDescriptif().equals("Test");
		assert issueDTO.getPriority().equals("ERROR");
		assert issueDTO.getType().equals("BUG");
	}

}
