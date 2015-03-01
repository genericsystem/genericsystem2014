package org.genericsystem.kernel;

import java.util.Arrays;
import java.util.Collections;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.api.exception.ExistsException;
import org.genericsystem.kernel.Config.SystemMap;
import org.testng.annotations.Test;

@Test
public class AdjustMetaTest extends AbstractTest {

	public void test000_AdjustMeta() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic bike = engine.addInstance(vehicle, "Bike");

		car.addInstance("myBmw");
		bike.addInstance("myBmw");

		assert vehicle.getInstance("myBmw") == null;
	}

	public void test001_AdjustMeta() {
		Root engine = new Root();
		Generic type1 = engine.addInstance("Type1");
		Generic type2 = engine.addInstance(type1, "Type2");
		Generic type3 = engine.addInstance(type1, "Type3");

		Generic instance = type2.addInstance("instance");
		assert instance.getMeta().equals(type2);
		assert instance.equals(type2.getInstance("instance"));
		type3.addInstance("instance");

		catchAndCheckCause(() -> type3.addInstance("instance"), ExistsException.class);

	}

	public void test002_AdjustMeta() {
		Root engine = new Root();
		Generic type1 = engine.addInstance("Type1");
		Generic type2 = engine.addInstance(type1, "Type2");
		Generic type3 = engine.addInstance(type2, "Type3");

		Generic instance1 = type1.addInstance("instance");
		Generic instance2 = type2.addInstance("instance2");
		Generic instance3 = type3.addInstance("instance");
		assert type1.getInstance("instance").equals(instance1);
		assert type2.getInstance("instance2").equals(instance2);
		assert type3.getInstance("instance").equals(instance3);
		// catchAndCheckCause(() -> type3.addInstance("instance"), CollisionException.class);
	}

	public void test003_AdjustMeta() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic color = engine.addInstance("Color");
		// Vertex vehicleColor = engine.addInstance("vehicleColor", vehicle, color);
		Generic vehicleColor = vehicle.addAttribute("VehicleColor", color);
		Generic carColor = car.addAttribute(vehicleColor, "CarColor", color);
		assert carColor.inheritsFrom(vehicleColor);

		Generic myBmw = car.addInstance("myBmw");
		Generic red = color.addInstance("red");
		assert myBmw.getAttributes(ApiStatics.BASE_POSITION).contains(carColor);
		assert !myBmw.getAttributes(ApiStatics.TARGET_POSITION).contains(carColor);
		// assert false : myBmw.getAttributes(vehicleColor).info() + "   " + color.getAttributes(vehicleColor).info();
		// assert false : color.getAttributes().stream().filter(x -> x.inheritsFrom(vehicleColor)).collect(Collectors.toList());
		Generic myBmwRed = myBmw.addHolder(vehicleColor, "myBmwRed", red);
		assert carColor.equals(myBmwRed.getMeta());
	}

	public void test004_AdjustMeta() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic power = vehicle.addAttribute("Power");

		Generic myBmw = car.addInstance("myBmw");
		Generic holder = myBmw.addHolder(power, 235);
		assert holder.getMeta().equals(power);
		Generic carPower = car.addAttribute(power, "CarPower");
		assert carPower.equals(myBmw.getHolders(power).first().getMeta());
	}

	public void test001_AdjustMeta_SystemMap() {
		Root engine = new Root();
		Generic metaAttribute = engine.getMetaAttribute();
		Generic systemMap = engine.find(SystemMap.class);
		assert systemMap.getMeta().equals(metaAttribute);
	}

	public void test001_AdjustMeta_MetaLevel_metaAttribut_NoComposite() {
		Root engine = new Root();
		assert engine == engine.adjustMeta("Power", Collections.emptyList());
	}

	public void test002_AdjustMeta_MetaLevel_metaAttribut_OneComposite() {
		Root engine = new Root();
		Generic metaAttribute = engine.getMetaAttribute();
		assert metaAttribute != null;
		Generic car = engine.addInstance("Car");
		assert metaAttribute == engine.adjustMeta("Power", car);
	}

	public void test003_AdjustMeta_MetaLevel_metaAttribut_TwoComposites() {
		Root engine = new Root();
		Generic metaRelation = engine.getMetaRelation();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		assert metaRelation == engine.adjustMeta("CarColor", car, color);
	}

	public void test004_AdjustMeta_MetaLevel_metaAttribut() {
		Root engine = new Root();
		engine.addInstance("Robot");
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		assert engine.getMetaRelation() == engine.adjustMeta("CarColor", car, color);
		engine.addInstance("CarColor", car, color);
		assert engine.getMetaAttribute() == engine.adjustMeta("Radio", car);
	}

	public void test005_AdjustMeta_MetaLevel_metaRelation_ThreeComposites() {
		Root engine = new Root();
		Generic metaAttribute = engine.getMetaAttribute();
		assert metaAttribute == engine.adjustMeta(engine.getValue(), engine);
		Generic metaRelation = engine.getMetaRelation();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		engine.addInstance("CarColor", car, color);
		Generic finition = engine.addInstance("Finition");
		Generic adjustMeta = engine.adjustMeta("CarColorFinition", Arrays.asList(car, color, finition));
		assert metaRelation == adjustMeta : adjustMeta.info();
	}

	public void test006_AdjustMeta_TypeLevel_Relation_TwoComposites() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic car = vehicle.addInstance("Car");
		Generic red = color.addInstance("Red");
		assert vehicleColor == vehicleColor.adjustMeta("CarRed", Arrays.asList(car, red)) : engine.adjustMeta("CarRed", Arrays.asList(car, red));
	}

	public void test007_AdjustMeta_TypeLevel_Relation_TwoComposites_oneCompositeSpecializedByInheritance() {
		Root engine = new Root();
		assert engine.getMetaAttribute().equalsRegardlessSupers(engine.getMetaAttribute(), engine.getValue(), Collections.singletonList(engine));
		engine.setInstance(engine.getValue(), engine);

		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic red = engine.addInstance(color, "red");
		Generic car = vehicle.addInstance("Car");
		assert vehicleColor == vehicleColor.adjustMeta("CarRed", car, red);
	}

	public void test008_AdjustMeta_TypeLevel_Relation_TwoComposites_oneCompositeSpecializedByInstanciation() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic red = color.addInstance("red");
		Generic car = vehicle.addInstance("Car");
		assert vehicleColor == vehicleColor.adjustMeta("CarRed", car, red);
	}

	public void test009_AdjustMeta_TypeLevel_Relation_TwoComposites_TwoCompositeSpecializedByInheritance() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic vehicle2 = engine.addInstance(vehicle, "Vehicle2");
		Generic red = engine.addInstance(color, "red");
		Generic car = vehicle2.addInstance("Car");
		assert vehicleColor == vehicleColor.adjustMeta("CarRed", car, red);
	}

	public void testMeta() {
		Root engine = new Root();
		assert engine.getMetaAttribute().equals(engine.adjustMeta(engine.getValue(), engine));
		assert engine.getMetaRelation().equals(engine.adjustMeta(engine.getValue(), engine, engine));
		assert engine.getMetaRelation() == engine.setInstance(engine.getValue(), engine, engine);
		Generic metaTernaryRelation = engine.setInstance(engine.getValue(), engine, engine, engine);
		assert engine.getCurrentCache().getMeta(3).equals(metaTernaryRelation);
	}

	public void test010_AdjustMeta_TypeLevel_Relation_TwoComposites_TwoCompositeSpecializedByInstanciation() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic vehicle2 = engine.addInstance("Vehicle2");
		Generic red = color.addInstance("red");
		Generic car = vehicle2.addInstance("Car");
		assert vehicleColor == vehicleColor.adjustMeta("CarRed", car, red) : engine.adjustMeta("CarRed", car, red);
	}

	public void test011_AdjustMeta_TypeLevel_Relation_TwoComposites_TwoCompositeSpecialized() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic vehicle2 = engine.addInstance(vehicle, "Vehicle2");
		Generic red = color.addInstance("red");
		Generic car = vehicle2.addInstance("Car");
		assert vehicleColor == vehicleColor.adjustMeta("CarRed", car, red);
	}

	public void test012_AdjustMeta_TypeLevel_Relation_ThreeComposites() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic vehicle2 = engine.addInstance(vehicle, "Vehicle2");
		Generic red = color.addInstance("red");
		Generic finition = engine.addInstance("Finition");
		Generic car = vehicle2.addInstance("Car");

		assert vehicleColor == vehicleColor.adjustMeta("CarRed", car, red, finition);
	}

	public void test013_AdjustMeta_TypeLevel_Relation_ThreeComposites() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);

		Generic car = engine.addInstance(vehicle, "Car");
		Generic carColor = engine.addInstance(vehicleColor, "CarColor", car, color);
		Generic red = color.addInstance("red");
		Generic finition = engine.addInstance("Finition");
		car.addInstance("myBmw");

		assert carColor == vehicleColor.adjustMeta("CarRed", car, red, finition);
	}

	public void test020_AdjustMeta_TypeLevel_Attribute() {
		Root engine = new Root();
		Generic power = engine.addInstance("Power", engine);
		Generic car = engine.addInstance("Car", engine);
		Generic carPower = engine.addInstance(power, "carPower", engine);
		assert carPower.equals(power.adjustMeta(235, car));
	}

	public void testAdjustMetaValue() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic power = vehicle.addAttribute("Power");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic myBmw = car.addInstance("myBmw");
		Generic power235 = myBmw.addHolder(power, 235);
		assert power235.getMeta().equals(power);

		Generic power2 = car.addAttribute(power, "Power2");
		assert !power235.isAlive();
		assert myBmw.getHolders(power).get().findFirst().get().getMeta().equals(power2) : "meta : " + power235.getMeta();
	}
}
