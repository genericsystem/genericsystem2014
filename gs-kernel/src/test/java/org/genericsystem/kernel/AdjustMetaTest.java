package org.genericsystem.kernel;

import java.util.Arrays;
import java.util.Collections;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.api.core.exceptions.ExistsException;
import org.genericsystem.kernel.Config.SystemMap;
import org.testng.annotations.Test;

@Test
public class AdjustMetaTest extends AbstractTest {

	public void test001() {
		Root engine = new Root();
		assert engine.getMetaAttribute().equals(engine.adjustMeta(engine.getMetaAttribute().getValue(), engine));
		assert engine.getMetaRelation().equals(engine.adjustMeta(engine.getMetaRelation().getValue(), engine, engine));
		assert engine.getMetaRelation() == engine.setInstance(engine.getValue(), engine, engine);
		Generic metaTernaryRelation = engine.setInstance(engine.getValue(), engine, engine, engine);
		assert engine.getCurrentCache().getMeta(3).equals(metaTernaryRelation);
	}

	public void test002() {
		Root engine = new Root();
		assert engine == engine.adjustMeta("Color", Collections.emptyList());
	}

	public void test003() {
		Root engine = new Root();
		Generic car = engine.addInstance("Car");
		assert engine.getMetaAttribute() == engine.adjustMeta("Power", car);
	}

	public void test004() {
		Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		assert engine.getMetaRelation() == engine.adjustMeta("CarColor", car, color);
	}

	public void test005() {
		Root engine = new Root();
		assert engine.getMetaAttribute() == engine.adjustMeta(engine.getValue(), engine);
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		engine.addInstance("CarColor", car, color);
		Generic design = engine.addInstance("Design");
		Generic adjustMeta = engine.adjustMeta("CarColorDesign", Arrays.asList(car, color, design));
		assert engine.getMetaRelation() == adjustMeta : adjustMeta.info();
	}

	public void test006() {
		Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic carColor = engine.addInstance("CarColor", car, color);
		Generic myBmw = car.addInstance("myBmw");
		Generic red = color.addInstance("Red");
		assert carColor == carColor.adjustMeta("myBmwRed", Arrays.asList(myBmw, red)) : engine.adjustMeta("myBmwRed", Arrays.asList(myBmw, red));
	}

	public void test007() {
		Root engine = new Root();
		assert engine.getMetaAttribute().equalsRegardlessSupers(engine.getMetaAttribute(), engine.getMetaAttribute().getValue(), Collections.singletonList(engine));

		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic carColor = engine.addInstance("CarColor", car, color);
		Generic red = engine.addInstance(color, "red");
		Generic myBmw = car.addInstance("myBmw");
		assert carColor == carColor.adjustMeta("myBmwRed", myBmw, red);
	}

	public void test008() {
		Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic carColor = engine.addInstance("CarColor", car, color);
		Generic red = color.addInstance("red");
		Generic myBmw = car.addInstance("myBmw");
		assert carColor == carColor.adjustMeta("myBmwRed", myBmw, red);
	}

	public void test009() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic car = engine.addInstance(vehicle, "Car");
		Generic red = engine.addInstance(color, "red");
		Generic myBmw = car.addInstance("myBmw");
		assert vehicleColor == vehicleColor.adjustMeta("myBmwRed", myBmw, red);
	}

	public void test010() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic car = engine.addInstance(vehicle, "Car");
		Generic red = color.addInstance("red");
		Generic myBmw = car.addInstance("myBmw");
		assert vehicleColor == vehicleColor.adjustMeta("myBmwRed", myBmw, red);
	}

	public void test011() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);

		Generic car = engine.addInstance(vehicle, "Car");
		Generic carColor = engine.addInstance(vehicleColor, "CarColor", car, color);
		Generic red = color.addInstance("red");
		Generic design = engine.addInstance("Design");

		assert carColor == vehicleColor.adjustMeta("CarRed", car, red, design);
	}

	public void test012() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic car = engine.addInstance(vehicle, "Car");
		Generic carColor = engine.addInstance(vehicleColor, "CarColor", car, color);

		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic myBmw = car.addInstance("myBmw");
		Generic red = color.addInstance("red");
		assert carColor == vehicleColor.adjustMeta("myBmwRed", myBmw, red);
		assert vehicleColor == vehicleColor.adjustMeta("myVehicleRed", myVehicle, red);
	}

	public void test013() {
		Root engine = new Root();
		// TODO power n'est pas un attribut comme ça ? carPower n'a aucun rapport avec car ?
		Generic power = engine.addInstance("Power", engine);
		Generic car = engine.addInstance("Car", engine);
		Generic carPower = engine.addInstance(power, "carPower", engine);
		assert carPower.equals(power.adjustMeta(235, car));
	}

	/**
	 * Others tests
	 */

	public void test014() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic bike = engine.addInstance(vehicle, "Bike");

		car.addInstance("myBmw");
		bike.addInstance("myBmw");

		assert vehicle.getInstance("myBmw") == null;
	}

	public void test015() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic bike = engine.addInstance(vehicle, "Bike");

		Generic myBmw = car.addInstance("myBmw");
		assert myBmw.getMeta().equals(car);
		assert myBmw.equals(car.getInstance("myBmw"));
		bike.addInstance("myBmw");

		catchAndCheckCause(() -> bike.addInstance("myBmw"), ExistsException.class);

	}

	public void test016() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic bike = engine.addInstance(vehicle, "Bike");
		Generic vtt = engine.addInstance(bike, "VTT");

		Generic vehicleInstance = vehicle.addInstance("instance");
		Generic bikeInstance = bike.addInstance("instanceBike");
		Generic vttInstance = vtt.addInstance("instance");
		assert vehicle.getInstance("instance").equals(vehicleInstance);
		assert bike.getInstance("instanceBike").equals(bikeInstance);
		assert vtt.getInstance("instance").equals(vttInstance);
	}

	public void test017() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);
		Generic carColor = car.addRelation(vehicleColor, "CarColor", color);
		assert carColor.inheritsFrom(vehicleColor);

		Generic myBmw = car.addInstance("myBmw");
		Generic red = color.addInstance("red");
		assert myBmw.getRelations(ApiStatics.BASE_POSITION).contains(carColor);
		assert !myBmw.getRelations(ApiStatics.TARGET_POSITION).contains(carColor);
		Generic myBmwRed = myBmw.addLink(vehicleColor, "myBmwRed", red);
		assert carColor.equals(myBmwRed.getMeta());
	}

	public void test018() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic power = vehicle.addAttribute("Power");

		Generic myBmw = car.addInstance("myBmw");
		Generic holder = myBmw.addHolder(power, 235);
		assert holder.getMeta().equals(power);
		Generic carPower = car.addAttribute(power, "CarPower");
		assert !holder.isAlive();
		assert carPower.equals(myBmw.getHolders(power).first().getMeta());
	}

	public void test019() {
		Root engine = new Root();
		Generic metaAttribute = engine.getMetaAttribute();
		Generic systemMap = engine.find(SystemMap.class);
		assert systemMap.getMeta().equals(metaAttribute);
	}
}
