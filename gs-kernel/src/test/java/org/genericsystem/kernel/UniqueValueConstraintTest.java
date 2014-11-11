package org.genericsystem.kernel;

import org.genericsystem.api.exception.UniqueValueConstraintViolationException;
import org.testng.annotations.Test;

@Test
public class UniqueValueConstraintTest extends AbstractTest {

	public void test01_enableConstraint() {
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex power = root.addInstance("Power", vehicle);
		myVehicle.addHolder(power, "125");

		assert !vehicle.isUniqueValueEnabled();
		vehicle.enableUniqueValueConstraint();
		assert vehicle.isUniqueValueEnabled();
		vehicle.disableUniqueValueConstraint();
		assert !vehicle.isUniqueValueEnabled();

	}

	public void test02_erorrCase() {
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex myVehicle2 = vehicle.addInstance("myVehicle2");
		Vertex power = root.addInstance("Power");
		vehicle.addAttribute(power, "Power");
		power.enableUniqueValueConstraint();
		myVehicle.addHolder(power, 125);
		catchAndCheckCause(() -> myVehicle2.addHolder(power, 125), UniqueValueConstraintViolationException.class);
	}

	public void test03_sameValue_differentType() {
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex myVehicle2 = vehicle.addInstance("myVehicle2");
		Vertex power = root.addInstance("Power");
		vehicle.addAttribute(power, "Power");
		power.enableUniqueValueConstraint();
		myVehicle.addHolder(power, 125);
		myVehicle2.addHolder(power, "125");
	}

	public void test04_disableConstraint() {
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex myVehicle2 = vehicle.addInstance("myVehicle2");
		Vertex power = root.addInstance("Power");
		vehicle.addAttribute(power, "Power");
		power.enableUniqueValueConstraint();
		myVehicle.addHolder(power, 125);
		power.disableUniqueValueConstraint();
		myVehicle2.addHolder(power, 125);
	}
}
