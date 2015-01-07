package org.genericsystem.example;

import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class CreationOfRelationsAndLinks {
	public void createRelation() {
		Engine engine = new Engine();

		// 1. Create the structure : Vehicle, Color and the relation VehicleColor
		Generic vehicle = engine.addType("Vehicle");
		Generic color = engine.addType("Color");
		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);

		// 2. Instantiate a vehicle and a color
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic red = color.addInstance("red");

		// 3. Do the link between the instance of the vehicle and the color
		myVehicle.addLink(vehicleColor, "myVehicleRed", red);
	}
}
