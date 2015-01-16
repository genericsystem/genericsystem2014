package org.genericsystem.example;

import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class CreationOfAttributesPropertiesAndHolders {
	public void createAttribute() {
		Engine engine = new Engine();

		// Create a type vehicle with an attribute Options
		Generic vehicle = engine.addInstance("Vehicle");
		Generic options = vehicle.addAttribute("Options");

		// Create an instance of Vehicle and instantiate two Options on it
		Generic myVehicle = vehicle.addInstance("myVehicle");
		myVehicle.addHolder(options, "music player");
		myVehicle.addHolder(options, "air conditioning");
		// myVehicle has two Options : music player and air conditioning
	}

	public void createProperty() {
		Engine engine = new Engine();

		// Create a type Vehicle with a property Power
		Generic vehicle = engine.addInstance("Vehicle");
		Generic power = vehicle.addAttribute("Power").enablePropertyConstraint();

		// Create an instance of Vehicle and instantiate a Power on it
		Generic myVehicle = vehicle.addInstance("myVehicle");
		myVehicle.addHolder(power, 213);
		// myVehicle has one Power : 213

		// Add another Power on myVehicle
		myVehicle.addHolder(power, 220);
		// Error : Power is a property, it can have only one value
	}
}
