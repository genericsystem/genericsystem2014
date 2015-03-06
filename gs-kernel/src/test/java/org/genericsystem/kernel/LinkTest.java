package org.genericsystem.kernel;

import org.genericsystem.api.exception.AmbiguousSelectionException;
import org.testng.annotations.Test;

@Test
public class LinkTest extends AbstractTest {

	public void test_addLink() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic carColor = car.addRelation("CarColor", color);
		Generic myCar = car.addInstance("myCar");
		Generic green = color.addInstance("green");
		Generic myCarGreen = myCar.addLink(carColor, null, green);
		assert myCarGreen.equals(myCar.getLink(carColor));
	}

	public void test_setLink() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic carColor = car.addRelation("CarColor", color);
		Generic myCar = car.addInstance("myCar");
		Generic green = color.addInstance("green");
		Generic myCarGreen = myCar.setLink(carColor, null, green);
		assert myCarGreen.equals(myCar.getLink(carColor));
	}

	public void test_getLinkAmbigious() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic carColor = car.addRelation("CarColor", color);
		Generic myCar = car.addInstance("myCar");
		Generic green = color.addInstance("green");
		Generic red = color.addInstance("red");
		myCar.addLink(carColor, null, green);
		myCar.addLink(carColor, null, red);
		catchAndCheckCause(() -> myCar.getLink(carColor), AmbiguousSelectionException.class);
	}

	public void test_getLinkAmbiguousName() {
		final Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic red = color.addInstance("red");
		Generic blue = color.addInstance("blue");
		myVehicle.addLink(vehicleColor, "myVehicleRed", red);
		myVehicle.addLink(vehicleColor, "myVehicleRed", blue);
		catchAndCheckCause(() -> myVehicle.getLink(vehicleColor, "myVehicleRed"), AmbiguousSelectionException.class);
	}

	public void test_redondantTarget() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic door = engine.addInstance("Door");
		Generic carColorDoor = car.addRelation("CarColorDoor", color, door);
		Generic myCar = car.addInstance("myCar");
		Generic green = color.addInstance("green");
		Generic rightDoor = door.addInstance("rightDoor");
		Generic myCarColorDoor = myCar.addLink(carColorDoor, null, green, rightDoor);
		assert myCarColorDoor.equals(myCar.getLink(carColorDoor, myCar));
	}

	public void test_getLinkReverse() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic carColor = car.addRelation("CarColor", color);
		Generic myCar = car.addInstance("myCar");
		Generic red = color.addInstance("red");
		Generic myCarRed = myCar.addLink(carColor, null, red);
		assert myCarRed.equals(red.getLink(carColor, myCar));
	}

	public void test_getLinkTernaire_reverse() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic door = engine.addInstance("Door");
		Generic carColorDoor = car.addRelation("CarColorDoor", color, door);
		Generic myCar = car.addInstance("myCar");
		Generic green = color.addInstance("green");
		Generic rightDoor = door.addInstance("rightDoor");
		Generic myCarRigtDoorGreen = myCar.addLink(carColorDoor, null, green, rightDoor);
		assert myCarRigtDoorGreen.equals(myCar.getLink(carColorDoor, rightDoor, green));
	}

	public void test_getLinkTernaire_type() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic door = engine.addInstance("Door");
		Generic carColorDoor = car.addRelation("CarColorDoor", color, door);
		Generic myCar = car.addInstance("myCar");
		Generic green = color.addInstance("green");
		Generic rightDoor = door.addInstance("rightDoor");
		Generic myCarRigtDoorGreen = myCar.addLink(carColorDoor, null, green, rightDoor);
		assert myCarRigtDoorGreen.equals(myCar.getLink(carColorDoor, color, door));
	}

	public void test_getLinkTernaire_type_reverse() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic door = engine.addInstance("Door");
		Generic carColorDoor = car.addRelation("CarColorDoor", color, door);
		Generic myCar = car.addInstance("myCar");
		Generic green = color.addInstance("green");
		Generic rightDoor = door.addInstance("rightDoor");
		Generic myCarRigtDoorGreen = myCar.addLink(carColorDoor, null, green, rightDoor);
		assert myCarRigtDoorGreen.equals(myCar.getLink(carColorDoor, door, color));
	}

	public void test_getLinkTernaire_redoundant() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic door = engine.addInstance("Door");
		Generic carColorDoor = car.addRelation("CarColorDoor", color, door);
		Generic myCar = car.addInstance("myCar");
		Generic green = color.addInstance("green");
		Generic rightDoor = door.addInstance("rightDoor");
		myCar.addLink(carColorDoor, null, green, rightDoor);
		assert null == myCar.getLink(carColorDoor, green, green) : myCar.getLink(carColorDoor, green, green).info();
	}

	public void test_getLinkDefaultLink() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic carColor = car.addRelation("CarColor", color);
		Generic myCar = car.addInstance("myCar");
		Generic green = color.addInstance("green");
		car.addLink(carColor, null, green);
		Generic myCarGreen = myCar.addLink(carColor, null, green);
		assert myCarGreen.equals(myCar.getLink(carColor));
	}

	public void test_getLinkDefaultLink_reverse() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic carColor = car.addRelation("CarColor", color);
		Generic myCar = car.addInstance("myCar");
		Generic green = color.addInstance("green");
		car.addLink(carColor, null, green);
		Generic myCarGreen = myCar.addLink(carColor, null, green);
		assert myCarGreen.equals(green.getLink(carColor));
	}

	public void test_getLinkDefaultLink2() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic carColor = car.addRelation("CarColor", color);
		Generic myCar = car.addInstance("myCar");
		Generic green = color.addInstance("green");
		Generic carGreen = car.addLink(carColor, null, green);
		assert carGreen.equals(myCar.getLink(carColor));
	}

}
