package org.genericsystem.kernel;

import org.genericsystem.kernel.Config.Sequence;
import org.testng.annotations.Test;

@Test
public class SequenceTest extends AbstractTest {

	public void testFindSequence() {
		Root root = new Root();
		Generic sequence = root.find(Sequence.class);
		assert sequence != null;
	}

	public void testFindSequenceViaGeneric() {
		Root root = new Root();
		Generic issue = root.addInstance("Issue");
		Generic sequence = issue.getRoot().find(Sequence.class);
		assert sequence != null;
		assert sequence.getMeta() == root.getMetaAttribute();
		assert sequence.getComponents().contains(root);
	}

	public void testGeneratorGetIncrementedValue() {
		Root root = new Root();
		Generic issue = root.addInstance("Issue");
		// Generic sequence = issue.getRoot().find(Sequence.class);
		Generic sequence = issue.getRoot().getSequence();
		Generic sequenceHolder = null;
		if (issue.getHolders(sequence).first() != null)
			sequenceHolder = issue.getHolders(sequence).first();
		int value = sequenceHolder != null ? (Integer) sequenceHolder.getValue() + 1 : 0;
		issue.setHolder(sequence, value);
		assert root != null;
		assert issue != null;
		assert sequence != null;

	}

}
