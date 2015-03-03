package org.genericsystem.kernel;

import org.testng.annotations.Test;

@Test
public class ComponentsOrderTest extends AbstractTest {

	public void test001() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		final Generic carColor = engine.addInstance("CarColor", car, color);
		final Generic myCar = car.addInstance("myCar");
		final Generic green = color.addInstance("green");
		myCar.addLink(carColor, "myCarColor", green);
	}

	public void test002() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		final Generic carColor = engine.addInstance("CarColor", car, color);
		final Generic myCar = car.addInstance("myCar");
		final Generic green = color.addInstance("green");
		green.addLink(carColor, "myCarColor", myCar);
	}

	public void test003() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		final Generic carColor = engine.addInstance("CarColor", car, color);
		final Generic myCar = car.addInstance("myCar");
		final Generic green = color.addInstance("green");
		carColor.addInstance("myCarColor", myCar, green);
	}

	public void test004() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		final Generic carColor = engine.addInstance("CarColor", car, color);
		final Generic myCar = car.addInstance("myCar");
		final Generic green = color.addInstance("green");
		carColor.addInstance("myCarColor", green, myCar);
	}

}
