package org.genericsystem.example;

import java.util.Arrays;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class AdvancedSearches {
	public void findTypes() {
		Engine engine = new Engine();

		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance("Car");
		Generic bike = engine.addInstance("Bike");

		// Find the types vehicle, car, bike
		Snapshot<Generic> types = engine.getInstances();
		assert types.size() >= 3;
		assert types.containsAll(Arrays.asList(vehicle, car, bike));
	}

	public void findAttributes() {
		Engine engine = new Engine();

		Generic vehicle = engine.addInstance("Vehicle");
		Generic options = vehicle.addAttribute("Options");
		Generic wheels = vehicle.addAttribute("Wheels");

		// Find the attributes options, wheels
		Snapshot<Generic> attributes = vehicle.getAttributes();
		assert attributes.size() >= 2;
		assert attributes.containsAll(Arrays.asList(options, wheels));
	}

	public void findRelations() {
		Engine engine = new Engine();

		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);

		// Find the relation VehicleColor from the type Vehicle
		Snapshot<Generic> relationsFromVehicle = vehicle.getRelations();
		assert relationsFromVehicle.size() >= 1;
		assert relationsFromVehicle.containsAll(Arrays.asList(vehicleColor));

		// Find the relation VehicleColor from the type Color
		Snapshot<Generic> relationsFromColor = color.getRelations();
		assert relationsFromColor.size() >= 1;
		assert relationsFromColor.containsAll(Arrays.asList(vehicleColor));
	}

	public void findInstances() {
		Engine engine = new Engine();

		Generic vehicle = engine.addInstance("Vehicle");
		Generic myFirstVehicle = vehicle.addInstance("myFirstVehicle");
		Generic mySecondVehicle = vehicle.addInstance("mySecondVehicle");

		// Find the instances of vehicle
		Snapshot<Generic> instances = vehicle.getInstances();
		assert instances.size() >= 2;
		assert instances.containsAll(Arrays.asList(myFirstVehicle, mySecondVehicle));
	}

	public void findHolders() {
		Engine engine = new Engine();

		Generic vehicle = engine.addInstance("Vehicle");
		Generic options = vehicle.addAttribute("Options");

		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic musicPlayer = myVehicle.addHolder(options, "Music player");
		Generic airConditioning = myVehicle.addHolder(options, "Air conditioning");

		// Find the holders of myVehicle for the attribute Options
		Snapshot<Generic> holders = myVehicle.getHolders(options);
		assert holders.size() >= 2;
		assert holders.containsAll(Arrays.asList(musicPlayer, airConditioning));
	}

	public void findLinks() {
		Engine engine = new Engine();

		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);

		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic red = color.addInstance("red");
		Generic myVehicleRed = myVehicle.addLink(vehicleColor, "myVehicleRed", red);

		// Find the link myVehicleRed for the relation vehicleColor from myVehicle
		Snapshot<Generic> linksFromMyVehicle = myVehicle.getLinks(vehicleColor);
		assert linksFromMyVehicle.size() >= 1;
		assert linksFromMyVehicle.containsAll(Arrays.asList(myVehicleRed));

		// Find the link myVehicleRed for the relation vehicleColor from red
		Snapshot<Generic> linksFromRed = red.getLinks(vehicleColor);
		assert linksFromRed.size() >= 1;
		assert linksFromRed.containsAll(Arrays.asList(myVehicleRed));
	}

	public void filter() {
		Engine engine = new Engine();

		// Create a type Vehicle with options
		Generic vehicle = engine.addInstance("Vehicle");
		Generic options = vehicle.addAttribute("Options");

		// Instantiate three vehicles
		Generic myFirstVehicle = vehicle.addInstance("myFirstVehicle");
		Generic mySecondVehicle = vehicle.addInstance("mySecondVehicle");
		Generic myThirdVehicle = vehicle.addInstance("myThirdVehicle");

		// Instantiate three options, one for each vehicle
		myFirstVehicle.addHolder(options, "Air conditioning");
		mySecondVehicle.addHolder(options, "Music player");
		myThirdVehicle.addHolder(options, "Air conditioning");

		// Find all instances of Vehicle with the option Air conditioning
		Snapshot<Generic> instances = () -> vehicle.getInstances().get().filter(generic -> generic.getHolders(options).get().anyMatch(holder -> holder.getValue().equals("Air conditioning")));

		assert instances.size() >= 2;
		assert instances.containsAll(Arrays.asList(myFirstVehicle, myThirdVehicle));
	}
}
