package org.genericsystem.example;

import org.genericsystem.kernel.Statics;
import org.genericsystem.mutability.Engine;
import org.genericsystem.mutability.Generic;

public class BinaryRelations {
	public void oneToOneRelation() {
		Engine engine = new Engine();

		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);

		// Make vehicleColor a 1-1 relation
		vehicleColor.enableSingularConstraint(Statics.BASE_POSITION);
		vehicleColor.enableSingularConstraint(Statics.TARGET_POSITION);

		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic yourVehicle = vehicle.addInstance("yourVehicle");
		Generic red = color.addInstance("red");
		Generic yellow = color.addInstance("yellow");

		// Create the link between myVehicle and red from the relation vehicleColor
		myVehicle.addLink(vehicleColor, "myVehicleRed", red); // OK

		// Flush the changes done in cache
		engine.getCurrentCache().flush();

		// Create the link between myVehicle and yellow from the relation vehicleColor
		try {
			myVehicle.addLink(vehicleColor, "myVehicleYellow", yellow);
		} catch (Exception e) {
			// error : myVehicle has more than one link : [myVehicleRed, myVehicleYellow] for attribute : VehicleColor
		}

		// Create the link between yourVehicle and red from the relation vehicleColor
		try {
			yourVehicle.addLink(vehicleColor, "yourVehicleRed", red);
		} catch (Exception e) {
			// error : red has more than one link : [myVehicleRed, yourVehicleRed] for attribute : VehicleColor
		}
	}

	public void oneToManyRelation() {
		Engine engine = new Engine();

		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);

		// Make vehicleColor a 1-n relation
		vehicleColor.enableSingularConstraint(Statics.BASE_POSITION);

		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic yourVehicle = vehicle.addInstance("yourVehicle");
		Generic red = color.addInstance("red");
		Generic yellow = color.addInstance("yellow");

		// Create the link between myVehicle and red from the relation vehicleColor
		myVehicle.addLink(vehicleColor, "myVehicleRed", red); // OK

		// Flush the changes done in cache
		engine.getCurrentCache().flush();

		// Create the link between myVehicle and yellow from the relation vehicleColor
		try {
			myVehicle.addLink(vehicleColor, "myVehicleYellow", yellow);
		} catch (Exception e) {
			// error : myVehicle has more than one link : [myVehicleRed, myVehicleYellow] for attribute : VehicleColor
		}

		// Create the link between yourVehicle and red from the relation vehicleColor
		yourVehicle.addLink(vehicleColor, "yourVehicleRed", red); // OK
	}

	public void manyToOneRelation() {
		Engine engine = new Engine();

		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);

		// Make vehicleColor a n-1 relation
		vehicleColor.enableSingularConstraint(Statics.TARGET_POSITION);

		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic yourVehicle = vehicle.addInstance("yourVehicle");
		Generic red = color.addInstance("red");
		Generic yellow = color.addInstance("yellow");

		// Create the link between myVehicle and red from the relation vehicleColor
		myVehicle.addLink(vehicleColor, "myVehicleRed", red); // OK

		// Flush the changes done in cache
		engine.getCurrentCache().flush();

		// Create the link between myVehicle and yellow from the relation vehicleColor
		myVehicle.addLink(vehicleColor, "myVehicleYellow", yellow); // OK

		// Create the link between yourVehicle and red from the relation vehicleColor
		yourVehicle.addLink(vehicleColor, "yourVehicleRed", red);
		// error : red has more than one link : [myVehicleRed, yourVehicleRed] for attribute : VehicleColor
	}

	public void manyToManyRelation() {
		Engine engine = new Engine();

		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);

		// Make vehicleColor a n-n relation : nothing to do, it is the case by default

		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic yourVehicle = vehicle.addInstance("yourVehicle");
		Generic red = color.addInstance("red");
		Generic yellow = color.addInstance("yellow");

		// Create the link between myVehicle and red from the relation vehicleColor
		myVehicle.addLink(vehicleColor, "myVehicleRed", red); // OK

		// Flush the changes done in cache
		engine.getCurrentCache().flush();

		// Create the link between myVehicle and yellow from the relation vehicleColor
		myVehicle.addLink(vehicleColor, "myVehicleYellow", yellow); // OK

		// Create the link between yourVehicle and red from the relation vehicleColor
		yourVehicle.addLink(vehicleColor, "yourVehicleRed", red); // OK
	}
}
