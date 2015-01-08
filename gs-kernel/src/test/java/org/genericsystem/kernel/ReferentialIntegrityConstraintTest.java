package org.genericsystem.kernel;

import org.genericsystem.api.exception.ReferentialIntegrityConstraintViolationException;
import org.testng.annotations.Test;

@Test
public class ReferentialIntegrityConstraintTest extends AbstractTest {

	public void test001_enableReferentialIntegrity_remove() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex color = engine.addInstance("Color");
		vehicle.addAttribute("VehicleColor", color);
		color.enableReferentialIntegrity(Statics.BASE_POSITION);
		catchAndCheckCause(() -> color.remove(), ReferentialIntegrityConstraintViolationException.class);
	}

	public void test002_enableReferentialIntegrity_remove() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex color = engine.addInstance("Color");
		vehicle.addAttribute("VehicleColor", color);
		vehicle.remove();
	}

	public void test004_enableReferentialIntegrity_remove() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex color = engine.addInstance("Color");
		vehicle.addAttribute("VehicleColor", color);
		engine.getMetaAttribute().disableReferentialIntegrity(Statics.BASE_POSITION);
		vehicle.remove();
	}

	public void test005_enableReferentialIntegrity_remove() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVechile");
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("red");
		Vertex vehicleColor = vehicle.addAttribute("vehicleColor", color);
		myVehicle.addHolder(vehicleColor, "myVehicleRed", red);
		color.enableReferentialIntegrity(Statics.BASE_POSITION);
		catchAndCheckCause(() -> red.remove(), ReferentialIntegrityConstraintViolationException.class);

	}

	public void test006_enableReferentialIntegrity_remove() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVechile");
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("red");
		Vertex vehicleColor = vehicle.addAttribute("vehicleColor", color);
		myVehicle.addHolder(vehicleColor, "myVehicleRed", red);
		myVehicle.remove();
	}

	public void test007_enableReferentialIntegrity_remove() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVechile");
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("red");
		Vertex vehicleColor = vehicle.addAttribute("vehicleColor", color);
		myVehicle.addHolder(vehicleColor, "myVehicleRed", red);
		catchAndCheckCause(() -> red.remove(), ReferentialIntegrityConstraintViolationException.class);
	}

	public void test008_enableReferentialIntegrity_remove() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVechile");
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("red");
		Vertex vehicleColor = vehicle.addAttribute("vehicleColor", color);
		myVehicle.addHolder(vehicleColor, "myVehicleRed", red);
		engine.getMetaAttribute().disableReferentialIntegrity(Statics.BASE_POSITION);
		myVehicle.remove();
	}
}
