package org.genericsystem.example;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class ConstraintsUses {
	public void propertyConstraint() {
		Engine engine = new Engine();

		// Create a type Vehicle
		Generic vehicle = engine.addInstance("Vehicle");
		// Create the attribute Options for the type Vehicle
		Generic options = vehicle.addAttribute("Options");
		// Only one value for Options : enable property constraint
		options.enablePropertyConstraint();

		// Create an instance of Vehicle
		Generic myVehicle = vehicle.addInstance("myVehicle");
		// Add values for Options
		myVehicle.addHolder(options, "music player");
		myVehicle.addHolder(options, "air conditioning");
		// Error : Power is a property, it can have only one value
	}

	public void instanceValueClassConstraint() {
		Engine engine = new Engine();

		// Create a type Vehicle
		Generic vehicle = engine.addInstance("Vehicle");
		// Create the attribute Options for the type Vehicle
		Generic options = vehicle.addAttribute("Options");
		// Constrains the type of Options to String
		options.enableClassConstraint(String.class);

		// Create an instance of Vehicle
		Generic myVehicle = vehicle.addInstance("myVehicle");
		// Add values for Options
		myVehicle.addHolder(options, "music player"); // OK
		myVehicle.addHolder(options, 123);
		// Error : class of attribute Options is String
	}

	public void singularConstraint() {
		Engine engine = new Engine();

		// Create a type Vehicle
		Generic vehicle = engine.addInstance("Vehicle");
		// Create a type Color
		Generic color = engine.addInstance("Color");
		// Create the relation VehicleColor between Vehicle and Color
		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);
		// Instances of Vehicle can be linked to 1 Color maximum
		vehicleColor.enableSingularConstraint(ApiStatics.BASE_POSITION);

		// Create an instance of Vehicle
		Generic myVehicle = vehicle.addInstance("myVehicle");
		// Create an instance of Color
		Generic red = color.addInstance("red");
		// Create another instance of Color
		Generic yellow = color.addInstance("yellow");

		// Create the link between myVehicle and red from the relation VehicleColor
		myVehicle.addLink(vehicleColor, "myVehicleRed", red); // OK
		// Create the link between myVehicle and yellow from the relation VehicleColor
		myVehicle.addLink(vehicleColor, "myVehicleYellow", yellow);
		// Error : myVehicle has more than one link
	}
}
