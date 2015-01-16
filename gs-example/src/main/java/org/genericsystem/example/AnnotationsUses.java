package org.genericsystem.example;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.Meta;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.kernel.annotations.value.StringValue;
import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class AnnotationsUses {
	public void dynamicSetting() {
		// Create the engine
		Engine engine = new Engine();

		// Create the structure
		Generic vehicle = engine.addInstance("Vehicle");
		Generic options = vehicle.addAttribute("Options");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);

		// Add datas
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic musicPlayer = myVehicle.addHolder(options, "music player");
		Generic airConditioning = myVehicle.addHolder(options, "air conditioning");
		Generic red = color.addInstance("red");
		Generic myVehicleRed = myVehicle.addLink(vehicleColor, "myVehicleRed", red);
	}

	public void staticSetting() {
		// Create the engine
		// Give in parameters all annotated classes used later : DON'T FORGET IT or the following code won't run
		Engine engine = new Engine(Vehicle.class, Options.class, Color.class, VehicleColor.class, MyVehicle.class, MusicPlayer.class, AirConditioning.class, Red.class, MyVehicleRed.class);

		// Retrieve the type Vehicle
		Generic vehicle = engine.find(Vehicle.class);
		// Retrieve the attribute Options for the type Vehicle
		Generic options = engine.find(Options.class);
		// Retrieve the type Color
		Generic color = engine.find(Color.class);
		// Retrieve the relation VehicleColor between Vehicle and Color
		Generic vehicleColor = engine.find(VehicleColor.class);

		// Retrieve the instance of Vehicle
		Generic myVehicle = engine.find(MyVehicle.class);
		// Retrieve values for Options
		Generic musicPlayer = engine.find(MusicPlayer.class);
		Generic airConditioning = engine.find(AirConditioning.class);
		// Retrieve the instance of Color
		Generic red = engine.find(Red.class);
		// Retrieve the link between myVehicle and red from the relation VehicleColor
		Generic myVehicleRed = engine.find(MyVehicleRed.class);
	}

	// classes for example staticSetting

	@SystemGeneric
	public static class Vehicle {

	}

	@SystemGeneric
	@Components(Vehicle.class)
	public static class Options {

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
	@StringValue("myVehicle")
	public static class MyVehicle {

	}

	@SystemGeneric
	@Meta(Options.class)
	@Components(MyVehicle.class)
	@StringValue("music player")
	public static class MusicPlayer {

	}

	@SystemGeneric
	@Meta(Options.class)
	@Components(MyVehicle.class)
	@StringValue("air conditioning")
	public static class AirConditioning {

	}

	@SystemGeneric
	@Meta(Color.class)
	@StringValue("red")
	public static class Red {

	}

	@SystemGeneric
	@Meta(VehicleColor.class)
	@Components({ MyVehicle.class, Red.class })
	@StringValue("myVehicleRed")
	public static class MyVehicleRed {

	}

	public void crud() {
		// Create the engine
		// Specifies the user class Phones
		Engine engine = new Engine(Phones.class);

		// Retrieve the type Phones
		Phones phones = engine.find(Phones.class);

		// Add phones
		phones.add("HTC Hero");
		phones.add("Nokia 3210");
		phones.add("Samsung S4");
		phones.add("HTC One");
		phones.add("HTC One");

		// Removes phones
		phones.remove("HTC One");
		phones.remove("HTC One");

		// Get the current cache and validate the modifications done on it
		engine.getCurrentCache().flush();

		assert phones.size() == 3;
		assert !phones.contains("HTC One");

		// Get the current cache and clear it
		engine.getCurrentCache().clear();

		assert phones.size() == 3;
		assert !phones.contains("HTC One");
	}

	// classes for example crud

	public static interface SimpleCRUD<T extends Serializable> extends Snapshot<T> {
		@Override
		@SuppressWarnings("unchecked")
		default Stream<T> get() {
			return ((Generic) this).getInstances().get().map(x -> (T) x.getValue());
		}

		default void add(T value) {
			((Generic) this).setInstance(value);
		}

		default List<T> getValues() {
			return get().collect(Collectors.toList());
		}

		default boolean remove(T value) {
			for (Generic instance : ((Generic) this).getInstances())
				if (Objects.equals(value, instance.getValue())) {
					instance.remove();
					return true;
				}
			return false;
		}
	}

	@SystemGeneric
	public static class Phones implements SimpleCRUD<String> {

	}
}
