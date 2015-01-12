package org.genericsystem.example;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.kernel.Config.MetaRelation;
import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class Removes {
	public void removeType() {
		Engine engine = new Engine();

		Generic vehicle = engine.addInstance("Vehicle");

		// Remove the type Vehicle
		vehicle.remove();
	}

	public void removeAttribute() {
		Engine engine = new Engine();

		Generic vehicle = engine.addInstance("Vehicle");
		Generic options = vehicle.addAttribute("Options");

		// Remove the attribute options
		options.remove();
	}

	public void removeRelation() {
		Engine engine = new Engine();

		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);

		// Remove the relation vehicleColor
		vehicleColor.remove();
	}

	public void removeInstance() {
		Engine engine = new Engine();

		Generic vehicle = engine.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");

		// Remove the instance myVehicle
		myVehicle.remove();
	}

	public void removeHolder() {
		Engine engine = new Engine();

		Generic vehicle = engine.addInstance("Vehicle");
		Generic options = vehicle.addAttribute("Options");

		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic musicPlayer = myVehicle.addHolder(options, "Music player");
		myVehicle.addHolder(options, "Air conditioning");

		// Remove the holder musicPlayer
		musicPlayer.remove();
	}

	public void removeLink() {
		Engine engine = new Engine();

		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);

		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic red = color.addInstance("red");

		Generic myVehicleRed = myVehicle.addLink(vehicleColor, "myVehicleRed", red);

		// Remove the link myVehicleRed
		myVehicleRed.remove();
	}

	public void dependentRemove() {
		Engine engine = new Engine();

		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);

		// Remove the type vehicle
		vehicle.remove();
		assert !vehicle.isAlive();
		assert !vehicleColor.isAlive();
		assert color.isAlive();
	}

	public void referentialIntegrity() {
		Engine engine = new Engine();

		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		vehicle.addRelation("VehicleColor", color);

		// Enable referential integrity for vehicle in vehicleColor for the base : vehicle
		engine.find(MetaRelation.class).enableReferentialIntegrity(ApiStatics.BASE_POSITION);

		// Remove the type Vehicle
		vehicle.remove();
		// error : VehicleColor is Referential Integrity for ancestor Vehicle by composite position : 0
	}

	public void cascadeRemove() {
		Engine engine = new Engine();

		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);

		// Disable default referential integrity for vehicle in vehicleColor for the first target : color
		engine.find(MetaRelation.class).disableReferentialIntegrity(ApiStatics.TARGET_POSITION);

		// Enable cascade remove for Color in vehicleColor
		engine.find(MetaRelation.class).enableCascadeRemove(ApiStatics.TARGET_POSITION);

		// Remove the type vehicle
		vehicle.remove();
		assert !vehicle.isAlive();
		assert !vehicleColor.isAlive();
		assert !color.isAlive();
	}
}
