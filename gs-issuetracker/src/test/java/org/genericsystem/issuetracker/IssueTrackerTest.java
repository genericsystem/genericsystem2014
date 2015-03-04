package org.genericsystem.issuetracker;

import javax.inject.Inject;

import org.genericsystem.issuetracker.model.IssuePriority;
import org.genericsystem.issuetracker.model.IssueStatut;
import org.genericsystem.mutability.Engine;
import org.testng.annotations.Test;

@Test
public class IssueTrackerTest extends AbstractTest {

	@Inject
	Engine engine;

	public void test() {
		assert null != engine.find(IssuePriority.class);
		assert null != engine.find(IssueStatut.class);
	}
}
