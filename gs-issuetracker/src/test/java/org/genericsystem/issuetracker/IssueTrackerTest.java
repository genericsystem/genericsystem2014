package org.genericsystem.issuetracker;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.genericsystem.issuetracker.model.Comment;
import org.genericsystem.issuetracker.model.Issue;
import org.genericsystem.issuetracker.model.IssueComment;
import org.genericsystem.issuetracker.model.IssuePriority;
import org.genericsystem.issuetracker.model.IssueStatut;
import org.genericsystem.mutability.Engine;
import org.testng.annotations.Test;

@Test
public class IssueTrackerTest extends AbstractTest {

	@Inject
	Engine engine;

	@Inject
	Issue issue;

	// public void addNewIssue() {
	// Issue issue = engine.find(Issue.class);
	// issue.addInstance();
	// }

	public void test() {
		assert null != engine.find(IssuePriority.class);
		assert null != engine.find(IssueStatut.class);
	}

	public static interface A {
		String getNom();
	}

	public static class B implements A {

		@Override
		public String getNom() {
			return "### B";
		}
	}

	public static class ProviderA {
		@Produces
		public A getA(InjectionPoint ip) {
			return () -> "### A";
		}
	}

	public static class ProviderB {
		// @Produces
		// public B getB(InjectionPoint ip) {
		// return new B() {
		// @Override
		// public String getNom() {
		// return "C";
		// }
		// };
		// }
	}

	@Inject
	B b;

	public void testDInsertion() {
		System.out.println("@@@@@@@@@@" + b.getNom());
	}

	public void testWithoutCascadeRemove() {
		// Issue issue = engine.find(Issue.class);
		// Comment comment = engine.find(Comment.class);
		// IssueComment issueComment = engine.find(IssueComment.class);
		// Generic firstIssue = issue.addInstance("firstIssue");
		// Generic link = firstIssue.addLink(issueComment, "myComment", comment);
		// assert firstIssue.isAlive();
		// assert link.isAlive();
		// assert link.getTargetComponent().isAlive();
		// firstIssue.remove();
		// assert !firstIssue.isAlive();
		// assert !link.isAlive();
		// assert link.getTargetComponent().isAlive();
	}

	public void testWhithCascadeRemove() {
		Issue issue = engine.find(Issue.class);
		Comment comment = engine.find(Comment.class);
		IssueComment issueComment = engine.find(IssueComment.class);
		// issueComment.enableCascadeRemove(ApiStatics.TARGET_POSITION);
		// Generic secondIssue = issue.addInstance("firstIssue");
		// Generic link = secondIssue.addLink(issueComment, "yourComment", comment);
		// assert secondIssue.isAlive();
		// assert link.isAlive();
		// assert link.getTargetComponent().isAlive();
		// link.remove();
		// assert secondIssue.isAlive();
		// assert !link.isAlive();
		// assert !link.getTargetComponent().isAlive();
	}
}
