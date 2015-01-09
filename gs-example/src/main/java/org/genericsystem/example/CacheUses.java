package org.genericsystem.example;

import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class CacheUses {
	public void simpleUse() {
		// Create an in memory engine
		Engine engine = new Engine();
		// A cache is automatically created

		// Create a type vehicle
		Generic vehicle = engine.addInstance("Vehicle");
		// Create an attribute options to the vehicle
		Generic options = vehicle.addAttribute("Options");

		// Create an instance of vehicle
		Generic myVehicle = vehicle.addInstance("myVehicle");
		// Add an option to myVehicle
		myVehicle.addHolder(options, "Music player");

		// Get the current cache and validate the modifications done on it : vehicle and others will be visible in other caches
		engine.getCurrentCache().flush();
	}

	public void mountCache() {
		// Create an in memory engine
		Engine engine = new Engine();
		// A cache is automatically created

		// Create a type vehicle
		Generic vehicle = engine.addInstance("Vehicle");

		// Create a property power on vehicle
		Generic power = vehicle.addAttribute("Power").enablePropertyConstraint();

		// Mount a cache on the current cache
		engine.getCurrentCache().mount();

		// Create an instance of vehicle
		Generic myVehicle = vehicle.addInstance("myVehicle");

		// Instantiate a power on it
		myVehicle.addHolder(power, 213);
		// myVehicle has one power : 213

		// Add another power on myVehicle
		myVehicle.addHolder(power, 220);
		// error : power is a property, it can have only one value

		// A rollback is performed by Generic System
		// The instance of power and the instance of vehicle are lost
		// but thanks to the cache we mount, the vehicle and its instance are NOT lost
	}
}
