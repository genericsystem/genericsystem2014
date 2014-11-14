package org.genericsystem.cache;

import org.testng.annotations.Test;

@Test
public class ApiImproveTest extends AbstractTest {

	public void test001_mutabilityOnUpdate() {
		Engine engine = new Engine();
		Generic car = engine.addInstance("Car");
		Generic vehicle = engine.addInstance("Vehicle");
		car.updateSupers(vehicle);
		Generic myCar = car.addInstance("myCar");

	}
	//
	// public void test002_mutabilityOnUpdate() {
	// Engine engine = new Engine();
	// Generic car = engine.addInstance("Car");
	// Generic vehicle = engine.addInstance("Vehicle");
	// Generic car2 = car.updateSupers(vehicle);
	// assert !car.isAlive();
	// Generic myCar = car2.addInstance("myCar");
	//
	// }
	//
	// public void test002_addByValue() {
	// Engine engine = new Engine();
	// Generic car = engine.addInstance("Car");
	// Generic vehicle = engine.addInstance("Vehicle");
	// car = car.updateSupers(vehicle);
	// Generic myCar = car.addInstance("myCar");
	// vehicle.addAttribute("Power");
	// myCar.addHolder("Power", 123);
	// }
	//
	// public void test003_forceUpdate() {
	// Engine engine = new Engine();
	// Generic car = engine.addInstance("Car");
	// Generic vehicle = engine.addInstance("Vehicle");
	// car = car.updateSupers(vehicle);
	// Generic myCar = car.addInstance("myCar");
	// Generic power = vehicle.addAttribute("Power");
	//
	// Generic v123 = myCar.addHolder(power, 123);
	// assert myCar.getHolders(power).contains(v123);
	// assert myCar.getHolders(power).size() == 1;
	//
	// car = car.forceUpdateSupers();
	// }
	//
	// public void test003_removePartOfSupers() {
	// Engine engine = new Engine();
	// Generic car = engine.addInstance("Car");
	// Generic vehicle = engine.addInstance("Vehicle");
	// Generic object = engine.addInstance("Object");
	// car = car.updateSupers(vehicle, object);
	// assert car.getSupers().contains(vehicle);
	// assert car.getSupers().contains(object);
	// assert car.getSupers().size() == 2;
	// car = car.removeSupers(vehicle);
	// assert car.getSupers().contains(object);
	// assert car.getSupers().size() == 1;
	// }
	//
	// public void test004_removeHolderByValue() {
	// Engine engine = new Engine();
	// Generic car = engine.addInstance("Car");
	// Generic vehicle = engine.addInstance("Vehicle");
	// car = car.updateSupers(vehicle);
	// Generic myCar = car.addInstance("myCar");
	// Generic power = vehicle.addAttribute("Power");
	// Generic v123 = myCar.addHolder(power, 123);
	// myCar.removeHolder(123);
	// }
	//
	// public void test005_addRelation() {
	// Engine engine = new Engine();
	// Generic car = engine.addInstance("Car");
	// Generic power = engine.addInstance("Power");
	// Generic carPower = engine.addRelation("carPower", car, power);
	// }
	//
	// public void test006_addRelationByName() {
	// Engine engine = new Engine();
	// Generic car = engine.addInstance("Car");
	// Generic power = engine.addInstance("Power");
	// Generic carPower = engine.addRelation("carPower", "Car", "Power");
	// }
}
