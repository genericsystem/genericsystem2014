package org.genericsystem.kernel;

import org.genericsystem.api.exception.ExistsException;
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
		myVehicle.addHolder(power, "123");
		new RollbackCatcher() {

			@Override
			public void intercept() {
				myVehicle.addHolder(power, "126");
			}
		}.assertIsCausedBy(PropertyConstraintViolationException.class);
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
	}

	public void test002_enablePropertyConstraint_addInstance() {
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex power = root.addInstance("Power", vehicle);
		Vertex myVehicle = vehicle.addInstance("myVehicle");

		power.enablePropertyConstraint();
		assert power.isPropertyConstraintEnabled();
		Vertex myVehicle123 = myVehicle.addHolder(power, "123");
		myVehicle.addHolder(power, myVehicle123, "126");
	}

	// public void test001_enablePropertyConstraint_addInstance() {
	// Root Root = new Root();
	// Vertex vehicle = Root.addInstance("Vehicle");
	// Vertex power = Root.addInstance("Power", vehicle);
	// power.enablePropertyConstraint();
	// assert power.isPropertyConstraintEnabled();
	// vehicle.addHolder("123", power);
	// // new RollbackCatcher() {
	// //
	// // @Override
	// // public void intercept() {
	// System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	// System.out.println("aaa " + power.addInstance("126", vehicle));
	// System.out.println("===>" + vehicle.getHolders(power).info());
	// // }
	// // }.assertIsCausedBy(PropertyConstraintViolationException.class);
	// }

	public void test003_enablePropertyConstraint_addInstance() {
		Root Root = new Root();
		Vertex vehicle = Root.addInstance("Vehicle");
		Vertex power = Root.addInstance("Power", vehicle);
		Vertex subPower = Root.addInstance(power, "SubPower", vehicle);
		assert subPower.inheritsFrom(power);
		power.enablePropertyConstraint();
		assert subPower.isPropertyConstraintEnabled();
		subPower.addInstance("123", vehicle);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				subPower.addInstance("126", vehicle);
			}
		}.assertIsCausedBy(ExistsException.class);
	}

	public void test004_enablePropertyConstraint_addInstance() {
		Root Root = new Root();
		Vertex vehicle = Root.addInstance("Vehicle");
		Vertex car = Root.addInstance(vehicle, "Car");
		Vertex power = Root.addInstance("Power", vehicle);
		Vertex subPower = Root.addInstance(power, "Power", car);
		assert subPower.inheritsFrom(power);
		power.enablePropertyConstraint();
		assert subPower.isPropertyConstraintEnabled();
		subPower.addInstance("123", car);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				subPower.addInstance("126", car);
			}
		}.assertIsCausedBy(ExistsException.class);
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
