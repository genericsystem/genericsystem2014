package org.genericsystem.kernel;

import org.genericsystem.api.core.annotations.Components;
import org.genericsystem.api.core.annotations.SystemGeneric;
import org.genericsystem.defaults.DefaultGenerator.IntAutoIncrementGenerator;
import org.genericsystem.defaults.annotation.GenerateValue;
import org.testng.annotations.Test;

@Test
public class SequenceTest extends AbstractTest {

	public void testFindSequence() {
		Root root = new Root();
		Generic sequence = root.getSequence();
		assert sequence != null;
		assert sequence.getMeta() == root.getMetaAttribute();
		assert sequence.getComponents().contains(root);
	}

	public void testStringAutoIncrementGenerator() {
		Root root = new Root(Car.class);
		Generic car = root.find(Car.class);
		Generic myBmw = car.addGenerateInstance();
		assert myBmw.getValue() instanceof String;
		assert ((String) myBmw.getValue()).contains(Car.class.getSimpleName());
	}

	public void testIntAutoIncrementGenerator() {
		Root root = new Root(CarInt.class);
		Generic car = root.find(CarInt.class);
		Generic myBmw = car.addGenerateInstance();
		assert myBmw.getValue() instanceof Integer;
		assert ((Integer) myBmw.getValue()) == 0;
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
	public static class Car {

	}

	@SystemGeneric
	@GenerateValue(clazz = IntAutoIncrementGenerator.class)
	public static class CarInt {

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
