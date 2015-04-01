package org.genericsystem.kernel;

import java.util.Collections;

import org.testng.annotations.Test;

@Test
public class GetInstanceTest extends AbstractTest {

	public void test001() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic car = root.addInstance(vehicle, "Car");

		Generic myBmw = car.addInstance("myBmw");

		assert vehicle.getInstance("myBmw") == null;
		assert car.getInstance("myBmw") == myBmw;
	}

	public void test002() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic vehiclePower = vehicle.addAttribute("power");
		Generic car = root.addInstance(vehicle, "Car");

		Generic myBmw = car.addInstance("myBmw");
		Generic myBmw115 = vehiclePower.addInstance(115, myBmw);

		assert vehiclePower.getInstance(115, myBmw) == myBmw115;
	}

	public void test003() {
		Root root = new Root();
		Generic car = root.addInstance("Car");
		Generic vehicle = root.addInstance("Vehicle");
		Generic carVehicle = root.addInstance(vehicle, "Car");

		assert root.getInstance(Collections.emptyList(), "Car") == car;
		assert root.getInstance(vehicle, "Car") == carVehicle;
	}

	public void test004() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic myBmw = vehicle.addInstance("myBmw");
		Generic car = root.addInstance(vehicle, "Car");
		Generic myBmwCar = car.addInstance("myBmw");

		assert vehicle.getInstance("myBmw") == myBmw;
		assert car.getInstance("myBmw") == myBmwCar;
	}
}
