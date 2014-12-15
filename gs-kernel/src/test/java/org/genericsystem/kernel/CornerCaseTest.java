package org.genericsystem.kernel;

import org.genericsystem.api.exception.AmbiguousSelectionException;
import org.genericsystem.api.exception.CollisionException;
import org.genericsystem.api.exception.UnreachableOverridesException;
import org.testng.annotations.Test;

@Test
public class CornerCaseTest extends AbstractTest {

	public void test000() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex power=vehicle.addAttribute("Power");
		Vertex carPower = car.addAttribute(power,"CarPower");
		Vertex carPower2 = car.addAttribute(power,"CarPower2");
		Vertex myCar = car.addInstance("myCar");
		catchAndCheckCause(()->myCar.addHolder(power,233),AmbiguousSelectionException.class);
	}
	
	public void test001() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex power=vehicle.addAttribute("Power");
		Vertex carPower = car.addAttribute(power,"CarPower");
		Vertex myCar = car.addInstance("myCar");
		Vertex v233 = myCar.addHolder(power,233);
		assert v233.isInstanceOf(carPower) : v233.info();
		Vertex v233_2 = myCar.setHolder(power,233);
		assert v233 == v233_2;
	}
	
	public void test002() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex power=vehicle.addAttribute("Power");
		catchAndCheckCause(()->vehicle.setAttribute(power,"Power"),CollisionException.class);
	}

}
