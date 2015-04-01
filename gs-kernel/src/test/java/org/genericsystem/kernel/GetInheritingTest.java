package org.genericsystem.kernel;

import java.util.Arrays;

import org.testng.annotations.Test;

@Test
public class GetInheritingTest extends AbstractTest {

	public void test001() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic car = root.addInstance(vehicle, "Car");
		root.addInstance(car, "Vehicle");

		assert root.getInheriting("Vehicle") == null;
		assert vehicle.getInheriting("Car") == car;
	}

	public void test002() {
		Root root = new Root();
		Generic tree = root.addInstance("Tree");
		Generic father = tree.addInstance("father");
		Generic mother = tree.addInstance("mother");
		Generic children1 = tree.addInstance(Arrays.asList(father, mother), "children1");
		tree.addInstance(Arrays.asList(father, mother), "children2");
		tree.addInstance(children1, "children2");

		assert father.getInheriting("children1") == children1;
	}

	public void test003() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic color = root.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("vehicleColor", color);

		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic red = color.addInstance("red");
		Generic blue = color.addInstance("blue");
		Generic vehicleRed = vehicleColor.addInstance("", vehicle, red);
		Generic myVehicleRed = vehicleColor.addInstance("", myVehicle, red);
		vehicleColor.addInstance("", myVehicle, blue);

		assert vehicleRed.getInheriting("", myVehicle, red) == myVehicleRed;
	}

	public void test004() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic color = root.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("vehicleColor", color);

		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic red = color.addInstance("red");
		Generic blue = color.addInstance("blue");
		Generic vehicleRed = vehicleColor.addInstance("", vehicle, red);
		Generic myVehicleRed = vehicleColor.addInstance("", myVehicle, red);
		vehicleColor.addInstance("", myVehicle, blue);

		assert vehicleRed.getInheriting(red) == myVehicleRed;
	}

	public void test005() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic color = root.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("vehicleColor", color);

		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic red = color.addInstance("red");
		Generic blue = color.addInstance("blue");
		Generic vehicleRed = vehicleColor.addInstance("", vehicle, red);
		vehicleColor.addInstance("", myVehicle, red);
		Generic myVehicleRed2 = vehicleColor.addInstance(vehicleRed, "myVehicleRed2", myVehicle, red);
		vehicleColor.addInstance("", myVehicle, blue);

		assert vehicleRed.getInheritings("myVehicleRed2").size() == 1;
		assert vehicleRed.getInheritings("myVehicleRed2").first() == myVehicleRed2;
	}

	public void test006() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic color = root.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("vehicleColor", color);

		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic red = color.addInstance("red");
		Generic blue = color.addInstance("blue");
		Generic vehicleRed = vehicleColor.addInstance("", vehicle, red);
		vehicleColor.addInstance("", myVehicle, red);
		Generic myVehicleBlue = vehicleColor.addInstance(vehicleRed, "", myVehicle, blue);

		assert vehicleRed.getInheritings("").size() == 2;
		assert vehicleRed.getInheritings("", blue).size() == 1;
		assert vehicleRed.getInheritings("", blue).first() == myVehicleBlue;
	}

	public void test007() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic color = root.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("vehicleColor", color);

		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic red = color.addInstance("red");
		Generic blue = color.addInstance("blue");
		Generic vehicleRed = vehicleColor.addInstance("", vehicle, red);
		vehicleColor.addInstance("", myVehicle, red);
		Generic myVehicleBlue = vehicleColor.addInstance(vehicleRed, "", myVehicle, blue);

		assert vehicleRed.getInheritings(blue).size() == 1;
		assert vehicleRed.getInheritings(blue).first() == myVehicleBlue;
	}

	public void test008() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic car = root.addInstance(vehicle, "Car");
		Generic carVehicle = root.addInstance(car, "Vehicle");

		assert vehicle.getAllInheritings("Vehicle").containsAll(Arrays.asList(vehicle, carVehicle)) : root.getMetaAttribute().getAllInheritings("Vehicle").info();
		assert vehicle.getAllInheritings("Car").first() == car;
	}

	public void test009() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic car = root.addInstance(vehicle, "Car");
		Generic sportCar = root.addInstance(car, "SportCar");
		Generic bike = root.addInstance(vehicle, "Bike");
		Generic color = root.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("vehicleColor", color);
		Generic carColor = car.addRelation("vehicleColor", color);
		Generic bikeColor = bike.addRelation("vehicleColor", color);
		Generic sportCarColor = sportCar.addRelation("vehicleColor", color);

		assert vehicleColor.getAllInheritings("vehicleColor", color).size() == 4 : vehicleColor.getAllInheritings("vehicleColor", color).info();
		assert vehicleColor.getAllInheritings("vehicleColor", color).containsAll(Arrays.asList(vehicleColor, carColor, bikeColor, sportCarColor));
	}

	public void test010() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic car = root.addInstance(vehicle, "Car");
		Generic sportCar = root.addInstance(car, "SportCar");
		Generic bike = root.addInstance(vehicle, "Bike");
		Generic color = root.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("vehicleColor", color);
		Generic carColor = car.addRelation("vehicleColor", color);
		bike.addRelation("vehicleColor", color);
		sportCar.addRelation("vehicleColor", color);

		assert vehicleColor.getAllInheritings(color, car).size() == 1 : vehicleColor.getAllInheritings(color, car).info();
		assert vehicleColor.getAllInheritings(color, car).contains(carColor);
	}

}
