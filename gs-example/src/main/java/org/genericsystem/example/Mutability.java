package org.genericsystem.example;

import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class Mutability {
	public void updateType() {
		Engine engine = new Engine();

		Generic vehicle = engine.addInstance("VehicleZ");
		Generic car = engine.addInstance(vehicle, "Car");

		// Update the value of vehicle
		vehicle.updateValue("Vehicle");
		// dependencies (here car) are automatically updated

		assert vehicle.isAlive();
		assert car.isAlive();
	}
}
