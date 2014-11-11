package org.genericsystem.kernel;

import java.util.stream.Stream;
import org.genericsystem.api.exception.InstanceValueClassViolationConstraint;
import org.testng.annotations.Test;

@Test
public class InstanceValueClassConstraintTest extends AbstractTest {

	public void test01_setAndGet() {
		Stream.empty().count();
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVehicle");

		assert myVehicle.getClassConstraint() == null;
		vehicle.setClassConstraint(Integer.class);
		assert Integer.class.equals(vehicle.getClassConstraint());
		vehicle.setClassConstraint(null);
		assert vehicle.getClassConstraint() == null;

	}

	public void test02_noException() {
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex power = root.addInstance("Power");
		vehicle.addAttribute(power, "Power");
		power.setClassConstraint(Integer.class);

		myVehicle.addHolder(power, 125);
	}

	public void test03_exception() {
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex power = root.addInstance("Power");
		vehicle.addAttribute(power, "Power");
		power.setClassConstraint(Integer.class);

		catchAndCheckCause(() -> myVehicle.addHolder(power, "125"), InstanceValueClassViolationConstraint.class);

	}

	public void test04_DisableConstraint() {
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex power = root.addInstance("Power");
		vehicle.addAttribute(power, "Power");
		power.setClassConstraint(Integer.class);

		myVehicle.addHolder(power, 125);
		power.setClassConstraint(null);
		myVehicle.addHolder(power, "230");
	}
}
