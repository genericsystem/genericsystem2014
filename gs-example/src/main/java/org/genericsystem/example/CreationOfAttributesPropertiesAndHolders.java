package org.genericsystem.example;

import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class CreationOfAttributesPropertiesAndHolders {
	public void createAttribute() {
		Engine engine = new Engine();

		// Create a type vehicle with an attribute options
		Generic vehicle = engine.addInstance("Vehicle");
		Generic options = vehicle.addAttribute("Options");

		// Create an instance of vehicle and instantiate two options on it
		Generic myVehicle = vehicle.addInstance("myVehicle");
		myVehicle.addHolder(options, "Music player");
		myVehicle.addHolder(options, "Air conditioning");
		// myVehicle has 2 options : Music player and Air conditioning
	}

	public void createProperty() {
		Engine engine = new Engine();

		// Create a type vehicle with a property power
		Generic vehicle = engine.addInstance("Vehicle");
		Generic power = vehicle.addAttribute("Power").enablePropertyConstraint();

		// Create an instance of vehicle and instantiate a power on it
		Generic myVehicle = vehicle.addInstance("myVehicle");
		myVehicle.addHolder(power, 213);
		// myVehicle has one power : 213

		// Update the power of myVehicle
		myVehicle.setHolder(power, 220);
		// myVehicle has always one power which is now 220
	}
}
