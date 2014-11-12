package org.genericsystem.kernel;

import java.util.Arrays;
import java.util.Collections;

import org.genericsystem.api.exception.ExistsException;
import org.testng.annotations.Test;

@Test
public class AdjustMetaTest extends AbstractTest {

	public void test000_AdjustMeta() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex bike = engine.addInstance(vehicle, "Bike");

		Vertex myCarBmw = car.addInstance("myBmw");
		Vertex myBikeBmw = bike.addInstance("myBmw");

		assert vehicle.getInstance("myBmw") == null;
	}

	public void test001_AdjustMeta() {
		Root engine = new Root();
		Vertex type1 = engine.addInstance("Type1");
		Vertex type2 = engine.addInstance(type1, "Type2");
		Vertex type3 = engine.addInstance(type1, "Type3");

		Vertex instance = type2.addInstance("instance");
		assert instance.getMeta().equals(type2);
		assert instance.equals(type2.getInstance("instance"));
		type3.addInstance("instance");

		catchAndCheckCause(() -> type3.addInstance("instance"), ExistsException.class);

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
		assert power2.equals(myBmw.getHolders(power).get().findFirst().get().getMeta());
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

	public void test001_AdjustMeta_MetaLevel_metaAttribut_NoComposite() {
		Root engine = new Root();
		assert engine == engine.adjustMeta("Power", Collections.emptyList());
	}

	public void test002_AdjustMeta_MetaLevel_metaAttribut_OneComposite() {
		Root engine = new Root();
		Vertex metaAttribute = engine.getMetaAttribute();
		assert metaAttribute != null;
		Vertex car = engine.addInstance("Car");
		assert metaAttribute == engine.adjustMeta("Power", Collections.singletonList(car));
	}

	public void test003_AdjustMeta_MetaLevel_metaAttribut_TwoComposites() {
		Root engine = new Root();
		Vertex metaRelation = engine.getMetaRelation();
		Vertex car = engine.addInstance("Car");
		Vertex color = engine.addInstance("Color");
		assert metaRelation == engine.adjustMeta("CarColor", Arrays.asList(car, color));
	}

	public void test004_AdjustMeta_MetaLevel_metaAttribut() {
		Root engine = new Root();
		Vertex robot = engine.addInstance("Robot");
		Vertex car = engine.addInstance("Car");
		Vertex color = engine.addInstance("Color");
		assert engine.getMetaRelation() == engine.adjustMeta("CarColor", Arrays.asList(car, color));
		Vertex carColor = engine.addInstance("CarColor", car, color);
		assert engine.getMetaAttribute() == engine.adjustMeta("Radio", Arrays.asList(car));
	}

	public void test005_AdjustMeta_MetaLevel_metaRelation_ThreeComposites() {
		Root engine = new Root();
		Vertex metaAttribute = engine.getMetaAttribute();
		assert metaAttribute == engine.adjustMeta(engine.getValue(), Arrays.asList(engine));
		Vertex metaRelation = engine.getMetaRelation();
		Vertex car = engine.addInstance("Car");
		Vertex color = engine.addInstance("Color");
		Vertex carColor = engine.addInstance("CarColor", car, color);
		Vertex finition = engine.addInstance("Finition");
		assert metaRelation == engine.adjustMeta("CarColorFinition", Arrays.asList(car, color, finition));
	}

	public void test006_AdjustMeta_TypeLevel_Relation_TwoComposites() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex color = engine.addInstance("Color");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Vertex car = vehicle.addInstance("Car");
		Vertex red = color.addInstance("Red");
		assert vehicleColor == vehicleColor.adjustMeta("CarRed", Arrays.asList(car, red)) : engine.adjustMeta("CarRed", Arrays.asList(car, red));
	}

	public void test007_AdjustMeta_TypeLevel_Relation_TwoComposites_oneCompositeSpecializedByInheritance() {
		Root engine = new Root();
		assert engine.getMetaAttribute().equalsRegardlessSupers(engine.getMetaAttribute(), engine.getValue(), Collections.singletonList(engine));
		engine.setInstance(engine.getValue(), engine);

		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex color = engine.addInstance("Color");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Vertex red = engine.addInstance(color, "red");
		Vertex car = vehicle.addInstance("Car");
		assert vehicleColor == vehicleColor.adjustMeta("CarRed", Arrays.asList(car, red)) : engine.adjustMeta("CarRed", Arrays.asList(car, red));
	}

	public void test008_AdjustMeta_TypeLevel_Relation_TwoComposites_oneCompositeSpecializedByInstanciation() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex color = engine.addInstance("Color");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Vertex red = color.addInstance("red");
		Vertex car = vehicle.addInstance("Car");
		assert vehicleColor == vehicleColor.adjustMeta("CarRed", Arrays.asList(car, red)) : engine.adjustMeta("CarRed", Arrays.asList(car, red));
	}

	public void test009_AdjustMeta_TypeLevel_Relation_TwoComposites_TwoCompositeSpecializedByInheritance() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex color = engine.addInstance("Color");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Vertex vehicle2 = engine.addInstance(vehicle, "Vehicle2");
		Vertex red = engine.addInstance(color, "red");
		Vertex car = vehicle2.addInstance("Car");
		assert vehicleColor == vehicleColor.adjustMeta("CarRed", Arrays.asList(car, red)) : engine.adjustMeta("CarRed", Arrays.asList(car, red));
	}

	public void testMeta() {
		Root engine = new Root();
		assert engine.getMetaAttribute().equals(engine.adjustMeta(engine.getValue(), Arrays.asList(engine)));
		assert engine.getMetaRelation().equals(engine.adjustMeta(engine.getValue(), Arrays.asList(engine, engine)));
		assert engine.getMetaRelation() == engine.setInstance(engine.getValue(), engine, engine);
		Vertex metaTernaryRelation = engine.setInstance(engine.getValue(), engine, engine, engine);
		assert engine.getMeta(3).equals(metaTernaryRelation);
	}

	public void test010_AdjustMeta_TypeLevel_Relation_TwoComposites_TwoCompositeSpecializedByInstanciation() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex color = engine.addInstance("Color");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Vertex vehicle2 = engine.addInstance("Vehicle2");
		Vertex red = color.addInstance("red");
		Vertex car = vehicle2.addInstance("Car");
		assert vehicleColor == vehicleColor.adjustMeta("CarRed", Arrays.asList(car, red)) : engine.adjustMeta("CarRed", Arrays.asList(car, red));
	}

	public void test011_AdjustMeta_TypeLevel_Relation_TwoComposites_TwoCompositeSpecialized() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex color = engine.addInstance("Color");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Vertex vehicle2 = engine.addInstance(vehicle, "Vehicle2");
		Vertex red = color.addInstance("red");
		Vertex car = vehicle2.addInstance("Car");
		assert vehicleColor == vehicleColor.adjustMeta("CarRed", Arrays.asList(car, red)) : engine.adjustMeta("CarRed", Arrays.asList(car, red));
	}

	public void test012_AdjustMeta_TypeLevel_Relation_ThreeComposites() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex color = engine.addInstance("Color");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Vertex vehicle2 = engine.addInstance(vehicle, "Vehicle2");
		Vertex red = color.addInstance("red");
		Vertex finition = engine.addInstance("Finition");
		Vertex car = vehicle2.addInstance("Car");

		assert vehicleColor == vehicleColor.adjustMeta("CarRed", Arrays.asList(car, red, finition)) : engine.adjustMeta("CarRed", Arrays.asList(car, red, finition));
	}

	public void test013_AdjustMeta_TypeLevel_Relation_ThreeComposites() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex color = engine.addInstance("Color");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);

		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex carColor = engine.addInstance(vehicleColor, "CarColor", car, color);
		Vertex red = color.addInstance("red");
		Vertex finition = engine.addInstance("Finition");
		Vertex myBmw = car.addInstance("myBmw");

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
		assert myBmw.getHolders(power).get().findFirst().get().getMeta().equals(power2) : "meta : " + power235.getMeta();
	}
}
