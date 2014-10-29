package org.genericsystem.kernel;

import org.testng.annotations.Test;

@Test
public class InstanceValueClassConstraintTest extends AbstractTest {

	public void test01_simpleCase() {
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex myVehicle2 = vehicle.addInstance("myVehicle2");
		Vertex power = root.addInstance("Power", vehicle);
		// myVehicle.enableInstanceValueClassConstraint(String.class);
		//
		//
		// catchAndCheckCause(() -> myVehicle.addHolder(power, "125"), UniqueValueConstraintViolationException.class);

	}
}
