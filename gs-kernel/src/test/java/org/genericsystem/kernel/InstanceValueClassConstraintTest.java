package org.genericsystem.kernel;

import java.util.stream.Stream;
import org.testng.annotations.Test;

@Test
public class InstanceValueClassConstraintTest extends AbstractTest {

	public void test01_simpleCase() {
		Stream.empty().count();
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVehicle");

		assert myVehicle.getClassConstraint() == null;
		myVehicle.setClassConstraint(Integer.class);
		assert Integer.class.equals(myVehicle.getClassConstraint());
		myVehicle.setClassConstraint(null);
		assert myVehicle.getClassConstraint() == null;

	}

	public void test02_simpleCase() {
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex power = root.addInstance("Power");
		vehicle.addAttribute(power, "Power");
		power.setClassConstraint(Integer.class);
		myVehicle.addHolder(power, 125);

		//
		//
		// catchAndCheckCause(() -> myVehicle.addHolder(power, "125"), UniqueValueConstraintViolationException.class);

	}
}
