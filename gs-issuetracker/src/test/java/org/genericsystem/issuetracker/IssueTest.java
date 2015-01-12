package org.genericsystem.issuetracker;

import java.util.List;

import javax.inject.Inject;

import org.genericsystem.issuetracker.crud.IssueCRUD;
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
}
