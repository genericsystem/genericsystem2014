package org.genericsystem.example;

import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class CreationOfRelationsAndLinks {
	public void createRelation() {
		Engine engine = new Engine();

		// 1. Create the structure : Vehicle, Color and the relation VehicleColor
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);

		// 2. Instantiate a Vehicle and a Color
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic red = color.addInstance("red");

		// 3. Do the link between the instance of the vehicle and the color
		myVehicle.addLink(vehicleColor, "myVehicleRed", red);
	}
}
