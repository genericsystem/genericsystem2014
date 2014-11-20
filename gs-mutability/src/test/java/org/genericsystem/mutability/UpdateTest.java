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
