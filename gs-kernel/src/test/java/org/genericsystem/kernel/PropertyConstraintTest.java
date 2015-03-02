package org.genericsystem.kernel;

import java.util.Arrays;

import org.genericsystem.api.exception.PropertyConstraintViolationException;
import org.testng.annotations.Test;

@Test
public class PropertyConstraintTest extends AbstractTest {

	public void test001_enablePropertyConstraint_addInstance() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic power = root.addInstance("Power", vehicle);
		Generic myVehicle = vehicle.addInstance("myVehicle");

		power.enablePropertyConstraint();
		assert power.isPropertyConstraintEnabled();

		Generic v123 = myVehicle.addHolder(power, "123");
		assert !v123.inheritsFrom(power, "126", Arrays.asList(myVehicle));

		catchAndCheckCause(() -> myVehicle.addHolder(power, "126"), PropertyConstraintViolationException.class);
	}

	public void test001_enablePropertyConstraint_addInstance_link() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic color = root.addInstance("Color");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic red = color.addInstance("red");
		Generic blue = color.addInstance("blue");
		Generic vehicleColorOutside = vehicle.addAttribute("outside", color);

		vehicleColorOutside.enablePropertyConstraint();
		assert vehicleColorOutside.isPropertyConstraintEnabled();
		myVehicle.addHolder(vehicleColorOutside, "outside", red);
		myVehicle.addHolder(vehicleColorOutside, "outside", blue);
		assert myVehicle.getHolders(vehicleColorOutside).size() == 2;
	}

	public void test002_enablePropertyConstraint_addInstance() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic power = root.addInstance("Power", vehicle);
		Generic myVehicle = vehicle.addInstance("myVehicle");

		power.enablePropertyConstraint();
		assert power.isPropertyConstraintEnabled();
		myVehicle.addHolder(power, "123");
		catchAndCheckCause(() -> myVehicle.addHolder(power, "126"), PropertyConstraintViolationException.class);
	}

	public void test001_enablePropertyConstraint_setInstance() {
		Root Root = new Root();
		Generic vehicle = Root.addInstance("Vehicle");
		Generic power = Root.addInstance("Power", vehicle);
		power.enablePropertyConstraint();
		assert power.isPropertyConstraintEnabled();
		power.setInstance("123", vehicle);
		power.setInstance("126", vehicle);
		assert power.getInstances().size() == 1;
		power.getInstances().forEach(x -> x.getValue().equals("126"));
	}

	public void test001_disablePropertyConstraint_setInstance() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic power = root.addInstance("Power", vehicle);
		power.enablePropertyConstraint();
		assert power.isPropertyConstraintEnabled();
		power.setInstance("123", vehicle);
		power.setInstance("126", vehicle);
		assert power.getInstances().size() == 1;
		power.getInstances().forEach(x -> x.getValue().equals("126"));
		power.disablePropertyConstraint();
		assert !power.isPropertyConstraintEnabled();
		power.setInstance("123", vehicle);
		assert power.getInstances().size() == 2;
	}

}
