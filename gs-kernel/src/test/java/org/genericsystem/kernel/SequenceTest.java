package org.genericsystem.kernel;

import org.genericsystem.api.core.annotations.Components;
import org.genericsystem.api.core.annotations.SystemGeneric;
import org.genericsystem.defaults.GenerateValue;
import org.genericsystem.defaults.Generator.IntAutoIncrementGenerator;
import org.genericsystem.kernel.Config.Sequence;
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
		Generic myIssue = issue.addGenerateInstance();
		assert myIssue.getValue() instanceof String;
		assert ((String) myIssue.getValue()).contains(Issue.class.getSimpleName());
	}

	public void testIntAutoIncrementGenerator() {
		Root root = new Root(IssueInt.class);
		Generic issue = root.find(IssueInt.class);
		Generic myIssue = issue.addGenerateInstance();
		assert myIssue.getValue() instanceof Integer;
		assert ((Integer) myIssue.getValue()) == 0;
	}

	public void testHolderIntAutoIncrementGenerator() {
		Root root = new Root(Id.class);
		Generic id = root.find(Id.class);
		Generic vehicle = root.find(Vehicle.class);
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic myVehicleId = id.addGenerateInstance(myVehicle);
		assert myVehicleId.getValue() instanceof Integer;
		assert ((Integer) myVehicleId.getValue()) == 0;
		Generic myVehicleId2 = id.addGenerateInstance(myVehicle);
		assert ((Integer) myVehicleId2.getValue()) == 1;
	}

	@SystemGeneric
	@GenerateValue
	public static class Issue {

	}

	@SystemGeneric
	@GenerateValue(clazz = IntAutoIncrementGenerator.class)
	public static class IssueInt {

	}

	@SystemGeneric
	public static class Vehicle {

	}

	@SystemGeneric
	@Components(Vehicle.class)
	@GenerateValue(clazz = IntAutoIncrementGenerator.class)
	public static class Id {

	}

}
