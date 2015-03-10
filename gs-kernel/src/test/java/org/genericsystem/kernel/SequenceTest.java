package org.genericsystem.kernel;

import org.genericsystem.api.defaults.Generator.IntAutoIncrementGenerator;
import org.genericsystem.kernel.Config.Sequence;
import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.Generate;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.testng.annotations.Test;

@Test
public class SequenceTest extends AbstractTest {

	public void testFindSequence() {
		Root root = new Root();
		Generic sequence = root.find(Sequence.class);
		assert sequence != null;
		assert sequence.getMeta() == root.getMetaAttribute();
		assert sequence.getComponents().contains(root);
	}

	public void testStringAutoIncrementGenerator() {
		Root root = new Root(Issue.class);
		Generic issue = root.find(Issue.class);
		Generic myIssue = issue.addInstance();
		assert myIssue.getValue() instanceof String;
		assert ((String) myIssue.getValue()).contains(Issue.class.getSimpleName());
	}

	public void testIntAutoIncrementGenerator() {
		Root root = new Root(IssueInt.class);
		Generic issue = root.find(IssueInt.class);
		Generic myIssue = issue.addInstance();
		assert myIssue.getValue() instanceof Integer;
		assert ((Integer) myIssue.getValue()) == 0;
	}

	public void testHolderIntAutoIncrementGenerator() {
		Root root = new Root(Id.class);
		Generic id = root.find(Id.class);
		Generic vehicle = root.find(Vehicle.class);
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic myVehicleId = id.addInstance(myVehicle);
		assert myVehicleId.getValue() instanceof Integer;
		assert ((Integer) myVehicleId.getValue()) == 0;
		Generic myVehicleId2 = myVehicle.addHolder(id);
		assert ((Integer) myVehicleId2.getValue()) == 1;
	}

	@SystemGeneric
	@Generate
	public static class Issue {

	}

	@SystemGeneric
	@Generate(clazz = IntAutoIncrementGenerator.class)
	public static class IssueInt {

	}

	@SystemGeneric
	public static class Vehicle {

	}

	@SystemGeneric
	@Components(Vehicle.class)
	@Generate(clazz = IntAutoIncrementGenerator.class)
	public static class Id {

	}

}
