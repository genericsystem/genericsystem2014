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
		Generic vehicle = engine.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic color = engine.addInstance("Color");
		Generic red = color.addInstance("red");
		Generic yellow = color.addInstance("yellow");
		Generic vehicleColor = vehicle.addAttribute("vehicleColor", color);

		myVehicle.addHolder(vehicleColor, "vehicleRed", red);
		myVehicle.addHolder(vehicleColor, "vehicleYellow", yellow);
		catchAndCheckCause(() -> vehicleColor.enableSingularConstraint(ApiStatics.BASE_POSITION), SingularConstraintViolationException.class);
	}

	public void test002_enableUniqueValueConstraint() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic myVehicle2 = vehicle.addInstance("myVehicle2");
		Generic power = root.addInstance("Power");
		vehicle.addAttribute(power, "Power");
		myVehicle.addHolder(power, 125);
		myVehicle2.addHolder(power, 125);
		catchAndCheckCause(() -> power.enableUniqueValueConstraint(), UniqueValueConstraintViolationException.class);
	}

	public void test003_enablePropertyConstraint() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic power = root.addInstance("Power", vehicle);
		Generic myVehicle = vehicle.addInstance("myVehicle");
		myVehicle.addHolder(power, "126");
		myVehicle.addHolder(power, "123");
		catchAndCheckCause(() -> power.enablePropertyConstraint(), PropertyConstraintViolationException.class);
	}

	public void test03_InstanceValueClassConstraint() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic power = root.addInstance("Power");
		vehicle.addAttribute(power, "Power");
		myVehicle.addHolder(power, "125");

		catchAndCheckCause(() -> power.setClassConstraint(Integer.class), InstanceValueClassConstraintViolationException.class);

	}
}
