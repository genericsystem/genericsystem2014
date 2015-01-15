package org.genericsystem.example;

import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class Configuration {
	public void mountDataBase() {
		// Create an engine named myDataBase and which is persistent
		Engine engine = new Engine("myDataBase", System.getenv("HOME") + "/my_directory_path");

		// Create a Vehicle with a Color
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = vehicle.addAttribute("Color");

		// Instantiate a Vehicle with a Color red
		Generic myVehicle = vehicle.addInstance("myVehicle");
		myVehicle.addHolder(color, "red");
	}
}
