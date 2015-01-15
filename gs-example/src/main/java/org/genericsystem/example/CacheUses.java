package org.genericsystem.example;

import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class CacheUses {
	public void simpleUse() {
		// Create an in memory engine
		Engine engine = new Engine();
		// A cache is automatically created

		// Create a type Vehicle
		Generic vehicle = engine.addInstance("Vehicle");
		// Create an attribute Options to the Vehicle
		Generic options = vehicle.addAttribute("Options");

		// Create an instance of Vehicle
		Generic myVehicle = vehicle.addInstance("myVehicle");
		// Add an Options to myVehicle
		myVehicle.addHolder(options, "music player");

		// Get the current cache and validate the modifications done on it
		engine.getCurrentCache().flush();
	}

	public void mountCache() {
		// Create an in memory engine
		Engine engine = new Engine();
		// A cache is automatically created

		// Create a type Vehicle
		Generic vehicle = engine.addInstance("Vehicle");

		// Create a property Power on Vehicle
		Generic power = vehicle.addAttribute("Power").enablePropertyConstraint();

		// Mount a cache on the current cache
		engine.getCurrentCache().mount();

		// Create an instance of Vehicle
		Generic myVehicle = vehicle.addInstance("myVehicle");

		// Instantiate a Power on it
		myVehicle.addHolder(power, 213);
		// myVehicle has one Power : 213

		// Add another Power on myVehicle
		myVehicle.addHolder(power, 220);
		// Error : Power is a property, it can have only one value

		// A rollback is performed by Generic System
		// The instance of Power and the instance of Vehicle are lost
		// but thanks to the cache we mount, the Vehicle and its Power are NOT lost
	}
}
