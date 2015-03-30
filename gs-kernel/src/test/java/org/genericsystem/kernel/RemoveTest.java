package org.genericsystem.kernel;

import java.util.Arrays;
import java.util.Collections;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.api.core.exceptions.ReferentialIntegrityConstraintViolationException;
import org.testng.annotations.Test;

@Test
public class RemoveTest extends AbstractTest {

	public void test001_conserveRemove() {
		Generic engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic myBmw = car.addInstance("myBmw");
		Generic red = color.addInstance("red");
		Generic carColor = car.addRelation("CarColor", color);
		Generic carRed = car.addLink(carColor, "carRed", red);
		myBmw.addLink(carColor, carRed, "myBmwRed", red);

		carRed.conserveRemove();
		Generic myBmwRed = myBmw.getLink(carColor, "myBmwRed", red);
		assert myBmwRed != null;
		assert myBmwRed.isAlive();
		assert myBmwRed.getSupers().size() == 0;
		assert !carRed.isAlive();
	}

	public void test002_conserveRemove() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("vehicle");
		Generic color = engine.addInstance("Color");
		Generic outsideColor = engine.addInstance(color, "OutsideColor");

		Generic myBmw = vehicle.addInstance("myBmw");
		Generic red = color.addInstance("red");
		Generic outsideRed = outsideColor.addInstance("OutsideRed");

		Generic vehicleColor = vehicle.addRelation("vehicleColor", color);
		Generic vehicleRed = vehicle.addLink(vehicleColor, "vehicleRed", red);

		Generic vehicleOutsideColor = vehicle.addRelation(vehicleColor, "vehicleOutsideColor", outsideColor);
		Generic vehicleOutsideRed = vehicle.addLink(vehicleOutsideColor, "vehicleOutsideRed", outsideRed);

		myBmw.addLink(vehicleOutsideColor, vehicleOutsideRed, "myBmwOutsideRed", outsideRed);

		vehicleRed.conserveRemove();
		Generic myBmwRed = myBmw.getLink(vehicleOutsideColor, "myBmwOutsideRed", outsideRed);
		assert myBmwRed != null;
		assert myBmwRed.isAlive();
		assert myBmwRed.getSupers().size() == 1;
		assert myBmwRed.getSupers().get(0).equals(vehicleOutsideColor, Collections.emptyList(), "vehicleOutsideRed", Arrays.asList(vehicle, outsideRed)) : myBmwRed.getSupers().get(0).info();
		assert !vehicleRed.isAlive();
	}

	public void test003_conserveRemove() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("vehicleColor", color);
		vehicleColor.getMeta().disableReferentialIntegrity(ApiStatics.TARGET_POSITION);
		color.conserveRemove();

		Generic newVehicleColor = vehicle.getAttribute("vehicleColor");
		assert !vehicleColor.isAlive();
		assert newVehicleColor.isAlive();
		assert newVehicleColor.getComponents().size() == 1 : newVehicleColor.info();
		assert newVehicleColor.getComponent(ApiStatics.BASE_POSITION).equals(vehicle) : vehicle.info() + " " + vehicle.getComposites();
	}

	public void test001_removeTypeWithHolder() {
		Generic engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic power = car.addAttribute("Power");
		Generic myBmw = car.addInstance("myBmw");
		myBmw.addHolder(power, 123);

		catchAndCheckCause(() -> car.remove(), ReferentialIntegrityConstraintViolationException.class);
	}

	public void test001_simpleHolder() {
		Generic engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");
		Generic myBmwRed = myBmw.addHolder(color, "red");

		assert myBmw.getHolders(color).contains(myBmwRed) : myBmw.getHolders(color).info();
		assert myBmw.getHolders(color).size() == 1;

		myBmwRed.remove();

		assert myBmw.getHolders(color).size() == 0;
	}

	public void test002_multipleHolders() {
		Generic engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");
		Generic myBmwRed = myBmw.addHolder(color, "red");
		Generic myBmwBlue = myBmw.addHolder(color, "blue");

		myBmwRed.remove();
		assert myBmw.getHolders(color).contains(myBmwBlue);
		assert myBmw.getHolders(color).size() == 1;

		Generic myBmwGreen = myBmw.addHolder(color, "green");

		myBmwBlue.remove();
		assert myBmw.getHolders(color).contains(myBmwGreen);
		assert myBmw.getHolders(color).size() == 1;
	}

	public void test003_removeAndAdd() {
		Generic engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");
		Generic myBmwRed = myBmw.addHolder(color, "red");
		Generic myBmwBlue = myBmw.addHolder(color, "blue");

		myBmwRed.remove();
		myBmwRed = myBmw.addHolder(color, "red");

		assert myBmw.getHolders(color).contains(myBmwRed);
		assert myBmw.getHolders(color).contains(myBmwBlue);
		assert myBmw.getHolders(color).size() == 2;
	}

	public void test004_removeAndAddAndRemove() {
		Generic engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");
		Generic myBmwRed = myBmw.addHolder(color, "red");
		Generic myBmwBlue = myBmw.addHolder(color, "blue");

		myBmwRed.remove();
		myBmwRed = myBmw.addHolder(color, "red");
		myBmwRed.remove();

		assert myBmw.getHolders(color).contains(myBmwBlue);
		assert myBmw.getHolders(color).size() == 1;
	}

	public void test005_removeConcret_withHolder() {
		Generic engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");
		Generic myBmwRed = myBmw.addHolder(color, "red");

		assert color.getInstances().contains(myBmwRed);
		assert color.getInstances().size() == 1;
		myBmw.remove();

		assert car.getInstances().size() == 0;
		assert color.getInstances().size() == 0;
	}

	public void test006_removeStructural_withHolder() {
		Generic engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");
		Generic myBmwRed = myBmw.addHolder(color, "red");
		myBmw.remove();
		car.remove();

		assert !engine.getInstances().contains(car);
		assert !engine.getInstances().contains(color);
	}

	public void test007_removeConcretAndAttribut() {
		Generic engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");
		Generic myBmwRed = myBmw.addHolder(color, "red");
		myBmw.remove();
		color.remove();

		assert engine.getInstances().contains(car);
		assert !car.getInstances().contains(myBmw);
		assert !engine.getInstances().contains(color);

	}

	public void test008_removeInstanceAndAttribute() {
		Generic engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");
		Generic myBmwRed = myBmw.addHolder(color, "red");
		myBmwRed.remove();
		color.remove();

		assert engine.getInstances().contains(car);
		assert car.getInstances().contains(myBmw);
		assert !engine.getInstances().contains(color);

	}

	public void test009_removeConcret() {
		Generic engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");
		Generic myBmwRed = myBmw.addHolder(color, "red");
		myBmwRed.remove();

		assert color.getInstances().size() == 0;

	}

	public void test010_cascadeRemove() {
		Root engine = new Root();

		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);

		// Disable default referential integrity for vehicle in vehicleColor for the first target : color
		engine.getMetaRelation().disableReferentialIntegrity(ApiStatics.TARGET_POSITION);

		// Enable cascade remove for Color in vehicleColor
		engine.getMetaRelation().enableCascadeRemove(ApiStatics.TARGET_POSITION);

		// Remove the type vehicle
		vehicle.remove();
		assert !vehicle.isAlive();
		assert !vehicleColor.isAlive();
		assert !color.isAlive();
	}
}
