package org.genericsystem.mutability;

import org.testng.annotations.Test;

@Test
public class UpdateTest extends AbstractTest {

	public void test001_updateValue() {
		Engine engine = new Engine();
		Generic car = engine.addInstance("Car");

		Generic carRename = car.updateValue("CarRename");

		assert carRename.isAlive();
		assert carRename.equals(car);
		assert carRename == car;
		assert car.getValue().equals("CarRename");
		assert car.isAlive();
	}

	public void test002_update() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic otherVehicle = engine.addInstance("OtherVehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		assert car.getSupers().contains(vehicle);
		assert car.getSupers().size() == 1;
		assert vehicle.getInheritings().contains(car);

		Generic carUpdate = car.update(otherVehicle, "Car");

		assert car.getSupers().contains(otherVehicle);
		assert car.getSupers().size() == 1;
		assert otherVehicle.getInheritings().contains(car);

		assert carUpdate.getSupers().contains(otherVehicle);
		assert carUpdate.getSupers().size() == 1;
		assert otherVehicle.getInheritings().contains(carUpdate);

		assert vehicle.getInheritings().size() == 0;
	}

	public void test003_update() {
		Engine engine = new Engine();
		Generic car = engine.addInstance("Car");

		Generic carRename = car.updateValue("CarRename");

		assert carRename.isAlive();
		assert carRename.equals(car);
		assert car.isAlive();
	}

	public void test001_removeAndAdd() {
		Engine engine = new Engine();
		Generic car = engine.addInstance("Car");
		assert car.isAlive();
		car.remove();
		assert !car.isAlive();
		engine.addInstance("Car");
		assert car.isAlive();
	}

}
