package org.genericsystem.kernel;

import java.util.List;

import org.genericsystem.api.exception.AliveConstraintViolationException;
import org.testng.annotations.Test;

@Test
public class RelationTest extends AbstractTest {

	public void test001_addInstance_NotAliveException() {
		final Root cache = new Root();
		Vertex car = cache.addInstance("Car");
		Vertex color = cache.addInstance("Color");
		final Vertex carColor = cache.addInstance("CarColor", car, color);
		final Vertex myCar = car.addInstance("myCar");
		final Vertex green = color.addInstance("green");
		myCar.remove();
		assert !myCar.isAlive();
		catchAndCheckCause(() -> carColor.addInstance("myCarColor", myCar, green), AliveConstraintViolationException.class);

	}

	public void test001_addInstance_NotAliveException_withMetaRelation() {
		final Root root = new Root();
		Vertex metaRelation = root.setInstance(root.getValue(), root, root);
		Vertex car = root.addInstance("Car");
		Vertex color = root.addInstance("Color");
		final Vertex carColor = root.addInstance("CarColor", car, color);
		assert carColor.isInstanceOf(metaRelation);
		final Vertex myCar = car.addInstance("myCar");
		final Vertex green = color.addInstance("green");
		myCar.remove();
		assert !myCar.isAlive();
		catchAndCheckCause(() -> carColor.addInstance("myCarColor", myCar, green), AliveConstraintViolationException.class);
	}

	public void test002_addInstance_2composites() {
		final Root cache = new Root();
		Vertex car = cache.addInstance("Car");
		Vertex color = cache.addInstance("Color");
		final Vertex carColor = cache.addInstance("CarColor", car, color);
		final Vertex myCar = car.addInstance("myCar");
		final Vertex green = color.addInstance("green");
		assert myCar.isAlive();
		carColor.addInstance("myCarColor", myCar, green);
	}

	public void test002_addInstance_2composites_MetaRelation() {
		final Root root = new Root();
		Vertex metaRelation = root.setInstance(root.getValue(), root, root);
		Vertex car = root.addInstance("Car");
		Vertex color = root.addInstance("Color");
		final Vertex carColor = root.addInstance("CarColor", car, color);
		assert carColor.isInstanceOf(metaRelation);
		final Vertex myCar = car.addInstance("myCar");
		final Vertex green = color.addInstance("green");
		assert myCar.isAlive();
		carColor.addInstance("myCarColor", myCar, green);
	}

	public void test003_addInstance_reflexiveRelation() {
		final Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex car = vehicle.addInstance("Car");
		Vertex caravane = vehicle.addInstance("Caravane");
		Vertex vehicleHaveSameOwnerAsVehicle = root.addInstance("VehicleHaveSameOwnerAsVehicle", vehicle, vehicle);
		Vertex myVehicleHaveSameOwnerAsVehicle = vehicleHaveSameOwnerAsVehicle.addInstance("myVehicleHaveSameOwnerAsVehicle", car, caravane);
		List<Vertex> composites = myVehicleHaveSameOwnerAsVehicle.getComponents();
		assert composites.size() == 2 : composites.size();
		assert composites.contains(caravane) : composites;
		assert composites.contains(car) : composites;
	}

	public void test003_addInstance_reflexiveRelation_MetaRelation() {
		final Root root = new Root();
		Vertex metaRelation = root.setInstance(root.getValue(), root, root);
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex car = vehicle.addInstance("Car");
		Vertex caravane = vehicle.addInstance("Caravane");
		Vertex vehicleHaveSameOwnerAsVehicle = root.addInstance("VehicleHaveSameOwnerAsVehicle", vehicle, vehicle);
		assert vehicleHaveSameOwnerAsVehicle.isInstanceOf(metaRelation);
		Vertex myVehicleHaveSameOwnerAsVehicle = vehicleHaveSameOwnerAsVehicle.addInstance("myVehicleHaveSameOwnerAsVehicle", car, caravane);
		List<Vertex> composites = myVehicleHaveSameOwnerAsVehicle.getComponents();
		assert composites.size() == 2 : composites.size();
		assert composites.contains(caravane) : composites;
		assert composites.contains(car) : composites;
	}

}
