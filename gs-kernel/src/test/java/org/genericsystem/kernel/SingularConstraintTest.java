package org.genericsystem.kernel;

import org.genericsystem.api.exception.SingularConstraintViolationException;
import org.testng.annotations.Test;

@Test
public class SingularConstraintTest extends AbstractTest {

	public void test001_enableSingularConstraint_addInstance() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("red");
		Vertex yellow = color.addInstance("yellow");
		Vertex vehicleColor = vehicle.addAttribute("vehicleColor", color);
		vehicleColor.enableSingularConstraint(Statics.BASE_POSITION);
		assert vehicleColor.isSingularConstraintEnabled(Statics.BASE_POSITION);
		assert !vehicleColor.isReferentialIntegrityEnabled(Statics.BASE_POSITION);
		myVehicle.addHolder(vehicleColor, "vehicleRed", red);
		catchAndCheckCause(() -> myVehicle.addHolder(vehicleColor, "vehicleYellow", yellow), SingularConstraintViolationException.class);
	}

	public void test002_enableSingularConstraint_addInstance() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex yourVehicle = vehicle.addInstance("yourVehicle");
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("red");
		Vertex vehicleColor = vehicle.addAttribute("vehicleColor", color);
		vehicleColor.enableSingularConstraint(Statics.BASE_POSITION);
		assert vehicleColor.isSingularConstraintEnabled(Statics.BASE_POSITION);
		myVehicle.addHolder(vehicleColor, "vehicleRed", red);
		yourVehicle.addHolder(vehicleColor, "vehicleRed", red);
	}

	public void test003_enableSingularConstraint_addDefaultInstance() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("red");
		Vertex yellow = color.addInstance("yellow");
		Vertex vehicleColor = vehicle.addAttribute("vehicleColor", color);
		vehicleColor.enableSingularConstraint(Statics.BASE_POSITION);
		assert vehicleColor.isSingularConstraintEnabled(Statics.BASE_POSITION);
		vehicle.addHolder(vehicleColor, "vehicleRed", red);
		catchAndCheckCause(() -> vehicle.addHolder(vehicleColor, "vehicleYellow", yellow), SingularConstraintViolationException.class);
	}

	public void test001_enableSingularConstraint_ternaryRelation() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex color = engine.addInstance("Color");
		Vertex time = engine.addInstance("Time");
		Vertex red = color.addInstance("red");
		Vertex today = time.addInstance("today");
		Vertex yesterday = time.addInstance("yesterday");
		Vertex vehicleColor = vehicle.addAttribute("vehicleColor", color);
		vehicleColor.enableSingularConstraint(Statics.BASE_POSITION);
		assert vehicleColor.isSingularConstraintEnabled(Statics.BASE_POSITION);
		myVehicle.addHolder(vehicleColor, "vehicleRedToday", red, today);
		catchAndCheckCause(() -> myVehicle.addHolder(vehicleColor, "vehicleRedYesterday", red, yesterday), SingularConstraintViolationException.class);
	}

	public void test005_enableSingularConstraint_targetPosition() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex myVehicle2 = vehicle.addInstance("myVehicle2");
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("red");
		Vertex vehicleColor = vehicle.addAttribute("vehicleColor", color);
		vehicleColor.enableSingularConstraint(Statics.TARGET_POSITION);
		assert vehicleColor.isSingularConstraintEnabled(Statics.TARGET_POSITION);
		myVehicle.addHolder(vehicleColor, "myVehicleRed", red);
		myVehicle2.addHolder(vehicleColor, "myVehicleRed2", red);

		catchAndCheckCause(() -> myVehicle2.addHolder(vehicleColor, "myVehicleRed2", red), SingularConstraintViolationException.class);
	}

	public void test006_enableSingularConstraint_targetPosition() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("red");
		Vertex yellow = color.addInstance("yellow");
		Vertex vehicleColor = vehicle.addAttribute("vehicleColor", color);
		vehicleColor.enableSingularConstraint(Statics.TARGET_POSITION);

		Vertex myVehicleRed = myVehicle.addHolder(vehicleColor, "myVehicleRed", red);
		Vertex myVehicleYellow = myVehicle.addHolder(vehicleColor, "myVehicleYellow", yellow);

		assert myVehicle.getHolders(vehicleColor).contains(myVehicleRed);
		assert myVehicle.getHolders(vehicleColor).contains(myVehicleYellow);
		assert myVehicle.getHolders(vehicleColor).size() == 2;
	}

	public void test007_enableSingularConstraint_targetPosition() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex myVehicle2 = vehicle.addInstance("myVehicle2");
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("red");
		Vertex yellow = color.addInstance("yellow");
		Vertex vehicleColor = vehicle.addAttribute("vehicleColor", color);
		vehicleColor.enableSingularConstraint(Statics.TARGET_POSITION);

		Vertex myVehicleRed = myVehicle.addHolder(vehicleColor, "myVehicleRed", red);
		Vertex myVehicle2Yellow = myVehicle2.addHolder(vehicleColor, "myVehicle2Yellow", yellow);

		assert myVehicle.getHolders(vehicleColor).contains(myVehicleRed);
		assert myVehicle.getHolders(vehicleColor).size() == 1;
		assert myVehicle2.getHolders(vehicleColor).contains(myVehicle2Yellow);
		assert myVehicle2.getHolders(vehicleColor).size() == 1;
	}

	public void test008_enableSingularConstraint_targetPosition() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex myVehicle2 = vehicle.addInstance("myVehicle2");
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("red");
		Vertex yellow = color.addInstance("yellow");
		Vertex vehicleColor = vehicle.addAttribute("vehicleColor", color);
		vehicleColor.enableSingularConstraint(Statics.TARGET_POSITION);

		Vertex myVehicleRed = myVehicle.addHolder(vehicleColor, "myVehicleRed", red);
		Vertex myVehicle2Yellow = myVehicle2.addHolder(vehicleColor, "myVehicle2Yellow", yellow);

		catchAndCheckCause(() -> myVehicle.addHolder(vehicleColor, "myVehicleYellow", yellow), SingularConstraintViolationException.class);
	}

	public void test009_enableSingularConstraint_ternaryPosition() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex myVehicle2 = vehicle.addInstance("myVehicle2");

		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("red");

		Vertex location = engine.addInstance("Location");
		Vertex outside = location.addInstance("outside");

		Vertex vehicleColorLocation = vehicle.addAttribute("vehicleColor", color, location);
		vehicleColorLocation.enableSingularConstraint(Statics.TERNARY_POSITION);

		Vertex myVehicleRedOutside = myVehicle.addHolder(vehicleColorLocation, "myVehicleRedOutside", red, outside);
		catchAndCheckCause(() -> myVehicle2.addHolder(vehicleColorLocation, "myVehicle2RedOutside", red, outside), SingularConstraintViolationException.class);
	}

	// public void test002_enablePropertyConstraint_addInstance() {
	// Root engine = new Root();
	// Vertex vehicle = engine.addInstance("Vehicle");
	// Vertex power = engine.addInstance("Power", vehicle);
	// Vertex subPower = engine.addInstance(power, "SubPower", vehicle);
	// assert subPower.inheritsFrom(power);
	// power.enablePropertyConstraint();
	// assert subPower.isPropertyConstraintEnabled();
	// subPower.addInstance("123", vehicle);
	// new RollbackCatcher() {
	//
	// @Override
	// public void intercept() {
	// subPower.addInstance("126", vehicle);
	// }
	// }.assertIsCausedBy(ExistsException.class);
	// }
	//
	// public void test003_enablePropertyConstraint_addInstance() {
	// Root engine = new Root();
	// Vertex vehicle = engine.addInstance("Vehicle");
	// Vertex car = engine.addInstance(vehicle, "Car");
	// Vertex power = engine.addInstance("Power", vehicle);
	// Vertex subPower = engine.addInstance(power, "Power", car);
	// assert subPower.inheritsFrom(power);
	// power.enablePropertyConstraint();
	// assert subPower.isPropertyConstraintEnabled();
	// subPower.addInstance("123", car);
	// new RollbackCatcher() {
	//
	// @Override
	// public void intercept() {
	// subPower.addInstance("126", car);
	// }
	// }.assertIsCausedBy(ExistsException.class);
	// }
	//
	// public void test001_enablePropertyConstraint_setInstance() {
	// Root engine = new Root();
	// Vertex vehicle = engine.addInstance("Vehicle");
	// Vertex power = engine.addInstance("Power", vehicle);
	// power.enablePropertyConstraint();
	// assert power.isPropertyConstraintEnabled();
	// power.setInstance("123", vehicle);
	// power.setInstance("126", vehicle);
	// assert power.getInstances().size() == 1;
	// power.getInstances().forEach(x -> x.getValue().equals("126"));
	// }
	//
	// public void test001_disablePropertyConstraint_setInstance() {
	// Root engine = new Root();
	// Vertex vehicle = engine.addInstance("Vehicle");
	// Vertex power = engine.addInstance("Power", vehicle);
	// power.enablePropertyConstraint();
	// assert power.isPropertyConstraintEnabled();
	// power.setInstance("123", vehicle);
	// power.setInstance("126", vehicle);
	// assert power.getInstances().size() == 1;
	// power.getInstances().forEach(x -> x.getValue().equals("126"));
	// power.disablePropertyConstraint();
	// assert !power.isPropertyConstraintEnabled();
	// power.setInstance("123", vehicle);
	// assert power.getInstances().size() == 2;
	// }

}
