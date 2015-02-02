package org.genericsystem.example;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class BinaryRelations {
	public void oneToOneRelation() {
		Engine engine = new Engine();

		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);

		// Make VehicleColor a 1-1 relation
		vehicleColor.enableSingularConstraint(ApiStatics.BASE_POSITION);
		vehicleColor.enableSingularConstraint(ApiStatics.TARGET_POSITION);

		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic yourVehicle = vehicle.addInstance("yourVehicle");
		Generic red = color.addInstance("red");
		Generic yellow = color.addInstance("yellow");

		// Create the link between myVehicle and red from the relation VehicleColor
		myVehicle.addLink(vehicleColor, "myVehicleRed", red); // OK

		// Persist changes
		engine.getCurrentCache().flush();

		try {
			// Create the link between myVehicle and yellow from the relation VehicleColor
			myVehicle.addLink(vehicleColor, "myVehicleYellow", yellow);
		} catch (Exception e) {
			// SingularConstraintViolationException : myVehicle has more than one link : [myVehicleRed, myVehicleYellow] for relation VehicleColor
		}

		try {
			// Create the link between yourVehicle and red from the relation VehicleColor
			yourVehicle.addLink(vehicleColor, "yourVehicleRed", red);
		} catch (Exception e) {
			// SingularConstraintViolationException : red has more than one link : [myVehicleRed, yourVehicleRed] for relation VehicleColor
		}
	}

	public void oneToManyRelation() {
		Engine engine = new Engine();

		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);

		// Make VehicleColor a 1-n relation
		vehicleColor.enableSingularConstraint(ApiStatics.BASE_POSITION);

		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic yourVehicle = vehicle.addInstance("yourVehicle");
		Generic red = color.addInstance("red");
		Generic yellow = color.addInstance("yellow");

		// Create the link between myVehicle and red from the relation VehicleColor
		myVehicle.addLink(vehicleColor, "myVehicleRed", red); // OK

		// Persist changes
		engine.getCurrentCache().flush();

		try {
			// Create the link between myVehicle and yellow from the relation VehicleColor
			myVehicle.addLink(vehicleColor, "myVehicleYellow", yellow);
		} catch (Exception e) {
			// SingularConstraintViolationException : myVehicle has more than one link : [myVehicleRed, myVehicleYellow] for relation VehicleColor
		}

		// Create the link between yourVehicle and red from the relation VehicleColor
		yourVehicle.addLink(vehicleColor, "yourVehicleRed", red); // OK

		// Persist changes
		engine.getCurrentCache().flush();
	}

	public void manyToOneRelation() {
		Engine engine = new Engine();

		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);

		// Make VehicleColor a n-1 relation
		vehicleColor.enableSingularConstraint(ApiStatics.TARGET_POSITION);

		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic yourVehicle = vehicle.addInstance("yourVehicle");
		Generic red = color.addInstance("red");
		Generic yellow = color.addInstance("yellow");

		// Create the link between myVehicle and red from the relation VehicleColor
		myVehicle.addLink(vehicleColor, "myVehicleRed", red); // OK

		// Persist changes
		engine.getCurrentCache().flush();

		// Create the link between myVehicle and yellow from the relation VehicleColor
		myVehicle.addLink(vehicleColor, "myVehicleYellow", yellow); // OK

		// Persist changes
		engine.getCurrentCache().flush();

		try {
			// Create the link between yourVehicle and red from the relation VehicleColor
			yourVehicle.addLink(vehicleColor, "yourVehicleRed", red);
		} catch (Exception e) {
			// SingularConstraintViolationException : red has more than one link : [myVehicleRed, yourVehicleRed] for relation VehicleColor
		}
	}

	public void manyToManyRelation() {
		Engine engine = new Engine();

		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);

		// Make VehicleColor a n-m relation : nothing to do, it is the case by default

		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic yourVehicle = vehicle.addInstance("yourVehicle");
		Generic red = color.addInstance("red");
		Generic yellow = color.addInstance("yellow");

		// Create the link between myVehicle and red from the relation VehicleColor
		myVehicle.addLink(vehicleColor, "myVehicleRed", red); // OK

		// Persist changes
		engine.getCurrentCache().flush();

		// Create the link between myVehicle and yellow from the relation VehicleColor
		myVehicle.addLink(vehicleColor, "myVehicleYellow", yellow); // OK

		// Create the link between yourVehicle and red from the relation VehicleColor
		yourVehicle.addLink(vehicleColor, "yourVehicleRed", red); // OK

		// Persist changes
		engine.getCurrentCache().flush();
	}
}
