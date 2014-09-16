package org.genericsystem.kernel;

import java.util.List;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.NotAliveException;
import org.genericsystem.kernel.AbstractTest.RollbackCatcher;
import org.testng.annotations.Test;

@Test
public class RelationTest {

	public void test001_addInstance_NotAliveException() {
		final Root cache = new Root();
		Vertex car = cache.addInstance("Car");
		Vertex color = cache.addInstance("Color");
		final Vertex carColor = cache.addInstance("CarColor", car, color);
		final Vertex myCar = car.addInstance("myCar");
		final Vertex green = color.addInstance("green");
		myCar.remove();
		assert !myCar.isAlive();
		new RollbackCatcher() {
			@Override
			public void intercept() {
				carColor.addInstance("myCarColor", myCar, green);
			}
		}.assertIsCausedBy(NotAliveException.class);
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
		new RollbackCatcher() {
			@Override
			public void intercept() {
				carColor.addInstance("myCarColor", myCar, green);
			}
		}.assertIsCausedBy(NotAliveException.class);
	}

	public void test002_addInstance_2components() {
		final Root cache = new Root();
		Vertex car = cache.addInstance("Car");
		Vertex color = cache.addInstance("Color");
		final Vertex carColor = cache.addInstance("CarColor", car, color);
		final Vertex myCar = car.addInstance("myCar");
		final Vertex green = color.addInstance("green");
		assert myCar.isAlive();
		carColor.addInstance("myCarColor", myCar, green);
	}

	public void test002_addInstance_2components_MetaRelation() {
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
		List<Vertex> components = myVehicleHaveSameOwnerAsVehicle.getComponents();
		assert components.size() == 2 : components.size();
		assert components.contains(caravane) : components;
		assert components.contains(car) : components;
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
		List<Vertex> components = myVehicleHaveSameOwnerAsVehicle.getComponents();
		assert components.size() == 2 : components.size();
		assert components.contains(caravane) : components;
		assert components.contains(car) : components;
	}

	public void test004_addInstance_OverridenRelation() {
		final Root root = new Root();
		Vertex human = root.addInstance("Human");
		Vertex bob = human.addInstance("Bob");
		Vertex jane = human.addInstance("Jane");
		Vertex humanIsBrotherOfHuman = root.addInstance("HumanIsBrotherOfHuman", human, human);
		Vertex bobIsBrotherOfHuman = root.addInstance(humanIsBrotherOfHuman, "BobIsBrotherOfHuman", bob, human);
		Vertex bobIsBrotherOfJane = humanIsBrotherOfHuman.addInstance("BobIsBrotherOfJane", bob, jane);
		assert bobIsBrotherOfHuman.inheritsFrom(humanIsBrotherOfHuman);
		assert bobIsBrotherOfJane.isInstanceOf(bobIsBrotherOfHuman) : bobIsBrotherOfJane.info();
		Snapshot<Vertex> instances = bobIsBrotherOfHuman.getInstances();
		assert instances.size() == 1 : instances.size();
		assert instances.contains(bobIsBrotherOfJane) : instances;
	}

	public void test004_addInstance_OverridenRelation_MetaRelation() {
		final Root root = new Root();
		Vertex metaRelation = root.setInstance(root.getValue(), root, root);
		Vertex human = root.addInstance("Human");
		Vertex bob = human.addInstance("Bob");
		Vertex jane = human.addInstance("Jane");
		Vertex humanIsBrotherOfHuman = root.addInstance("HumanIsBrotherOfHuman", human, human);
		assert humanIsBrotherOfHuman.isInstanceOf(metaRelation);
		Vertex bobIsBrotherOfHuman = root.addInstance(humanIsBrotherOfHuman, "BobIsBrotherOfHuman", bob, human);
		Vertex bobIsBrotherOfJane = humanIsBrotherOfHuman.addInstance("bobIsBrotherOfJane", bob, jane);
		assert bobIsBrotherOfHuman.inheritsFrom(humanIsBrotherOfHuman);
		assert bobIsBrotherOfJane.isInstanceOf(bobIsBrotherOfHuman);
		Snapshot<Vertex> instances = bobIsBrotherOfHuman.getInstances();
		assert instances.size() == 1 : instances.size();
		assert instances.contains(bobIsBrotherOfJane) : instances;
	}

	public void test005_addInstance_OverridenRelation_OverridenComponent() {
		final Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex car = vehicle.addInstance("Car");
		Vertex microCar = car.addInstance("MicroCar");
		Vertex caravane = vehicle.addInstance("Caravane");
		Vertex vehicleHaveSameOwnerAsVehicle = root.addInstance("VehicleHaveSameOwnerAsVehicle", vehicle, vehicle);
		Vertex carHaveSameOwnerAsVehicle = root.addInstance(vehicleHaveSameOwnerAsVehicle, "carHaveSameOwnerAsVehicle", car, vehicle);
		Vertex mycarHaveSameOwnerAsCaravane = vehicleHaveSameOwnerAsVehicle.addInstance("myCarHaveSameOwnerAsCaravane", microCar, caravane);
		assert mycarHaveSameOwnerAsCaravane.isInstanceOf(carHaveSameOwnerAsVehicle);
		Snapshot<Vertex> instances = carHaveSameOwnerAsVehicle.getInstances();
		assert instances.size() == 1 : instances.size();
		assert instances.contains(mycarHaveSameOwnerAsCaravane) : instances;
	}

	public void test005_addInstance_OverridenRelation_OverridenComponent_MetaRelation() {
		final Root root = new Root();
		Vertex metaRelation = root.setInstance(root.getValue(), root, root);
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex car = vehicle.addInstance("Car");
		Vertex microCar = car.addInstance("MicroCar");
		Vertex caravane = vehicle.addInstance("Caravane");
		Vertex vehicleHaveSameOwnerAsVehicle = root.addInstance("VehicleHaveSameOwnerAsVehicle", vehicle, vehicle);
		assert vehicleHaveSameOwnerAsVehicle.isInstanceOf(metaRelation);
		Vertex carHaveSameOwnerAsVehicle = root.addInstance(vehicleHaveSameOwnerAsVehicle, "carHaveSameOwnerAsVehicle", car, vehicle);
		Vertex mycarHaveSameOwnerAsCaravane = vehicleHaveSameOwnerAsVehicle.addInstance("myCarHaveSameOwnerAsCaravane", microCar, caravane);
		assert mycarHaveSameOwnerAsCaravane.isInstanceOf(carHaveSameOwnerAsVehicle);
		Snapshot<Vertex> instances = carHaveSameOwnerAsVehicle.getInstances();
		assert instances.size() == 1 : instances.size();
		assert instances.contains(mycarHaveSameOwnerAsCaravane) : instances;
	}

	public void test006_addInstance_OverridenRelation() {
		final Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex car = vehicle.addInstance("Car");
		Vertex caravane = vehicle.addInstance("Caravane");
		Vertex vehicleHaveSameOwnerAsVehicle = root.addInstance("VehicleHaveSameOwnerAsVehicle", vehicle, vehicle);
		Vertex carHaveSameOwnerAsVehicle = root.addInstance(vehicleHaveSameOwnerAsVehicle, "carHaveSameOwnerAsVehicle", vehicle, car);
		Vertex mycarHaveSameOwnerAsCaravane = vehicleHaveSameOwnerAsVehicle.addInstance("myCarHaveSameOwnerAsCaravane", caravane, car);
		assert carHaveSameOwnerAsVehicle.inheritsFrom(vehicleHaveSameOwnerAsVehicle);
		assert mycarHaveSameOwnerAsCaravane.isInstanceOf(carHaveSameOwnerAsVehicle);
		Snapshot<Vertex> instances = carHaveSameOwnerAsVehicle.getInstances();
		assert instances.size() == 1 : instances.size();
		assert instances.contains(mycarHaveSameOwnerAsCaravane) : instances;
	}

	public void test006_addInstance_OverridenRelation_MetaRelation() {
		final Root root = new Root();
		Vertex metaRelation = root.setInstance(root.getValue(), root, root);
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex car = vehicle.addInstance("Car");
		Vertex caravane = vehicle.addInstance("Caravane");
		Vertex vehicleHaveSameOwnerAsVehicle = root.addInstance("VehicleHaveSameOwnerAsVehicle", vehicle, vehicle);
		assert vehicleHaveSameOwnerAsVehicle.isInstanceOf(metaRelation);
		Vertex carHaveSameOwnerAsVehicle = root.addInstance(vehicleHaveSameOwnerAsVehicle, "carHaveSameOwnerAsVehicle", vehicle, car);
		Vertex mycarHaveSameOwnerAsCaravane = vehicleHaveSameOwnerAsVehicle.addInstance("myCarHaveSameOwnerAsCaravane", caravane, car);
		assert carHaveSameOwnerAsVehicle.inheritsFrom(vehicleHaveSameOwnerAsVehicle);
		assert mycarHaveSameOwnerAsCaravane.isInstanceOf(carHaveSameOwnerAsVehicle);
		Snapshot<Vertex> instances = carHaveSameOwnerAsVehicle.getInstances();
		assert instances.size() == 1 : instances.size();
		assert instances.contains(mycarHaveSameOwnerAsCaravane) : instances;
	}

	public void test007_addInstance_OverridenRelation_OverridenComponent() {
		final Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex car = vehicle.addInstance("Car");
		Vertex microCar = car.addInstance("MicroCar");
		Vertex caravane = vehicle.addInstance("Caravane");
		Vertex vehicleHaveSameOwnerAsVehicle = root.addInstance("VehicleHaveSameOwnerAsVehicle", vehicle, vehicle);
		Vertex carHaveSameOwnerAsVehicle = root.addInstance(vehicleHaveSameOwnerAsVehicle, "carHaveSameOwnerAsVehicle", vehicle, car);
		Vertex mycarHaveSameOwnerAsCaravane = vehicleHaveSameOwnerAsVehicle.addInstance("myCarHaveSameOwnerAsCaravane", caravane, microCar);
		assert mycarHaveSameOwnerAsCaravane.isInstanceOf(carHaveSameOwnerAsVehicle);
		Snapshot<Vertex> instances = carHaveSameOwnerAsVehicle.getInstances();
		assert instances.size() == 1 : instances.size();
		assert instances.contains(mycarHaveSameOwnerAsCaravane) : instances;
	}

	public void test007_addInstance_OverridenRelation_OverridenComponent_MetaRelation() {
		final Root root = new Root();
		Vertex metaRelation = root.setInstance(root.getValue(), root, root);
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex car = vehicle.addInstance("Car");
		Vertex microCar = car.addInstance("MicroCar");
		Vertex caravane = vehicle.addInstance("Caravane");
		Vertex vehicleHaveSameOwnerAsVehicle = root.addInstance("VehicleHaveSameOwnerAsVehicle", vehicle, vehicle);
		assert vehicleHaveSameOwnerAsVehicle.isInstanceOf(metaRelation);
		Vertex carHaveSameOwnerAsVehicle = root.addInstance(vehicleHaveSameOwnerAsVehicle, "carHaveSameOwnerAsVehicle", vehicle, car);
		Vertex mycarHaveSameOwnerAsCaravane = vehicleHaveSameOwnerAsVehicle.addInstance("myCarHaveSameOwnerAsCaravane", caravane, microCar);
		assert mycarHaveSameOwnerAsCaravane.isInstanceOf(carHaveSameOwnerAsVehicle);
		Snapshot<Vertex> instances = carHaveSameOwnerAsVehicle.getInstances();
		assert instances.size() == 1 : instances.size();
		assert instances.contains(mycarHaveSameOwnerAsCaravane) : instances;
	}
}
