package org.genericsystem.kernel;

import org.genericsystem.api.core.ApiStatics;
import org.testng.annotations.Test;

@Test
public class RequiredConstraintTest {

	public void testRequiredConstraint() {
		Root engine = new Root();
		Generic issue = engine.addInstance("Issue");
		Generic priority = engine.addInstance("Priority");
		Generic statut = engine.addInstance("Statut");

		Generic description = issue.addAttribute("description");
		Generic issuePriority = issue.setRelation("IssuePriority", priority);
		Generic issueStatut = issue.setRelation("IssueStatut", statut);
		issuePriority.enableSingularConstraint(ApiStatics.BASE_POSITION);
		issuePriority.enableRequiredConstraint(ApiStatics.BASE_POSITION);
		issueStatut.enableSingularConstraint(ApiStatics.BASE_POSITION);

		Generic myPriority = priority.addInstance("myPriority");
		Generic myStatut = statut.addInstance("myStatut");
		Generic myIssue = issue.addInstance("myIssue");

		issue.setLink(issueStatut, "myIssueStatut", myStatut);
		issue.setHolder(description, "myDescription");
		Generic myIssueWithPriority = myIssue.setLink(issuePriority, "myIssueWithPriority", myPriority);

		myIssue.remove();
		assert !myIssue.isAlive();
		assert !myIssueWithPriority.isAlive();
		assert issue.isAlive();
		assert priority.isAlive();
		assert issuePriority.isAlive();
		assert myPriority.isAlive();
		assert !myIssue.isAlive();
		assert !myIssueWithPriority.isAlive();
	}

}
