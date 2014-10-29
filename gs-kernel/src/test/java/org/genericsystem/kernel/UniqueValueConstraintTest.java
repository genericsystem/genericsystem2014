package org.genericsystem.kernel;

import org.testng.annotations.Test;

@Test
public class UniqueValueConstraintTest extends AbstractTest {

	public void test01_simpleCase() {
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex myVehicle2 = vehicle.addInstance("myVehicle2");
		Vertex power = root.addInstance("Power", vehicle);
		myVehicle.addHolder(power, "125");

		assert !myVehicle.isUniqueValueEnabled();
		myVehicle.enableUniqueValueConstraint();
		assert myVehicle.isUniqueValueEnabled();
		myVehicle.disableUniqueValueConstraint();
		assert !myVehicle.isUniqueValueEnabled();

		// myVehicle.enableUniqueValueConstraint();
		//
		// catchAndCheckCause(() -> myVehicle2.addHolder(power, "125"), UniqueValueConstraintViolationException.class);

	}
}
