package org.genericsystem.kernel;

import java.util.List;

import org.genericsystem.api.exception.AliveConstraintViolationException;
import org.testng.annotations.Test;

@Test
public class RelationTest extends AbstractTest {

	public void test001_addInstance_NotAliveException() {
		final Root cache = new Root();
		Generic car = cache.addInstance("Car");
		Generic color = cache.addInstance("Color");
		final Generic carColor = cache.addInstance("CarColor", car, color);
		final Generic myCar = car.addInstance("myCar");
		final Generic green = color.addInstance("green");
		myCar.remove();
		assert !myCar.isAlive();
		catchAndCheckCause(() -> carColor.addInstance("myCarColor", myCar, green), AliveConstraintViolationException.class);

	}

	public void test001_addInstance_NotAliveException_withMetaRelation() {
		final Root root = new Root();
		Generic car = root.addInstance("Car");
		Generic color = root.addInstance("Color");
		final Generic carColor = root.addInstance("CarColor", car, color);
		assert carColor.isInstanceOf(root.getMetaRelation());
		final Generic myCar = car.addInstance("myCar");
		final Generic green = color.addInstance("green");
		myCar.remove();
		assert !myCar.isAlive();
		catchAndCheckCause(() -> carColor.addInstance("myCarColor", myCar, green), AliveConstraintViolationException.class);
	}

	public void test002_addInstance_2composites() {
		final Root cache = new Root();
		Generic car = cache.addInstance("Car");
		Generic color = cache.addInstance("Color");
		final Generic carColor = cache.addInstance("CarColor", car, color);
		final Generic myCar = car.addInstance("myCar");
		final Generic green = color.addInstance("green");
		assert myCar.isAlive();
		carColor.addInstance("myCarColor", myCar, green);
	}

	public void test002_addInstance_2composites_MetaRelation() {
		final Root root = new Root();
		Generic car = root.addInstance("Car");
		Generic color = root.addInstance("Color");
		final Generic carColor = root.addInstance("CarColor", car, color);
		assert carColor.isInstanceOf(root.getMetaRelation());
		final Generic myCar = car.addInstance("myCar");
		final Generic green = color.addInstance("green");
		assert myCar.isAlive();
		carColor.addInstance("myCarColor", myCar, green);
	}

	public void test003_addInstance_reflexiveRelation() {
		final Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic car = vehicle.addInstance("Car");
		Generic caravane = vehicle.addInstance("Caravane");
		Generic vehicleHaveSameOwnerAsVehicle = root.addInstance("VehicleHaveSameOwnerAsVehicle", vehicle, vehicle);
		Generic myVehicleHaveSameOwnerAsVehicle = vehicleHaveSameOwnerAsVehicle.addInstance("myVehicleHaveSameOwnerAsVehicle", car, caravane);
		List<Generic> composites = myVehicleHaveSameOwnerAsVehicle.getComponents();
		assert composites.size() == 2 : composites.size();
		assert composites.contains(caravane) : composites;
		assert composites.contains(car) : composites;
	}

	public void test003_addInstance_reflexiveRelation_MetaRelation() {
		final Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic car = vehicle.addInstance("Car");
		Generic caravane = vehicle.addInstance("Caravane");
		Generic vehicleHaveSameOwnerAsVehicle = root.addInstance("VehicleHaveSameOwnerAsVehicle", vehicle, vehicle);
		assert vehicleHaveSameOwnerAsVehicle.isInstanceOf(root.getMetaRelation());
		Generic myVehicleHaveSameOwnerAsVehicle = vehicleHaveSameOwnerAsVehicle.addInstance("myVehicleHaveSameOwnerAsVehicle", car, caravane);
		List<Generic> composites = myVehicleHaveSameOwnerAsVehicle.getComponents();
		assert composites.size() == 2 : composites.size();
		assert composites.contains(caravane) : composites;
		assert composites.contains(car) : composites;
	}

	public void test_getRelation() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic carColor = car.addRelation("carColor", color);
		assert carColor.equals(car.getRelation("carColor"));
	}

	public void test_getRelation2() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic carColor = car.addRelation("carColor", color);
		assert carColor.equals(car.getRelation("carColor", car));
	}

	public void test_getRelation3() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic carColor = car.addRelation("carColor", color);
		assert carColor.equals(car.getRelation("carColor", car, color));
	}

	public void test_getRelation4() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic carColor = car.addRelation("carColor", color);
		assert carColor.equals(car.getRelation("carColor", color));
	}

	public void test_getRelation5() {
		final Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic door = engine.addInstance("Door");
		Generic carColor = car.addRelation("carColor", color);
		Generic carDoor = car.addRelation("carDoor", door);
		assert carColor.equals(car.getRelation("carColor"));
		assert carDoor.equals(car.getRelation("carDoor"));
	}

}
