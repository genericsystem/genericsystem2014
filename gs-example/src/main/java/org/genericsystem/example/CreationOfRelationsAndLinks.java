package org.genericsystem.example;

import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class CreationOfRelationsAndLinks {
	public void createRelation() {
		Engine engine = new Engine();

		// Create the types Vehicle and Color
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");

		// Create the relation VehicleColor between Vehicle and Color
		vehicle.addRelation("VehicleColor", color);
	}

	public void createLink() {
		Engine engine = new Engine();

		// Create the types Vehicle and Color
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");

		// Create the relation VehicleColor between Vehicle and Color
		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);

		// Create an instance of type Vehicle and an instance of type Color
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic red = color.addInstance("red");

		// Do the link between the instance of the vehicle and the instance of the color
		myVehicle.addLink(vehicleColor, "myVehicleRed", red);
	}
}
