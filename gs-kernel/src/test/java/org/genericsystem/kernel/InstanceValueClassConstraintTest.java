package org.genericsystem.kernel;

import java.util.stream.Stream;

import org.genericsystem.api.defaults.exceptions.InstanceValueClassConstraintViolationException;
import org.testng.annotations.Test;

@Test
public class InstanceValueClassConstraintTest extends AbstractTest {

	public void test01_setAndGet() {
		Stream.empty().count();
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");

		assert myVehicle.getClassConstraint() == null;
		vehicle.setClassConstraint(String.class);
		assert String.class.equals(vehicle.getClassConstraint());
		vehicle.disableClassConstraint();
		myVehicle.updateValue(null);

		assert vehicle.getClassConstraint() == null;

	}

	public void test02_noException() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic power = root.addInstance("Power");
		vehicle.addAttribute(power, "Power");
		power.setClassConstraint(Integer.class);

		myVehicle.addHolder(power, 125);
	}

	public void test03_exception() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic power = root.addInstance("Power");
		vehicle.addAttribute(power, "Power");
		power.setClassConstraint(Integer.class);

		catchAndCheckCause(() -> myVehicle.addHolder(power, "125"), InstanceValueClassConstraintViolationException.class);

	}

	public void test04_DisableConstraint() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic power = root.addInstance("Power");
		vehicle.addAttribute(power, "Power");
		power.setClassConstraint(Integer.class);

		myVehicle.addHolder(power, 125);
		power.setClassConstraint(null);
		myVehicle.addHolder(power, "230");
	}

	public void test05_DisableConstraint() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic power = root.addInstance("Power");
		vehicle.addAttribute(power, "Power");
		power.enableClassConstraint(Integer.class);

		myVehicle.addHolder(power, 125);
		power.disableClassConstraint();
		myVehicle.addHolder(power, "230");
	}
}
