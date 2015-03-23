package org.genericsystem.kernel;

import org.genericsystem.api.core.exceptions.AliveConstraintViolationException;
import org.testng.annotations.Test;

@Test
public class AliveConstraintTest extends AbstractTest {

	public void test001_AliveConstraint_addInstance() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic color = engine.addInstance("Color");
		Generic red = color.addInstance("red");
		Generic vehicleColor = vehicle.addAttribute("vehicleColor", color);
		myVehicle.remove();
		assert !myVehicle.isAlive();
		catchAndCheckCause(() -> myVehicle.addHolder(vehicleColor, "vehicleRed", red), AliveConstraintViolationException.class);
	}

}
