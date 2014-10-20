package org.genericsystem.kernel;

import java.util.Arrays;

import org.genericsystem.api.exception.PropertyConstraintViolationException;
import org.testng.annotations.Test;

@Test
public class PropertyConstraintTest extends AbstractTest {

	public void test001_enablePropertyConstraint_addInstance() {
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex power = root.addInstance("Power", vehicle);
		Vertex myVehicle = vehicle.addInstance("myVehicle");

		power.enablePropertyConstraint();
		assert power.isPropertyConstraintEnabled();

		Vertex v123 = myVehicle.addHolder(power, "123");
		assert !v123.inheritsFrom(power, "126", Arrays.asList(myVehicle));

		catchAndCheckCause(() -> myVehicle.addHolder(power, "126"), PropertyConstraintViolationException.class);
	}

	public void test001_enablePropertyConstraint_addInstance_link() {
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex color = root.addInstance("Color");
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex red = color.addInstance("red");
		Vertex blue = color.addInstance("blue");
		Vertex vehicleColorOutside = vehicle.addAttribute("outside", color);

		vehicleColorOutside.enablePropertyConstraint();
		assert vehicleColorOutside.isPropertyConstraintEnabled();
		myVehicle.addHolder(vehicleColorOutside, "outside", red);
		myVehicle.addHolder(vehicleColorOutside, "outside", blue);
		assert myVehicle.getHolders(vehicleColorOutside).size() == 2;
	}

	public void test002_enablePropertyConstraint_addInstance() {
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex power = root.addInstance("Power", vehicle);
		Vertex myVehicle = vehicle.addInstance("myVehicle");

		power.enablePropertyConstraint();
		assert power.isPropertyConstraintEnabled();
		myVehicle.addHolder(power, "123");
		catchAndCheckCause(() -> myVehicle.addHolder(power, "126"), PropertyConstraintViolationException.class);
	}

	public void test001_enablePropertyConstraint_setInstance() {
		Root Root = new Root();
		Vertex vehicle = Root.addInstance("Vehicle");
		Vertex power = Root.addInstance("Power", vehicle);
		power.enablePropertyConstraint();
		assert power.isPropertyConstraintEnabled();
		power.setInstance("123", vehicle);
		power.setInstance("126", vehicle);
		assert power.getInstances().size() == 1;
		power.getInstances().forEach(x -> x.getValue().equals("126"));
	}

	public void test001_disablePropertyConstraint_setInstance() {
		Root Root = new Root();
		Vertex vehicle = Root.addInstance("Vehicle");
		Vertex power = Root.addInstance("Power", vehicle);
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
