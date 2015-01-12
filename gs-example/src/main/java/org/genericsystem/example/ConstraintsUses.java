package org.genericsystem.example;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class ConstraintsUses {
	public void propertyConstraint() {
		Engine engine = new Engine();

		// Create a type Vehicle
		Generic vehicle = engine.addInstance("Vehicle");
		// Create the attribute options for the type Vehicle
		Generic options = vehicle.addAttribute("Options");
		// Only one value for options : enable property constraint
		options.enablePropertyConstraint();

		// Create an instance of Vehicle
		Generic myVehicle = vehicle.addInstance("myVehicle");
		// Add values for options
		myVehicle.addHolder(options, "Music player");
		myVehicle.addHolder(options, "Air conditioning");
		// error : power is a property, it can have only one value
	}

	public void instanceValueClassConstraint() {
		Engine engine = new Engine();

		// Create a type Vehicle
		Generic vehicle = engine.addInstance("Vehicle");
		// Create the attribute options for the type Vehicle
		Generic options = vehicle.addAttribute("Options");
		// Constrains the type of options to String
		options.enableClassConstraint(String.class);

		// Create an instance of Vehicle
		Generic myVehicle = vehicle.addInstance("myVehicle");
		// Add values for options
		myVehicle.addHolder(options, "Music player"); // OK
		myVehicle.addHolder(options, 123);
		// error : class of attribute options is String
	}

	public void singularConstraint() {
		Engine engine = new Engine();

		// Create a type Vehicle
		Generic vehicle = engine.addInstance("Vehicle");
		// Create a type Color
		Generic color = engine.addInstance("Color");
		// Create the relation vehicleColor between Vehicle and Color
		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);
		// Instances of Vehicle can be linked to 1 Color maximum
		vehicleColor.enableSingularConstraint(ApiStatics.BASE_POSITION);

		// Create an instance of Vehicle
		Generic myVehicle = vehicle.addInstance("myVehicle");
		// Create an instance of Color
		Generic red = color.addInstance("red");
		// Create another instance of Color
		Generic yellow = color.addInstance("yellow");

		// Create the link between myVehicle and red from the relation vehicleColor
		myVehicle.addLink(vehicleColor, "myVehicleRed", red); // OK
		// Create the link between myVehicle and yellow from the relation vehicleColor
		myVehicle.addLink(vehicleColor, "myVehicleYellow", yellow);
		// error : myVehicle has more than one link
	}
}
