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
		new RollbackCatcher() {

			@Override
			public void intercept() {
				vehicle.remove();
			}
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	}

	public void test002_enableReferentialIntegrity_remove() {
		Root engine = new Root();
		Vertex vehicle = engine.addType("Vehicle");
		Vertex color = engine.addType("Color");
		vehicle.addAttribute("VehicleColor", color);
		vehicle.remove();
	}

	public void test003_enableReferentialIntegrity_remove() {
		Root engine = new Root();
		Vertex vehicle = engine.addType("Vehicle");
		Vertex color = engine.addType("Color");
		vehicle.addAttribute("VehicleColor", color);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				color.remove();
			}
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	}

	public void test004_enableReferentialIntegrity_remove() {
		Root engine = new Root();
		Vertex vehicle = engine.addType("Vehicle");
		Vertex color = engine.addType("Color");
		vehicle.addAttribute("VehicleColor", color);
		engine.getMetaAttribute().disableReferentialIntegrity(Statics.TARGET_POSITION);
		color.remove();
	}

	// ------------------------

	public void test005_enableReferentialIntegrity_remove() {
		Root engine = new Root();
		Vertex vehicle = engine.addType("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVechile");
		Vertex color = engine.addType("Color");
		Vertex red = color.addInstance("red");
		myVehicle.addAttribute("myVehicleRed", red);
		engine.getMetaAttribute().enableReferentialIntegrity(Statics.BASE_POSITION);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				myVehicle.remove();
			}
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	}

	public void test006_enableReferentialIntegrity_remove() {
		Root engine = new Root();
		Vertex vehicle = engine.addType("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVechile");
		Vertex color = engine.addType("Color");
		Vertex red = color.addInstance("red");
		myVehicle.addAttribute("myVehicleRed", red);
		myVehicle.remove();
	}

	public void test007_enableReferentialIntegrity_remove() {
		Root engine = new Root();
		Vertex vehicle = engine.addType("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVechile");
		Vertex color = engine.addType("Color");
		Vertex red = color.addInstance("red");
		myVehicle.addAttribute("myVehicleRed", red);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				red.remove();
			}
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	}

	public void test008_enableReferentialIntegrity_remove() {
		Root engine = new Root();
		Vertex vehicle = engine.addType("Vehicle");
		Vertex myVehicle = vehicle.addInstance("myVechile");
		Vertex color = engine.addType("Color");
		Vertex red = color.addInstance("red");
		myVehicle.addAttribute("myVehicleRed", red);
		engine.getMetaAttribute().disableReferentialIntegrity(Statics.TARGET_POSITION);
		red.remove();
	}
}
