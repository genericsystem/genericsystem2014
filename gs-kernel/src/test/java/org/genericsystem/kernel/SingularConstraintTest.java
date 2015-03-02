package org.genericsystem.kernel;

import java.util.Arrays;
import java.util.Collections;
import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.api.exception.SingularConstraintViolationException;
import org.testng.annotations.Test;

@Test
public class SingularConstraintTest extends AbstractTest {

	public void test000_enableSingularConstraint() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = vehicle.addAttribute("vehicleColor", color);
		vehicleColor.enableSingularConstraint(ApiStatics.BASE_POSITION);
		Generic color2 = engine.addInstance("Color2");
		Generic vehicleColor2 = vehicle.addAttribute("vehicleColor2", color2);
		vehicleColor2.enableSingularConstraint(ApiStatics.BASE_POSITION);
		assert vehicleColor.isSingularConstraintEnabled(0);
		assert vehicleColor2.isSingularConstraintEnabled(0);
	}

	public void test001_enableSingularConstraint_addInstance() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic color = engine.addInstance("Color");
		Generic red = color.addInstance("red");
		Generic yellow = color.addInstance("yellow");
		Generic vehicleColor = vehicle.addAttribute("vehicleColor", color);
		vehicleColor.enableSingularConstraint(ApiStatics.BASE_POSITION);
		assert vehicleColor.isSingularConstraintEnabled(ApiStatics.BASE_POSITION);
		assert !vehicleColor.isReferentialIntegrityEnabled(ApiStatics.BASE_POSITION);
		myVehicle.addHolder(vehicleColor, "vehicleRed", red);
		catchAndCheckCause(() -> myVehicle.addHolder(vehicleColor, "vehicleYellow", yellow), SingularConstraintViolationException.class);
	}

	public void test002_enableSingularConstraint_addInstance() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic yourVehicle = vehicle.addInstance("yourVehicle");
		Generic color = engine.addInstance("Color");
		Generic red = color.addInstance("red");
		Generic vehicleColor = vehicle.addAttribute("vehicleColor", color);
		vehicleColor.enableSingularConstraint(ApiStatics.BASE_POSITION);
		assert vehicleColor.isSingularConstraintEnabled(ApiStatics.BASE_POSITION);
		myVehicle.addHolder(vehicleColor, "vehicleRed", red);
		yourVehicle.addHolder(vehicleColor, "vehicleRed", red);
	}

	public void test003_enableSingularConstraint_addDefaultInstance() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic red = color.addInstance("red");
		Generic yellow = color.addInstance("yellow");
		Generic vehicleColor = vehicle.addAttribute("vehicleColor", color);
		vehicleColor.enableSingularConstraint(ApiStatics.BASE_POSITION);
		assert vehicleColor.isSingularConstraintEnabled(ApiStatics.BASE_POSITION);
		vehicle.addHolder(vehicleColor, "vehicleRed", red);
		catchAndCheckCause(() -> vehicle.addHolder(vehicleColor, "vehicleYellow", yellow), SingularConstraintViolationException.class);
	}

	public void test001_enableSingularConstraint_ternaryRelation() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic color = engine.addInstance("Color");
		Generic time = engine.addInstance("Time");
		Generic red = color.addInstance("red");
		Generic today = time.addInstance("today");
		Generic yesterday = time.addInstance("yesterday");

		Generic vehicleColor = vehicle.addAttribute("vehicleColor", color, time);
		vehicleColor.enableSingularConstraint(ApiStatics.BASE_POSITION);
		assert vehicleColor.isSingularConstraintEnabled(ApiStatics.BASE_POSITION);
		myVehicle.addHolder(vehicleColor, "vehicleRedToday", red, today);
		catchAndCheckCause(() -> myVehicle.addHolder(vehicleColor, "vehicleRedYesterday", red, yesterday), SingularConstraintViolationException.class);

	}

	public void test005_enableSingularConstraint_targetPosition() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic myVehicle2 = vehicle.addInstance("myVehicle2");
		Generic color = engine.addInstance("Color");
		Generic red = color.addInstance("red");
		Generic vehicleColor = vehicle.addAttribute("vehicleColor", color);
		vehicleColor.enableSingularConstraint(ApiStatics.TARGET_POSITION);
		assert vehicleColor.isSingularConstraintEnabled(ApiStatics.TARGET_POSITION);
		myVehicle.addHolder(vehicleColor, "myVehicleRed", red);

		catchAndCheckCause(() -> myVehicle2.addHolder(vehicleColor, "myVehicleRed2", red), SingularConstraintViolationException.class);
	}

	public void test006_enableSingularConstraint_targetPosition() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic color = engine.addInstance("Color");
		Generic red = color.addInstance("red");
		Generic yellow = color.addInstance("yellow");
		Generic vehicleColor = vehicle.addAttribute("vehicleColor", color);
		vehicleColor.enableSingularConstraint(ApiStatics.TARGET_POSITION);

		Generic myVehicleRed = myVehicle.addHolder(vehicleColor, "myVehicleRed", red);
		Generic myVehicleYellow = myVehicle.addHolder(vehicleColor, "myVehicleYellow", yellow);

		assert myVehicle.getHolders(vehicleColor).contains(myVehicleRed);
		assert myVehicle.getHolders(vehicleColor).contains(myVehicleYellow);
		assert myVehicle.getHolders(vehicleColor).size() == 2;
	}

	public void test007_enableSingularConstraint_targetPosition() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic myVehicle2 = vehicle.addInstance("myVehicle2");
		Generic color = engine.addInstance("Color");
		Generic red = color.addInstance("red");
		Generic yellow = color.addInstance("yellow");
		Generic vehicleColor = vehicle.addAttribute("vehicleColor", color);
		vehicleColor.enableSingularConstraint(ApiStatics.TARGET_POSITION);

		Generic myVehicleRed = myVehicle.addHolder(vehicleColor, "myVehicleRed", red);
		Generic myVehicle2Yellow = myVehicle2.addHolder(vehicleColor, "myVehicle2Yellow", yellow);

		assert myVehicle.getHolders(vehicleColor).contains(myVehicleRed);
		assert myVehicle.getHolders(vehicleColor).size() == 1;
		assert myVehicle2.getHolders(vehicleColor).contains(myVehicle2Yellow);
		assert myVehicle2.getHolders(vehicleColor).size() == 1;
	}

	public void test008_enableSingularConstraint_targetPosition() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic myVehicle2 = vehicle.addInstance("myVehicle2");
		Generic color = engine.addInstance("Color");
		Generic red = color.addInstance("red");
		Generic yellow = color.addInstance("yellow");
		Generic vehicleColor = vehicle.addAttribute("vehicleColor", color);
		vehicleColor.enableSingularConstraint(ApiStatics.TARGET_POSITION);

		Generic myVehicleRed = myVehicle.addHolder(vehicleColor, "myVehicleRed", red);
		Generic myVehicle2Yellow = myVehicle2.addHolder(vehicleColor, "myVehicle2Yellow", yellow);

		catchAndCheckCause(() -> myVehicle.addHolder(vehicleColor, "myVehicleYellow", yellow), SingularConstraintViolationException.class);
	}

	public void test009_enableSingularConstraint_ternaryPosition() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic myVehicle2 = vehicle.addInstance("myVehicle2");

		Generic color = engine.addInstance("Color");
		Generic red = color.addInstance("red");

		Generic location = engine.addInstance("Location");
		Generic outside = location.addInstance("outside");

		Generic vehicleColorLocation = vehicle.addAttribute("vehicleColor", color, location);
		vehicleColorLocation.enableSingularConstraint(ApiStatics.TERNARY_POSITION);

		Generic myVehicleRedOutside = myVehicle.addHolder(vehicleColorLocation, "myVehicleRedOutside", red, outside);
		catchAndCheckCause(() -> myVehicle2.addHolder(vehicleColorLocation, "myVehicle2RedOutside", red, outside), SingularConstraintViolationException.class);
	}

	public void test010_enableSingularConstraint_inherintings() {

		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic myCar = car.addInstance("myCar");
		Generic color = engine.addInstance("Color");
		Generic red = color.addInstance("red");
		Generic yellow = color.addInstance("yellow");
		Generic vehicleColor = vehicle.addAttribute("vehicleColor", color);
		vehicleColor.enableSingularConstraint(ApiStatics.BASE_POSITION);
		myCar.addHolder(vehicleColor, "myCarRed", red);
		catchAndCheckCause(() -> myCar.addHolder(vehicleColor, "myCarYellow", yellow), SingularConstraintViolationException.class);

	}

	public void test011_enablePropertyConstraint_inherintings() {
		Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic myCar = car.addInstance("myCar");
		Generic color = engine.addInstance("Color");
		Generic red = color.addInstance("red");
		Generic yellow = color.addInstance("yellow");
		Generic carColor = car.addAttribute("vehicleColor", color);
		carColor.enablePropertyConstraint();
		Generic carRed = car.addHolder(carColor, "CarRed", red);
		assert carRed.isSuperOf(carColor, Collections.emptyList(), "myCarRed", Arrays.asList(myCar, red));
		Generic myCarRed = myCar.addHolder(carColor, "myCarRed", red);
		assert myCar.getHolders(carColor).contains(myCarRed);
		assert myCar.getHolders(carColor).size() == 1;
		assert red.getHolders(carColor).contains(myCarRed);
		assert red.getHolders(carColor).size() == 1;
	}

	public void test0112_enableSingularConstraint_inherintings() {
		Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic myCar = car.addInstance("myCar");
		Generic color = engine.addInstance("Color");
		Generic red = color.addInstance("red");
		Generic yellow = color.addInstance("yellow");
		Generic carColor = car.addAttribute("vehicleColor", color);
		carColor.enableSingularConstraint(ApiStatics.BASE_POSITION);
		Generic carRed = car.addHolder(carColor, "CarRed", red);
		assert carRed.isSuperOf(carColor, Collections.emptyList(), "myCarYellow", Arrays.asList(myCar, yellow));
		Generic myCarYellow = myCar.addHolder(carColor, "myCarYellow", yellow);
		assert myCar.getHolders(carColor).contains(myCarYellow);
		assert myCar.getHolders(carColor).size() == 1;
		assert yellow.getHolders(carColor).contains(myCarYellow);
		assert yellow.getHolders(carColor).size() == 1;
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
