package org.genericsystem.kernel;

import org.genericsystem.defaults.exceptions.UniqueValueConstraintViolationException;
import org.testng.annotations.Test;

@Test
public class UniqueValueConstraintTest extends AbstractTest {

	public void test01_enableConstraint() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic power = root.addInstance("Power", vehicle);
		myVehicle.addHolder(power, "125");

		assert !vehicle.isUniqueValueEnabled();
		vehicle.enableUniqueValueConstraint();
		assert vehicle.isUniqueValueEnabled();
		vehicle.disableUniqueValueConstraint();
		assert !vehicle.isUniqueValueEnabled();

	}

	public void test02_erorrCase() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic myVehicle2 = vehicle.addInstance("myVehicle2");
		Generic power = root.addInstance("Power");
		vehicle.addAttribute(power, "Power");
		power.enableUniqueValueConstraint();
		myVehicle.addHolder(power, 125);
		catchAndCheckCause(() -> myVehicle2.addHolder(power, 125), UniqueValueConstraintViolationException.class);
	}

	public void test03_sameValue_differentType() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic myVehicle2 = vehicle.addInstance("myVehicle2");
		Generic power = root.addInstance("Power");
		vehicle.addAttribute(power, "Power");
		power.enableUniqueValueConstraint();
		myVehicle.addHolder(power, 125);
		myVehicle2.addHolder(power, "125");
	}

	public void test04_disableConstraint() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic myVehicle2 = vehicle.addInstance("myVehicle2");
		Generic power = root.addInstance("Power");
		vehicle.addAttribute(power, "Power");
		power.enableUniqueValueConstraint();
		myVehicle.addHolder(power, 125);
		power.disableUniqueValueConstraint();
		myVehicle2.addHolder(power, 125);
	}
}
