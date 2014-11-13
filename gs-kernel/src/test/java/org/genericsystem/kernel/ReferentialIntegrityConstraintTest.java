package org.genericsystem.kernel;

import org.genericsystem.api.exception.ReferentialIntegrityConstraintViolationException;
import org.testng.annotations.Test;

@Test
public class ReferentialIntegrityConstraintTest extends AbstractTest {

	public void test001_enableReferentialIntegrity_remove() {
		Root engine = new Root();
		Vertex vehicle = engine.addType("Vehicle");
		Vertex color = engine.addType("Color");
		vehicle.addAttribute("VehicleColor", color);
		engine.getMetaAttribute().enableReferentialIntegrity(Statics.BASE_POSITION);
		catchAndCheckCause(() -> vehicle.remove(), ReferentialIntegrityConstraintViolationException.class);
	}

	public void test002_enableReferentialIntegrity_remove() {
		Root engine = new Root();
		Vertex vehicle = engine.addType("Vehicle");
		Vertex color = engine.addType("Color");
		vehicle.addAttribute("VehicleColor", color);
		vehicle.remove();
	}

	public void test004_enableReferentialIntegrity_remove() {
		Root engine = new Root();
		Vertex vehicle = engine.addType("Vehicle");
		Vertex color = engine.addType("Color");
		vehicle.addAttribute("VehicleColor", color);
		engine.getMetaAttribute().disableReferentialIntegrity(Statics.BASE_POSITION);
		vehicle.remove();
	}

	public void test005_enableReferentialIntegrity_remove() {
		Root engine = new Root();
		Vertex vehicle = engine.addType("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVechile");
		Vertex color = engine.addType("Color");
		Vertex red = color.addInstance("red");
		Vertex vehicleColor = vehicle.addAttribute("vehicleColor", color);
		myVehicle.addHolder(vehicleColor, "myVehicleRed", red);
		engine.getMetaAttribute().enableReferentialIntegrity(Statics.BASE_POSITION);
		catchAndCheckCause(() -> myVehicle.remove(), ReferentialIntegrityConstraintViolationException.class);

	}

	public void test006_enableReferentialIntegrity_remove() {
		Root engine = new Root();
		Vertex vehicle = engine.addType("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVechile");
		Vertex color = engine.addType("Color");
		Vertex red = color.addInstance("red");
		Vertex vehicleColor = vehicle.addAttribute("vehicleColor", color);
		myVehicle.addHolder(vehicleColor, "myVehicleRed", red);
		myVehicle.remove();
	}

	public void test007_enableReferentialIntegrity_remove() {
		Root engine = new Root();
		Vertex vehicle = engine.addType("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVechile");
		Vertex color = engine.addType("Color");
		Vertex red = color.addInstance("red");
		Vertex vehicleColor = vehicle.addAttribute("vehicleColor", color);
		myVehicle.addHolder(vehicleColor, "myVehicleRed", red);
		catchAndCheckCause(() -> red.remove(), ReferentialIntegrityConstraintViolationException.class);
	}

	public void test008_enableReferentialIntegrity_remove() {
		Root engine = new Root();
		Vertex vehicle = engine.addType("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVechile");
		Vertex color = engine.addType("Color");
		Vertex red = color.addInstance("red");
		Vertex vehicleColor = vehicle.addAttribute("vehicleColor", color);
		myVehicle.addHolder(vehicleColor, "myVehicleRed", red);
		engine.getMetaAttribute().disableReferentialIntegrity(Statics.BASE_POSITION);
		myVehicle.remove();
	}
}
