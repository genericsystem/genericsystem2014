package org.genericsystem.kernel;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

@Test
public class InstanceValueClassConstraintTest extends AbstractTest {

	public void test01_simpleCase() {
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex myVehicle2 = vehicle.addInstance("myVehicle2");
		Vertex power = root.addInstance("Power", vehicle);

		assert myVehicle.getClassConstraint() == null;
		myVehicle.setClassConstraint(Integer.class);
		assert Integer.class.equals(myVehicle.getClassConstraint());
		myVehicle.setClassConstraint(null);
		assert myVehicle.getClassConstraint() == null;
		//
		//
		// catchAndCheckCause(() -> myVehicle.addHolder(power, "125"), UniqueValueConstraintViolationException.class);

	}

	public void test02_simpleCase() {
		List<Integer> list = new ArrayList<>();
		list.add(null);
		list.stream().findFirst();

	}
}
