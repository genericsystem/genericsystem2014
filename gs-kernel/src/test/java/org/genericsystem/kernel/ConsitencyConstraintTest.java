package org.genericsystem.kernel;

import org.genericsystem.api.exception.SingularConstraintViolationException;
import org.testng.annotations.Test;

@Test
public class ConsitencyConstraintTest extends AbstractTest {

	public void test001_enableSingualarConstraint() {

		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("red");
		Vertex yellow = color.addInstance("yellow");
		Vertex vehicleColor = vehicle.addAttribute("vehicleColor", color);

		myVehicle.addHolder(vehicleColor, "vehicleRed", red);
		myVehicle.addHolder(vehicleColor, "vehicleYellow", yellow);
		System.out.println("//////////////:::");
		catchAndCheckCause(() -> vehicleColor.enableSingularConstraint(Statics.BASE_POSITION), SingularConstraintViolationException.class);
	}
}
