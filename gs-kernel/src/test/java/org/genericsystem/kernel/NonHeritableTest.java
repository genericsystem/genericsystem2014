package org.genericsystem.kernel;

import java.util.Objects;

import org.testng.annotations.Test;

@Test
public class NonHeritableTest extends AbstractTest {

	public void test001() {
		Root root = new Root();
		Generic car = root.addInstance("Car");
		car.enableHeritable();
		assert car.isHeritableEnabled();
	}

	public void test002() {
		Root root = new Root();
		Generic car = root.addInstance("Car");
		assert car.isHeritableEnabled();
	}

	public void test003() {
		Root root = new Root();
		Generic car = root.addInstance("Car");
		car.disableHeritable();
		assert !car.isHeritableEnabled();
	}

	public void test004() {
		Root root = new Root();
		Generic car = root.addInstance("Car");
		assert car.isHeritableEnabled();
		car.disableHeritable();
		assert !car.isHeritableEnabled();
		car.enableHeritable();
		assert car.isHeritableEnabled();
	}

	public void test005() {
		Root root = new Root();
		Generic car = root.addInstance("Car");
		car.enableHeritable();
		assert car.isHeritableEnabled();
		car.disableHeritable();
		assert !car.isHeritableEnabled();
	}

	public void test006() {
		Root root = new Root();
		Generic car = root.addInstance("Car");
		Generic power = car.addAttribute("Power");
		Generic defaultPower = car.addHolder(power, 233);
		Generic myCar = car.addInstance("myCar");
		power.disableHeritable();
		assert myCar.getHolder(power) == null;
		power.enableHeritable();
		assert myCar.getHolder(power).equals(defaultPower);
	}

	public void test007() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic power = vehicle.addAttribute("Power");
		Generic car = root.addInstance(vehicle, "Car");
		Generic defaultPower = vehicle.addHolder(power, 233);
		assert defaultPower.equals(car.getHolder(power));
		assert car.getHolder(power) != null;
		power.disableHeritable();
		assert car.getHolder(power) == null;
		Generic defaultCarPower = car.addHolder(power, defaultPower, 256);
		Generic myCar = car.addInstance("myBmw");
		assert myCar.getHolder(power) == null;
		power.enableHeritable();
		assert myCar.getHolder(power) != null;
		assert defaultCarPower.equals(myCar.getHolder(power)) : myCar.getHolder(power);
	}

	public void test008() {
		Root root = new Root();

		Generic car = root.addInstance("Car");
		Generic color = root.addInstance("Color");
		Generic carColor = car.addAttribute("CarColor", color);

		carColor.disableHeritable();

		Generic blue = color.addInstance("blue");
		Generic red = color.addInstance("red");
		Generic green = color.addInstance("green");

		car.setHolder(carColor, "CarBlueByDefault", blue);

		Generic myCar = car.addInstance("myCar");
		Generic myAudi = car.addInstance(myCar, "myAudi");
		Generic myMercedes = car.addInstance(myCar, "myMercedes");
		Generic myPorsche = car.addInstance(myMercedes, "myPorsche");

		myCar.setHolder(carColor, "myCarRed", red);
		myPorsche.setHolder(carColor, "myPorscheGreen", green);

		assert Objects.equals(car.getHolders(carColor).first().getTargetComponent(), blue);
		assert Objects.equals(myAudi.getHolders(carColor).first(), null);
		assert Objects.equals(myMercedes.getHolders(carColor).first(), null);
		assert Objects.equals(myPorsche.getHolders(carColor).first().getTargetComponent(), green);
	}

	public void test009() {
		Root root = new Root();
		Generic car = root.addInstance("Car");
		Generic color = root.addInstance("Color");
		Generic carColor = car.addAttribute("CarColor", color);
		Generic blue = color.addInstance("blue");
		Generic red = color.addInstance("red");
		Generic green = color.addInstance("green");
		car.setHolder(carColor, "CarBlueByDefault", blue);
		Generic myCar = car.addInstance("myCar");
		Generic myAudi = car.addInstance(myCar, "myAudi");
		Generic myMercedes = car.addInstance(myCar, "myMercedes");
		Generic myPorsche = car.addInstance(myMercedes, "myPorsche");

		myCar.setHolder(carColor, "myCarRed", red);
		myPorsche.setHolder(carColor, "myPorscheGreen", green);

		carColor.disableHeritable();
		assert Objects.equals(car.getHolders(carColor).first().getTargetComponent(), blue);
		assert Objects.equals(myAudi.getHolders(carColor).first(), null);
		assert Objects.equals(myMercedes.getHolders(carColor).first(), null);
		assert Objects.equals(myPorsche.getHolders(carColor).first().getTargetComponent(), green);

		carColor.enableHeritable();
		assert Objects.equals(car.getHolders(carColor).first().getTargetComponent(), blue);
		assert Objects.equals(myAudi.getHolders(carColor).first().getTargetComponent(), red);
		assert Objects.equals(myMercedes.getHolders(carColor).first().getTargetComponent(), red);
		assert Objects.equals(myPorsche.getHolders(carColor).first().getTargetComponent(), green);
	}

}
