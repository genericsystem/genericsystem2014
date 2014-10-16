package org.genericsystem.kernel;

import java.util.Arrays;
import java.util.Collections;

import org.testng.annotations.Test;

@Test
public class AdjustMetaTest extends AbstractTest {

	public void test000_AdjustMeta() {
		Root engine = new Root();
		Vertex type1 = engine.addInstance("Type1");
		Vertex type2 = engine.addInstance(type1, "Type2");

		Vertex instance = type1.addInstance("instance");

		catchAndCheckCause(() -> type2.addInstance("instance"), IllegalStateException.class);
	}

	public void test001_AdjustMeta() {
		Root engine = new Root();
		Vertex type1 = engine.addInstance("Type1");
		Vertex type2 = engine.addInstance(type1, "Type2");
		Vertex type3 = engine.addInstance(type1, "Type3");

		Vertex instance = type2.addInstance("instance");
		assert instance.getMeta().equals(type2);
		assert instance.equals(type2.getInstance("instance"));
		catchAndCheckCause(() -> type3.addInstance("instance"), IllegalStateException.class);
	}

	public void test002_AdjustMeta() {
		Root engine = new Root();
		Vertex type1 = engine.addInstance("Type1");
		Vertex type2 = engine.addInstance(type1, "Type2");
		Vertex type3 = engine.addInstance(type2, "Type3");

		Vertex instance = type1.addInstance("instance");
		Vertex instance2 = type2.addInstance("instance2");
		catchAndCheckCause(() -> type3.addInstance("instance"), IllegalStateException.class);
	}

	public void test003_AdjustMeta() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex color = engine.addInstance("Color");
		// Vertex vehicleColor = engine.addInstance("vehicleColor", vehicle, color);
		Vertex vehicleColor = vehicle.addAttribute("VehicleColor", color);
		Vertex carColor = car.addAttribute(vehicleColor, "CarColor", color);
		assert carColor.inheritsFrom(vehicleColor);

		Vertex myBmw = car.addInstance("myBmw");
		Vertex red = color.addInstance("red");
		assert myBmw.getAttributes(Statics.BASE_POSITION).contains(carColor);
		assert !myBmw.getAttributes(Statics.TARGET_POSITION).contains(carColor);
		// assert false : myBmw.getAttributes(vehicleColor).info() + "   " + color.getAttributes(vehicleColor).info();
		// assert false : color.getAttributes().stream().filter(x -> x.inheritsFrom(vehicleColor)).collect(Collectors.toList());
		Vertex myBmwRed = myBmw.addHolder(vehicleColor, "myBmwRed", red);
		assert carColor.equals(myBmwRed.getMeta());
	}

	public void test004_AdjustMeta() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex power = vehicle.addAttribute("Power");

		Vertex myBmw = car.addInstance("myBmw");
		Vertex holder = myBmw.addHolder(power, 235);
		assert holder.getMeta().equals(power);
		Vertex power2 = car.addAttribute(power, "Power2");
		// assert !holder.isAlive();
		assert power2.equals(myBmw.getHolders(power).stream().findFirst().get().getMeta());
		// new RollbackCatcher() {
		// @Override
		// public void intercept() {
		// type3.addInstance("instance");
		// }
		// }.assertIsCausedBy(IllegalStateException.class);

	}

	public void test001_AdjustMeta_SystemMap() {
		Root engine = new Root();
		Vertex metaAttribute = engine.getMetaAttribute();
		Vertex systemMap = engine.getMap();
		assert systemMap.getMeta().equals(metaAttribute);
	}

	public void test001_AdjustMeta_MetaLevel_metaAttribut_NoComponent() {
		Root engine = new Root();
		assert engine == engine.adjustMeta("Power", Collections.emptyList());
	}

	public void test002_AdjustMeta_MetaLevel_metaAttribut_OneComponent() {
		Root engine = new Root();
		Vertex metaAttribute = engine.getMetaAttribute();
		assert metaAttribute != null;
		Vertex car = engine.addInstance("Car");
		assert metaAttribute == engine.adjustMeta("Power", Collections.singletonList(car));
	}

	public void test003_AdjustMeta_MetaLevel_metaAttribut_TwoComponents() {
		Root engine = new Root();
		Vertex metaAttribute = engine.getMetaAttribute();
		Vertex car = engine.addInstance("Car");
		Vertex color = engine.addInstance("Color");
		assert metaAttribute == engine.adjustMeta("CarColor", Arrays.asList(car, color));
	}

	public void test004_AdjustMeta_MetaLevel_metaAttribut() {
		Root engine = new Root();
		Vertex metaAttribute = engine.getMetaAttribute();
		Vertex robot = engine.addInstance("Robot");
		Vertex car = engine.addInstance("Car");
		Vertex color = engine.addInstance("Color");
		assert metaAttribute == engine.adjustMeta("CarColor", Arrays.asList(car, color));
		Vertex carColor = engine.addInstance("CarColor", car, color);
		assert metaAttribute == engine.adjustMeta("Radio", Arrays.asList(car));
	}

	public void test005_AdjustMeta_MetaLevel_metaRelation_ThreeComponents() {
		Root engine = new Root();
		Vertex metaAttribute = engine.getMetaAttribute();
		assert metaAttribute == engine.adjustMeta(engine.getValue(), Arrays.asList(engine, engine));
		Vertex metaRelation = engine.addInstance(engine.getValue(), engine, engine);
		Vertex car = engine.addInstance("Car");
		Vertex color = engine.addInstance("Color");
		Vertex carColor = engine.addInstance("CarColor", car, color);
		Vertex finition = engine.addInstance("Finition");
		assert metaRelation == engine.adjustMeta("CarColorFinition", Arrays.asList(car, color, finition));
	}

	public void test006_AdjustMeta_TypeLevel_Relation_TwoComponents() {
		Root engine = new Root();
		engine.setInstance(engine.getValue(), engine);
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex color = engine.addInstance("Color");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Vertex car = vehicle.addInstance("Car");
		Vertex red = color.addInstance("Red");
		assert vehicleColor == vehicleColor.adjustMeta("CarRed", Arrays.asList(car, red)) : engine.adjustMeta("CarRed", Arrays.asList(car, red));
	}

	public void test007_AdjustMeta_TypeLevel_Relation_TwoComponents_oneComponentSpecializedByInheritance() {
		Root engine = new Root();
		engine.setInstance(engine.getValue(), engine);
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex color = engine.addInstance("Color");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Vertex color2 = engine.addInstance(color, "Color2");
		Vertex car = vehicle.addInstance("Car");
		Vertex red = color2.addInstance("Red");
		assert vehicleColor == vehicleColor.adjustMeta("CarRed", Arrays.asList(car, red)) : engine.adjustMeta("CarRed", Arrays.asList(car, red));
	}

	public void test008_AdjustMeta_TypeLevel_Relation_TwoComponents_oneComponentSpecializedByInstanciation() {
		Root engine = new Root();
		engine.setInstance(engine.getValue(), engine);
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex color = engine.addInstance("Color");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Vertex color2 = color.addInstance("Color2");
		Vertex car = vehicle.addInstance("Car");
		Vertex red = color2.addInstance("Red");
		assert vehicleColor == vehicleColor.adjustMeta("CarRed", Arrays.asList(car, red)) : engine.adjustMeta("CarRed", Arrays.asList(car, red));
	}

	public void test009_AdjustMeta_TypeLevel_Relation_TwoComponents_TwoComponentSpecializedByInheritance() {
		Root engine = new Root();
		engine.setInstance(engine.getValue(), engine);
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex color = engine.addInstance("Color");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Vertex vehicle2 = engine.addInstance(vehicle, "Vehicle2");
		Vertex color2 = engine.addInstance(color, "Color2");
		Vertex car = vehicle2.addInstance("Car");
		Vertex red = color2.addInstance("Red");
		assert vehicleColor == vehicleColor.adjustMeta("CarRed", Arrays.asList(car, red)) : engine.adjustMeta("CarRed", Arrays.asList(car, red));
	}

	public void test010_AdjustMeta_TypeLevel_Relation_TwoComponents_TwoComponentSpecializedByInstanciation() {
		Root engine = new Root();
		engine.setInstance(engine.getValue(), engine);
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex color = engine.addInstance("Color");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Vertex vehicle2 = engine.addInstance("Vehicle2");
		Vertex color2 = color.addInstance("Color2");
		Vertex car = vehicle2.addInstance("Car");
		Vertex red = color2.addInstance("Red");
		assert vehicleColor == vehicleColor.adjustMeta("CarRed", Arrays.asList(car, red)) : engine.adjustMeta("CarRed", Arrays.asList(car, red));
	}

	public void test011_AdjustMeta_TypeLevel_Relation_TwoComponents_TwoComponentSpecialized() {
		Root engine = new Root();
		engine.setInstance(engine.getValue(), engine);
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex color = engine.addInstance("Color");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Vertex vehicle2 = engine.addInstance(vehicle, "Vehicle2");
		Vertex color2 = color.addInstance("Color2");
		Vertex car = vehicle2.addInstance("Car");
		Vertex red = color2.addInstance("Red");
		assert vehicleColor == vehicleColor.adjustMeta("CarRed", Arrays.asList(car, red)) : engine.adjustMeta("CarRed", Arrays.asList(car, red));
	}

	public void test012_AdjustMeta_TypeLevel_Relation_ThreeComponents() {
		Root engine = new Root();
		engine.setInstance(engine.getValue(), engine);
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex color = engine.addInstance("Color");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Vertex vehicle2 = engine.addInstance(vehicle, "Vehicle2");
		Vertex color2 = color.addInstance("Color2");
		Vertex finition = engine.addInstance("Finition");
		Vertex car = vehicle2.addInstance("Car");
		Vertex red = color2.addInstance("Red");

		assert vehicleColor == vehicleColor.adjustMeta("CarRed", Arrays.asList(car, red, finition)) : engine.adjustMeta("CarRed", Arrays.asList(car, red, finition));
	}

	public void test013_AdjustMeta_TypeLevel_Relation_ThreeComponents() {
		Root engine = new Root();
		engine.setInstance(engine.getValue(), engine);
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex color = engine.addInstance("Color");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);

		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex carColor = engine.addInstance(vehicleColor, "CarColor", car, color);
		Vertex color2 = color.addInstance("Color2");
		Vertex finition = engine.addInstance("Finition");
		Vertex myBmw = car.addInstance("myBmw");
		Vertex red = color2.addInstance("Red");

		assert carColor == vehicleColor.adjustMeta("CarRed", Arrays.asList(car, red, finition)) : engine.adjustMeta("CarRed", Arrays.asList(car, red, finition));
	}

	public void test020_AdjustMeta_TypeLevel_Attribute() {
		Root engine = new Root();
		Vertex power = engine.addInstance("Power", engine);
		Vertex car = engine.addInstance("Car", engine);
		Vertex carPower = engine.addInstance(power, "carPower", engine);
		assert carPower.equals(power.adjustMeta(235, Collections.singletonList(car)));
	}

	public void testAdjustMetaValue() {
		Root engine = new Root();
		Vertex vehicle = engine.addType("Vehicle");
		Vertex power = vehicle.addAttribute("Power");
		Vertex car = engine.addType(vehicle, "Car");
		Vertex myBmw = car.addInstance("myBmw");
		Vertex power235 = myBmw.addHolder(power, 235);
		assert power235.getMeta().equals(power);

		Vertex power2 = car.addAttribute(power, "Power2");
		assert !power235.isAlive();
		assert myBmw.getHolders(power).stream().findFirst().get().getMeta().equals(power2) : "meta : " + power235.getMeta();
	}
}
