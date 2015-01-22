package org.genericsystem.example;

import java.util.Arrays;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.Meta;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.kernel.annotations.value.StringValue;
import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class PossibilitiesOfGenericSystem {
	public void staticSetting() {
		// Create a database named vehicleManagement, persisted in the directory vehicleManagement and specifying parameterized classes
		Engine engine = new Engine("vehicleManagement", System.getenv("HOME") + "/vehicleManagement", Vehicle.class, Options.class, Color.class, VehicleColor.class, FirstVehicle.class, MusicPlayer.class, Red.class, FirstVehicleRed.class);

		// Retrieve our system vehicleManagement
		Vehicle vehicle = engine.find(Vehicle.class);

		// Manage our system vehicleManagement
		Options airConditioning = Options.createOptions("air conditioning");
		vehicle.addOption(airConditioning);
		assert vehicle.getOption().equals(airConditioning);
		assert airConditioning.getOwner().equals(vehicle);

		// Commit the transaction
		engine.getCurrentCache().flush();
	}

	// classes for example staticSetting

	@SystemGeneric
	public static class Vehicle {
		private Options options;

		public Options getOption() {
			return options;
		}

		public void addOption(Options option) {
			options = option;
			options.owner = this;
		}

		public void removeOption() {
			options.owner = null;
			options = null;
		}
	}

	@SystemGeneric
	@Components(Vehicle.class)
	public static class Options {
		private String name;

		private Vehicle owner;

		public static Options createOptions(String name) {
			Options option = new Options();
			option.name = name;
			return option;
		}

		public String getName() {
			return name;
		}

		public Vehicle getOwner() {
			return owner;
		}
	}

	@SystemGeneric
	public static class Color {
	}

	@SystemGeneric
	@Components({ Vehicle.class, Color.class })
	public static class VehicleColor {
	}

	@SystemGeneric
	@Meta(Vehicle.class)
	@StringValue("firstVehicle")
	public static class FirstVehicle {
	}

	@SystemGeneric
	@Meta(Options.class)
	@Components(FirstVehicle.class)
	@StringValue("music player")
	public static class MusicPlayer {
	}

	@SystemGeneric
	@Meta(Color.class)
	@StringValue("red")
	public static class Red {
	}

	@SystemGeneric
	@Meta(VehicleColor.class)
	@Components({ FirstVehicle.class, Red.class })
	@StringValue("firstVehicleRed")
	public static class FirstVehicleRed {
	}

	public void hotStructuralModification() {
		// Create a database named vehicleManagement and persisted in the directory vehicleManagement
		Engine engine = new Engine("vehicleManagement", System.getenv("HOME") + "/vehicleManagement");

		// Create the structure
		Generic vehicle = engine.addInstance("Vehicle");

		// Add data
		Generic firstVehicle = vehicle.addInstance("firstVehicle");
		Generic secondVehicle = vehicle.addInstance("secondVehicle");
		Generic thirdVehicle = vehicle.addInstance("thirdVehicle");

		// Modify the structure
		Generic brand = vehicle.addAttribute("Brand");

		// Add new data
		Generic cheetah = firstVehicle.addHolder(brand, "cheetah");
		Generic infernus = secondVehicle.addHolder(brand, "infernus");
		Generic phoenix = thirdVehicle.addHolder(brand, "phoenix");

		// Commit the transaction
		engine.getCurrentCache().flush();
	}

	public void mutability() {
		// Create a database named vehicleManagement and persisted in the directory vehicleManagement
		Engine engine = new Engine("vehicleManagement", System.getenv("HOME") + "/vehicleManagement");

		// Create the structure
		Generic vehicle = engine.addInstance("Vehicle");

		// Add data
		Generic firstVehicle = vehicle.addInstance("firstVehicle");
		Generic secondVehicle = vehicle.addInstance("secondVehicle");
		Generic thirdVehicle = vehicle.addInstance("thirdVehicle");

		// Update the name of the table
		vehicle.updateValue("VehicleTable");

		// Add new data
		Generic fourthVehicle = vehicle.addInstance("fourthVehicle");
		Generic fifthVehicle = vehicle.addInstance("fifthVehicle");

		// Commit the transaction
		engine.getCurrentCache().flush();
	}

	public void simpleInheriting() {
		// Create a database named vehicleManagement and persisted in the directory vehicleManagement
		Engine engine = new Engine("vehicleManagement", System.getenv("HOME") + "/vehicleManagement");

		// Create the structure
		Generic vehicle = engine.addInstance("Vehicle");
		Generic brand = vehicle.addAttribute("Brand");

		Generic car = engine.addInstance(vehicle, "Car");
		Generic numberOfPassengers = car.addAttribute("NumberOfPassengers");

		Generic truck = engine.addInstance(vehicle, "Truck");
		Generic maximumLoad = truck.addAttribute("MaximumLoad");

		// Add data
		Generic firstCar = car.addInstance("firstCar");
		Generic cheetah = firstCar.addHolder(brand, "cheetah");
		Generic numberOfPassengersForFirstCar = firstCar.addHolder(numberOfPassengers, 2);

		Generic firstTruck = truck.addInstance("firstTruck");
		Generic yankee = firstTruck.addHolder(brand, "yankee");
		Generic maximumLoadForFirstTruck = firstTruck.addHolder(maximumLoad, 2000);

		// Commit the transaction
		engine.getCurrentCache().flush();
	}

	public void multipleInheriting() {
		// Create a database named vehicleManagement and persisted in the directory vehicleManagement
		Engine engine = new Engine("vehicleManagement", System.getenv("HOME") + "/vehicleManagement");

		// Create the structure
		Generic vehicle = engine.addInstance("Vehicle");
		Generic brand = vehicle.addAttribute("Brand");

		Generic human = engine.addInstance("Human");
		Generic name = human.addAttribute("Name");

		Generic transformer = engine.addInstance(Arrays.asList(vehicle, human), "Transformer");

		// Add data
		Generic firstTransformer = transformer.addInstance("firstTransformer");
		Generic cheetah = firstTransformer.addHolder(brand, "Cheetah");
		Generic super500 = firstTransformer.addHolder(name, "super500");

		// Commit the transaction
		engine.getCurrentCache().flush();
	}

	public void defaultLink() {
		// Create a database named vehicleManagement and persisted in the directory vehicleManagement
		Engine engine = new Engine("vehicleManagement", System.getenv("HOME") + "/vehicleManagement");

		// Create the structure
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");

		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);

		Generic white = color.addInstance("white");
		Generic defaultWhiteVehicle = vehicle.addLink(vehicleColor, "defaultWhiteVehicle", white);

		// Add data
		Generic firstVehicle = vehicle.addInstance("firstVehicle");

		Generic secondVehicle = vehicle.addInstance("secondVehicle");
		Generic yellow = color.addInstance("yellow");
		Generic yellowSecondVehicle = secondVehicle.addLink(vehicleColor, "yellowSecondVehicle", yellow);

		// Commit the transaction
		engine.getCurrentCache().flush();
	}

	public void queryDatabase() {
		// Create a database named vehicleManagement and persisted in the directory vehicleManagement
		Engine engine = new Engine("vehicleManagement", System.getenv("HOME") + "/vehicleManagement");

		// Create the structure
		Generic vehicle = engine.addInstance("Vehicle");
		Generic power = vehicle.addAttribute("Power");

		Generic color = engine.addInstance("Color");

		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);

		// Add data
		Generic firstVehicle = vehicle.addInstance("firstVehicle");
		Generic power30 = firstVehicle.addHolder(power, 30);
		Generic white = color.addInstance("white");
		Generic whiteFirstVehicle = firstVehicle.addLink(vehicleColor, "whiteFirstVehicle", white);

		Generic secondVehicle = vehicle.addInstance("secondVehicle");
		Generic power55 = secondVehicle.addHolder(power, 55);
		Generic whiteSecondVehicle = secondVehicle.addLink(vehicleColor, "whiteSecondVehicle", white);

		Generic thirdVehicle = vehicle.addInstance("thirdVehicle");
		Generic power50 = thirdVehicle.addHolder(power, 50);
		Generic yellow = color.addInstance("yellow");
		Generic yellowThirdVehicle = thirdVehicle.addLink(vehicleColor, "yellowThirdVehicle", yellow);

		Generic fourthVehicle = vehicle.addInstance("fourthVehicle");
		Generic power89 = fourthVehicle.addHolder(power, 89);
		Generic whiteFourthVehicle = fourthVehicle.addLink(vehicleColor, "whiteFourthVehicle", white);

		Generic fifthVehicle = vehicle.addInstance("fifthVehicle");
		Generic power120 = fifthVehicle.addHolder(power, 120);
		Generic red = color.addInstance("red");
		Generic redFifthVehicle = fifthVehicle.addLink(vehicleColor, "redFifthVehicle", red);

		// Query the database
		Snapshot<Generic> searchedVehicles = () -> vehicle.getInstances().get().filter(
														generic -> generic.getHolders(power).get().anyMatch(
																holder -> (int) holder.getValue() >= 50 && (int) holder.getValue() <= 90
																)
													).filter(
														generic -> generic.getLinks(vehicleColor).get().anyMatch(
																link -> link.getTargetComponent().equals(white)
														)
													);

		// Commit the transaction
		engine.getCurrentCache().flush();
	}
}
