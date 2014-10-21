package org.genericsystem.concurrency;

import java.util.List;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.AliveConstraintViolationException;
import org.genericsystem.concurrency.AbstractTest.RollbackCatcher;
import org.testng.annotations.Test;

@Test
public class RelationTest {

	public void test001_addInstance_NotAliveException() {
		final Engine cache = new Engine();
		Generic car = cache.addInstance("Car");
		Generic color = cache.addInstance("Color");
		final Generic carColor = cache.addInstance("CarColor", car, color);
		final Generic myCar = car.addInstance("myCar");
		final Generic green = color.addInstance("green");
		myCar.remove();
		assert !myCar.isAlive();
		new RollbackCatcher() {
			@Override
			public void intercept() {
				carColor.addInstance("myCarColor", myCar, green);
			}
		}.assertIsCausedBy(AliveConstraintViolationException.class);
	}

	public void test001_addInstance_NotAliveException_withMetaRelation() {
		final Engine Engine = new Engine();
		Generic metaRelation = Engine.setInstance(Engine.getValue(), Engine, Engine);
		Generic car = Engine.addInstance("Car");
		Generic color = Engine.addInstance("Color");
		final Generic carColor = Engine.addInstance("CarColor", car, color);
		assert carColor.isInstanceOf(metaRelation);
		final Generic myCar = car.addInstance("myCar");
		final Generic green = color.addInstance("green");
		myCar.remove();
		assert !myCar.isAlive();
		new RollbackCatcher() {
			@Override
			public void intercept() {
				carColor.addInstance("myCarColor", myCar, green);
			}
		}.assertIsCausedBy(AliveConstraintViolationException.class);
	}

	public void test002_addInstance_2components() {
		final Engine cache = new Engine();
		Generic car = cache.addInstance("Car");
		Generic color = cache.addInstance("Color");
		final Generic carColor = cache.addInstance("CarColor", car, color);
		final Generic myCar = car.addInstance("myCar");
		final Generic green = color.addInstance("green");
		assert myCar.isAlive();
		carColor.addInstance("myCarColor", myCar, green);
	}

	public void test002_addInstance_2components_MetaRelation() {
		final Engine Engine = new Engine();
		Generic metaRelation = Engine.setInstance(Engine.getValue(), Engine, Engine);
		Generic car = Engine.addInstance("Car");
		Generic color = Engine.addInstance("Color");
		final Generic carColor = Engine.addInstance("CarColor", car, color);
		assert carColor.isInstanceOf(metaRelation);
		final Generic myCar = car.addInstance("myCar");
		final Generic green = color.addInstance("green");
		assert myCar.isAlive();
		carColor.addInstance("myCarColor", myCar, green);
	}

	public void test003_addInstance_reflexiveRelation() {
		final Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic car = vehicle.addInstance("Car");
		Generic caravane = vehicle.addInstance("Caravane");
		Generic vehicleHaveSameOwnerAsVehicle = Engine.addInstance("VehicleHaveSameOwnerAsVehicle", vehicle, vehicle);
		Generic myVehicleHaveSameOwnerAsVehicle = vehicleHaveSameOwnerAsVehicle.addInstance("myVehicleHaveSameOwnerAsVehicle", car, caravane);
		List<Generic> components = myVehicleHaveSameOwnerAsVehicle.getComposites();
		assert components.size() == 2 : components.size();
		assert components.contains(caravane) : components;
		assert components.contains(car) : components;
	}

	public void test003_addInstance_reflexiveRelation_MetaRelation() {
		final Engine Engine = new Engine();
		Generic metaRelation = Engine.setInstance(Engine.getValue(), Engine, Engine);
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic car = vehicle.addInstance("Car");
		Generic caravane = vehicle.addInstance("Caravane");
		Generic vehicleHaveSameOwnerAsVehicle = Engine.addInstance("VehicleHaveSameOwnerAsVehicle", vehicle, vehicle);
		assert vehicleHaveSameOwnerAsVehicle.isInstanceOf(metaRelation);
		Generic myVehicleHaveSameOwnerAsVehicle = vehicleHaveSameOwnerAsVehicle.addInstance("myVehicleHaveSameOwnerAsVehicle", car, caravane);
		List<Generic> components = myVehicleHaveSameOwnerAsVehicle.getComposites();
		assert components.size() == 2 : components.size();
		assert components.contains(caravane) : components;
		assert components.contains(car) : components;
	}

	public void test004_addInstance_OverridenRelation() {
		final Engine Engine = new Engine();
		Generic human = Engine.addInstance("Human");
		Generic bob = human.addInstance("Bob");
		Generic jane = human.addInstance("Jane");
		Generic humanIsBrotherOfHuman = human.addAttribute("HumanIsBrotherOfHuman", human);
		Generic bobIsBrotherOfHuman = Engine.addInstance(humanIsBrotherOfHuman, "BobIsBrotherOfHuman", bob, human);
		Generic bobIsBrotherOfJane = humanIsBrotherOfHuman.addInstance("BobIsBrotherOfJane", bob, jane);
		assert bobIsBrotherOfHuman.inheritsFrom(humanIsBrotherOfHuman);
		assert bobIsBrotherOfJane.isInstanceOf(bobIsBrotherOfHuman) : bobIsBrotherOfJane.info();
		Snapshot<Generic> instances = bobIsBrotherOfHuman.getInstances();
		assert instances.size() == 1 : instances.size();
		assert instances.contains(bobIsBrotherOfJane) : instances;
	}

	public void test004_addInstance_OverridenRelation_MetaRelation() {
		final Engine Engine = new Engine();
		Generic metaRelation = Engine.setInstance(Engine.getValue(), Engine, Engine);
		Generic human = Engine.addInstance("Human");
		Generic bob = human.addInstance("Bob");
		Generic jane = human.addInstance("Jane");
		Generic humanIsBrotherOfHuman = Engine.addInstance("HumanIsBrotherOfHuman", human, human);
		assert humanIsBrotherOfHuman.isInstanceOf(metaRelation);
		Generic bobIsBrotherOfHuman = Engine.addInstance(humanIsBrotherOfHuman, "BobIsBrotherOfHuman", bob, human);
		Generic bobIsBrotherOfJane = humanIsBrotherOfHuman.addInstance("bobIsBrotherOfJane", bob, jane);
		assert bobIsBrotherOfHuman.inheritsFrom(humanIsBrotherOfHuman);
		assert bobIsBrotherOfJane.isInstanceOf(bobIsBrotherOfHuman);
		Snapshot<Generic> instances = bobIsBrotherOfHuman.getInstances();
		assert instances.size() == 1 : instances.size();
		assert instances.contains(bobIsBrotherOfJane) : instances;
	}

	public void test005_addInstance_OverridenRelation_OverridenComponent() {
		final Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic car = vehicle.addInstance("Car");
		Generic microCar = car.addInstance("MicroCar");
		Generic caravane = vehicle.addInstance("Caravane");
		Generic vehicleHaveSameOwnerAsVehicle = Engine.addInstance("VehicleHaveSameOwnerAsVehicle", vehicle, vehicle);
		Generic carHaveSameOwnerAsVehicle = Engine.addInstance(vehicleHaveSameOwnerAsVehicle, "carHaveSameOwnerAsVehicle", car, vehicle);
		Generic mycarHaveSameOwnerAsCaravane = vehicleHaveSameOwnerAsVehicle.addInstance("myCarHaveSameOwnerAsCaravane", microCar, caravane);
		assert mycarHaveSameOwnerAsCaravane.isInstanceOf(carHaveSameOwnerAsVehicle);
		Snapshot<Generic> instances = carHaveSameOwnerAsVehicle.getInstances();
		assert instances.size() == 1 : instances.size();
		assert instances.contains(mycarHaveSameOwnerAsCaravane) : instances;
	}

	public void test005_addInstance_OverridenRelation_OverridenComponent_MetaRelation() {
		final Engine Engine = new Engine();
		Generic metaRelation = Engine.setInstance(Engine.getValue(), Engine, Engine);
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic car = vehicle.addInstance("Car");
		Generic microCar = car.addInstance("MicroCar");
		Generic caravane = vehicle.addInstance("Caravane");
		Generic vehicleHaveSameOwnerAsVehicle = Engine.addInstance("VehicleHaveSameOwnerAsVehicle", vehicle, vehicle);
		assert vehicleHaveSameOwnerAsVehicle.isInstanceOf(metaRelation);
		Generic carHaveSameOwnerAsVehicle = Engine.addInstance(vehicleHaveSameOwnerAsVehicle, "carHaveSameOwnerAsVehicle", car, vehicle);
		Generic mycarHaveSameOwnerAsCaravane = vehicleHaveSameOwnerAsVehicle.addInstance("myCarHaveSameOwnerAsCaravane", microCar, caravane);
		assert mycarHaveSameOwnerAsCaravane.isInstanceOf(carHaveSameOwnerAsVehicle);
		Snapshot<Generic> instances = carHaveSameOwnerAsVehicle.getInstances();
		assert instances.size() == 1 : instances.size();
		assert instances.contains(mycarHaveSameOwnerAsCaravane) : instances;
	}

	public void test006_addInstance_OverridenRelation() {
		final Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic car = vehicle.addInstance("Car");
		Generic caravane = vehicle.addInstance("Caravane");
		Generic vehicleHaveSameOwnerAsVehicle = Engine.addInstance("VehicleHaveSameOwnerAsVehicle", vehicle, vehicle);
		Generic carHaveSameOwnerAsVehicle = Engine.addInstance(vehicleHaveSameOwnerAsVehicle, "carHaveSameOwnerAsVehicle", vehicle, car);
		Generic mycarHaveSameOwnerAsCaravane = vehicleHaveSameOwnerAsVehicle.addInstance("myCarHaveSameOwnerAsCaravane", caravane, car);
		assert carHaveSameOwnerAsVehicle.inheritsFrom(vehicleHaveSameOwnerAsVehicle);
		assert mycarHaveSameOwnerAsCaravane.isInstanceOf(carHaveSameOwnerAsVehicle);
		Snapshot<Generic> instances = carHaveSameOwnerAsVehicle.getInstances();
		assert instances.size() == 1 : instances.size();
		assert instances.contains(mycarHaveSameOwnerAsCaravane) : instances;
	}

	public void test006_addInstance_OverridenRelation_MetaRelation() {
		final Engine Engine = new Engine();
		Generic metaRelation = Engine.setInstance(Engine.getValue(), Engine, Engine);
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic car = vehicle.addInstance("Car");
		Generic caravane = vehicle.addInstance("Caravane");
		Generic vehicleHaveSameOwnerAsVehicle = Engine.addInstance("VehicleHaveSameOwnerAsVehicle", vehicle, vehicle);
		assert vehicleHaveSameOwnerAsVehicle.isInstanceOf(metaRelation);
		Generic carHaveSameOwnerAsVehicle = Engine.addInstance(vehicleHaveSameOwnerAsVehicle, "carHaveSameOwnerAsVehicle", vehicle, car);
		Generic mycarHaveSameOwnerAsCaravane = vehicleHaveSameOwnerAsVehicle.addInstance("myCarHaveSameOwnerAsCaravane", caravane, car);
		assert carHaveSameOwnerAsVehicle.inheritsFrom(vehicleHaveSameOwnerAsVehicle);
		assert mycarHaveSameOwnerAsCaravane.isInstanceOf(carHaveSameOwnerAsVehicle);
		Snapshot<Generic> instances = carHaveSameOwnerAsVehicle.getInstances();
		assert instances.size() == 1 : instances.size();
		assert instances.contains(mycarHaveSameOwnerAsCaravane) : instances;
	}

	public void test007_addInstance_OverridenRelation_OverridenComponent() {
		final Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic car = vehicle.addInstance("Car");
		Generic microCar = car.addInstance("MicroCar");
		Generic caravane = vehicle.addInstance("Caravane");
		Generic vehicleHaveSameOwnerAsVehicle = Engine.addInstance("VehicleHaveSameOwnerAsVehicle", vehicle, vehicle);
		Generic carHaveSameOwnerAsVehicle = Engine.addInstance(vehicleHaveSameOwnerAsVehicle, "carHaveSameOwnerAsVehicle", vehicle, car);
		Generic mycarHaveSameOwnerAsCaravane = vehicleHaveSameOwnerAsVehicle.addInstance("myCarHaveSameOwnerAsCaravane", caravane, microCar);
		assert mycarHaveSameOwnerAsCaravane.isInstanceOf(carHaveSameOwnerAsVehicle);
		Snapshot<Generic> instances = carHaveSameOwnerAsVehicle.getInstances();
		assert instances.size() == 1 : instances.size();
		assert instances.contains(mycarHaveSameOwnerAsCaravane) : instances;
	}

	public void test007_addInstance_OverridenRelation_OverridenComponent_MetaRelation() {
		final Engine Engine = new Engine();
		Generic metaRelation = Engine.setInstance(Engine.getValue(), Engine, Engine);
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic car = vehicle.addInstance("Car");
		Generic microCar = car.addInstance("MicroCar");
		Generic caravane = vehicle.addInstance("Caravane");
		Generic vehicleHaveSameOwnerAsVehicle = Engine.addInstance("VehicleHaveSameOwnerAsVehicle", vehicle, vehicle);
		assert vehicleHaveSameOwnerAsVehicle.isInstanceOf(metaRelation);
		Generic carHaveSameOwnerAsVehicle = Engine.addInstance(vehicleHaveSameOwnerAsVehicle, "carHaveSameOwnerAsVehicle", vehicle, car);
		Generic mycarHaveSameOwnerAsCaravane = vehicleHaveSameOwnerAsVehicle.addInstance("myCarHaveSameOwnerAsCaravane", caravane, microCar);
		assert mycarHaveSameOwnerAsCaravane.isInstanceOf(carHaveSameOwnerAsVehicle);
		Snapshot<Generic> instances = carHaveSameOwnerAsVehicle.getInstances();
		assert instances.size() == 1 : instances.size();
		assert instances.contains(mycarHaveSameOwnerAsCaravane) : instances;
	}
}
