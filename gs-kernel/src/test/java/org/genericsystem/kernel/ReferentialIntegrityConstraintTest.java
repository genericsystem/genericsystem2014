package org.genericsystem.kernel;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.api.exception.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.kernel.Config.MetaRelation;
import org.testng.annotations.Test;

@Test
public class ReferentialIntegrityConstraintTest extends AbstractTest {

	public void test001_enableReferentialIntegrity_remove() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex color = engine.addInstance("Color");
		vehicle.addAttribute("VehicleColor", color);
		color.enableReferentialIntegrity(ApiStatics.BASE_POSITION);
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
		engine.getMetaAttribute().disableReferentialIntegrity(ApiStatics.BASE_POSITION);
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
		color.enableReferentialIntegrity(ApiStatics.BASE_POSITION);
		catchAndCheckCause(() -> red.remove(), ReferentialIntegrityConstraintViolationException.class);
	}

	public void test005_enableReferentialIntegrity_remove2() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVechile");
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("red");
		Vertex vehicleColor = vehicle.addAttribute("vehicleColor", color);
		myVehicle.addHolder(vehicleColor, "myVehicleRed", red);
		vehicleColor.enableReferentialIntegrity(ApiStatics.BASE_POSITION);
		catchAndCheckCause(() -> myVehicle.remove(), ReferentialIntegrityConstraintViolationException.class);
	}

	public void test005_enableReferentialIntegrity_remove3() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex color = engine.addInstance("Color");
		vehicle.addAttribute("vehicleColor", color);
		engine.find(MetaRelation.class).enableReferentialIntegrity(ApiStatics.BASE_POSITION);
		catchAndCheckCause(() -> vehicle.remove(), ReferentialIntegrityConstraintViolationException.class);
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
		engine.getMetaAttribute().disableReferentialIntegrity(ApiStatics.BASE_POSITION);
		myVehicle.remove();
	}
}
