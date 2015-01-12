package org.genericsystem.kernel;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.api.exception.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.kernel.Config.MetaRelation;
import org.testng.annotations.Test;

@Test
public class RemoveTest extends AbstractTest {

	public void test001_removeTypeWithHolder() {
		Vertex engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex power = car.addAttribute("Power");
		Vertex myBmw = car.addInstance("myBmw");
		myBmw.addHolder(power, 123);

		catchAndCheckCause(() -> car.remove(), ReferentialIntegrityConstraintViolationException.class);
	}

	public void test001_simpleHolder() {
		Vertex engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = car.addAttribute("Color");
		Vertex myBmw = car.addInstance("myBmw");
		Vertex myBmwRed = myBmw.addHolder(color, "red");

		assert myBmw.getHolders(color).contains(myBmwRed) : myBmw.getHolders(color).info();
		assert myBmw.getHolders(color).size() == 1;

		myBmwRed.remove();

		assert myBmw.getHolders(color).size() == 0;
	}

	public void test002_multipleHolders() {
		Vertex engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = car.addAttribute("Color");
		Vertex myBmw = car.addInstance("myBmw");
		Vertex myBmwRed = myBmw.addHolder(color, "red");
		Vertex myBmwBlue = myBmw.addHolder(color, "blue");

		myBmwRed.remove();
		assert myBmw.getHolders(color).contains(myBmwBlue);
		assert myBmw.getHolders(color).size() == 1;

		Vertex myBmwGreen = myBmw.addHolder(color, "green");

		myBmwBlue.remove();
		assert myBmw.getHolders(color).contains(myBmwGreen);
		assert myBmw.getHolders(color).size() == 1;
	}

	public void test003_removeAndAdd() {
		Vertex engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = car.addAttribute("Color");
		Vertex myBmw = car.addInstance("myBmw");
		Vertex myBmwRed = myBmw.addHolder(color, "red");
		Vertex myBmwBlue = myBmw.addHolder(color, "blue");

		myBmwRed.remove();
		myBmwRed = myBmw.addHolder(color, "red");

		assert myBmw.getHolders(color).contains(myBmwRed);
		assert myBmw.getHolders(color).contains(myBmwBlue);
		assert myBmw.getHolders(color).size() == 2;
	}

	public void test004_removeAndAddAndRemove() {
		Vertex engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = car.addAttribute("Color");
		Vertex myBmw = car.addInstance("myBmw");
		Vertex myBmwRed = myBmw.addHolder(color, "red");
		Vertex myBmwBlue = myBmw.addHolder(color, "blue");

		myBmwRed.remove();
		myBmwRed = myBmw.addHolder(color, "red");
		myBmwRed.remove();

		assert myBmw.getHolders(color).contains(myBmwBlue);
		assert myBmw.getHolders(color).size() == 1;
	}

	public void test005_removeConcret_withHolder() {
		Vertex engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = car.addAttribute("Color");
		Vertex myBmw = car.addInstance("myBmw");
		Vertex myBmwRed = myBmw.addHolder(color, "red");

		assert color.getInstances().contains(myBmwRed);
		assert color.getInstances().size() == 1;
		myBmw.remove();

		assert car.getInstances().size() == 0;
		assert color.getInstances().size() == 0;
	}

	public void test006_removeStructural_withHolder() {
		Vertex engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = car.addAttribute("Color");
		Vertex myBmw = car.addInstance("myBmw");
		Vertex myBmwRed = myBmw.addHolder(color, "red");
		myBmw.remove();
		car.remove();

		assert !engine.getInstances().contains(car);
		assert !engine.getInstances().contains(color);
	}

	public void test007_removeConcretAndAttribut() {
		Vertex engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = car.addAttribute("Color");
		Vertex myBmw = car.addInstance("myBmw");
		Vertex myBmwRed = myBmw.addHolder(color, "red");
		myBmw.remove();
		color.remove();

		assert engine.getInstances().contains(car);
		assert !car.getInstances().contains(myBmw);
		assert !engine.getInstances().contains(color);

	}

	public void test008_removeInstanceAndAttribute() {
		Vertex engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = car.addAttribute("Color");
		Vertex myBmw = car.addInstance("myBmw");
		Vertex myBmwRed = myBmw.addHolder(color, "red");
		myBmwRed.remove();
		color.remove();

		assert engine.getInstances().contains(car);
		assert car.getInstances().contains(myBmw);
		assert !engine.getInstances().contains(color);

	}

	public void test009_removeConcret() {
		Vertex engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = car.addAttribute("Color");
		Vertex myBmw = car.addInstance("myBmw");
		Vertex myBmwRed = myBmw.addHolder(color, "red");
		myBmwRed.remove();

		assert color.getInstances().size() == 0;

	}

	public void test010_cascadeRemove() {
		Root engine = new Root();

		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex color = engine.addInstance("Color");
		Vertex vehicleColor = vehicle.addRelation("VehicleColor", color);

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
