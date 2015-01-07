package org.genericsystem.example;

import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class CreationOfTypesSubtypesAndInstances {
	public void createTypeAndInstance() {
		// Create an engine which is in memory
		Engine engine = new Engine();

		// Create a type Vehicle and instantiate it
		Generic vehicle = engine.addType("Vehicle");
		vehicle.addInstance("myVehicle");
	}

	public void createMultiplesInstances() {
		Engine engine = new Engine();

		// Create a type Vehicle
		Generic vehicle = engine.addType("Vehicle");
		// Instantiate a first instance
		vehicle.addInstance("myFirstVehicle");
		// Instantiate a second instance
		vehicle.addInstance("mySecondVehicle");
	}

	public void createTypeAndSubtypes() {
		Engine engine = new Engine();

		// Create a type Vehicle
		Generic vehicle = engine.addType("Vehicle");
		// Create the type Car which is a subtype of Vehicle
		Generic car = engine.addType(vehicle, "Car");
		// Create the type Bike which is another subtype of Vehicle
		Generic bike = engine.addType(vehicle, "Bike");

		// Create an instance of Vehicle
		vehicle.addInstance("myVehicle");
		// Create an instance of Car
		car.addInstance("myCar");
		// Create an instance of Bike
		bike.addInstance("myBike");
	}
}
