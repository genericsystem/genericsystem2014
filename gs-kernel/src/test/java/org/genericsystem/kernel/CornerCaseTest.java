package org.genericsystem.kernel;

import org.genericsystem.api.core.exceptions.AmbiguousSelectionException;
import org.testng.annotations.Test;

@Test
public class CornerCaseTest extends AbstractTest {

	public void test000() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic power = vehicle.addAttribute("Power");
		Generic carPower = car.addAttribute(power, "CarPower");
		Generic carPower2 = car.addAttribute(power, "CarPower2");
		Generic myCar = car.addInstance("myCar");
		catchAndCheckCause(() -> myCar.addHolder(power, 233), AmbiguousSelectionException.class);
	}

	public void test001() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic power = vehicle.addAttribute("Power");
		Generic carPower = car.addAttribute(power, "CarPower");
		Generic myCar = car.addInstance("myCar");
		Generic v233 = myCar.addHolder(power, 233);
		assert v233.isInstanceOf(carPower) : v233.info();
		Generic v233_2 = myCar.setHolder(power, 233);
		assert v233 == v233_2;
	}

	public void test002() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic power = vehicle.addAttribute("Power");
		assert engine.getInstance("Power", vehicle).equals(power);
		// catchAndCheckCause(() -> vehicle.setAttribute(power, "Power"), CollisionException.class);
	}

}
