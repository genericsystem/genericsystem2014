package org.genericsystem.example;

import javax.inject.Inject;

import org.genericsystem.cdi.Engine;
import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.mutability.Generic;
import org.testng.annotations.Test;

@Test
public class CDIUses extends AbstractTest {
	@Inject
	private Engine engine;

	public void staticSetting() {
		// Retrieve annotated classes
		Generic vehicle = engine.find(Vehicle.class);
		Generic options = engine.find(Options.class);
		Generic color = engine.find(Color.class);
		Generic vehicleColor = engine.find(VehicleColor.class);
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

	public void simpleExample() {
		// Concerns structural level
		Generic vehicle = engine.addInstance("Vehicle");
		Generic power = vehicle.addAttribute("Power");

		// Concerns concrete level
		Generic myBmw = vehicle.addInstance("myBmw");
		myBmw.addHolder(power, 233);

		// Get the current cache and validate the modifications done on it
		engine.getCurrentCache().flush();
	}
}
