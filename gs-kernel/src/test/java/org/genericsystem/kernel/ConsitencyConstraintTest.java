package org.genericsystem.kernel;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.api.exception.InstanceValueClassConstraintViolationException;
import org.genericsystem.api.exception.PropertyConstraintViolationException;
import org.genericsystem.api.exception.SingularConstraintViolationException;
import org.genericsystem.api.exception.UniqueValueConstraintViolationException;
import org.testng.annotations.Test;

@Test
public class ConsitencyConstraintTest extends AbstractTest {

	public void test001_enableSingularConstraint() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("red");
		Vertex yellow = color.addInstance("yellow");
		Vertex vehicleColor = vehicle.addAttribute("vehicleColor", color);

		myVehicle.addHolder(vehicleColor, "vehicleRed", red);
		myVehicle.addHolder(vehicleColor, "vehicleYellow", yellow);
		catchAndCheckCause(() -> vehicleColor.enableSingularConstraint(ApiStatics.BASE_POSITION), SingularConstraintViolationException.class);
	}

	public void test002_enableUniqueValueConstraint() {
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex myVehicle2 = vehicle.addInstance("myVehicle2");
		Vertex power = root.addInstance("Power");
		vehicle.addAttribute(power, "Power");
		myVehicle.addHolder(power, 125);
		myVehicle2.addHolder(power, 125);
		catchAndCheckCause(() -> power.enableUniqueValueConstraint(), UniqueValueConstraintViolationException.class);
	}

	public void test003_enablePropertyConstraint() {
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex power = root.addInstance("Power", vehicle);
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		myVehicle.addHolder(power, "126");
		myVehicle.addHolder(power, "123");
		catchAndCheckCause(() -> power.enablePropertyConstraint(), PropertyConstraintViolationException.class);
	}

	public void test03_InstanceValueClassConstraint() {
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex power = root.addInstance("Power");
		vehicle.addAttribute(power, "Power");
		myVehicle.addHolder(power, "125");

		catchAndCheckCause(() -> power.setClassConstraint(Integer.class), InstanceValueClassConstraintViolationException.class);

	}
}
