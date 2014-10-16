package org.genericsystem.kernel;

import org.genericsystem.api.exception.AliveConstraintViolationException;
import org.testng.annotations.Test;

@Test
public class AliveConstraintTest extends AbstractTest {

	public void test001_AliveConstraint_addInstance() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("red");
		Vertex vehicleColor = vehicle.addAttribute("vehicleColor", color);
		myVehicle.remove();
		assert !myVehicle.isAlive();
		catchAndCheckCause(() -> myVehicle.addHolder(vehicleColor, "vehicleRed", red), AliveConstraintViolationException.class);
	}

}
