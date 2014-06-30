package org.genericsystem.cache;

import java.util.Arrays;
import java.util.Collections;
import org.genericsystem.kernel.Statics;
import org.testng.annotations.Test;

@Test
public class AdjustMetaTest extends AbstractTest {

	public void test001_AdjustMeta_MetaLevel_metaAttribut_NoComponent() {
		Engine engine = new Engine();
		engine.addInstance(engine.getValue(), engine);
		assert engine == engine.adjustMeta(Collections.emptyList(), "Power", Collections.emptyList());
	}

	public void test002_AdjustMeta_MetaLevel_metaAttribut_OneComponent() {
		Engine engine = new Engine();
		Generic metaAttribute = engine.addInstance(engine.getValue(), engine);
		Generic car = engine.addInstance("Car");
		assert metaAttribute.equals(engine.adjustMeta(Collections.emptyList(), "Power", Collections.singletonList(car)));
	}

	public void test003_AdjustMeta_MetaLevel_metaAttribut_TwoComponents() {
		Engine engine = new Engine();
		Generic metaAttribute = engine.addInstance(engine.getValue(), engine);
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		assert metaAttribute.equals(engine.adjustMeta(Collections.emptyList(), "CarColor", Arrays.asList(car, color)));
	}

	public void test004_AdjustMeta_MetaLevel_metaAttribut() {
		Engine engine = new Engine();
		Generic metaAttribute = engine.addInstance(engine.getValue(), engine);
		Generic robot = engine.addInstance("Robot");
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		assert metaAttribute.equals(engine.adjustMeta(Collections.emptyList(), "CarColor", Arrays.asList(car, color)));
		Generic carColor = engine.addInstance("CarColor", car, color);
		assert metaAttribute.equals(engine.adjustMeta(Collections.emptyList(), "Radio", Arrays.asList(car)));
	}

	public void test005_AdjustMeta_MetaLevel_metaRelation_ThreeComponents() {
		Engine engine = new Engine();
		Generic metaAttribute = engine.addInstance(engine.getValue(), engine);
		assert metaAttribute.equals(engine.adjustMeta(Collections.emptyList(), engine.getValue(), Arrays.asList(engine, engine)));
		Generic metaRelation = engine.addInstance(engine.getValue(), engine, engine);
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic carColor = engine.addInstance("CarColor", car, color);
		Generic finition = engine.addInstance("Finition");
		assert metaRelation.equals(engine.adjustMeta(Collections.emptyList(), "CarColorFinition", Arrays.asList(car, color, finition)));
	}

	public void test006_AdjustMeta_TypeLevel_Relation_TwoComponents() {
		Engine engine = new Engine();
		engine.addInstance(engine.getValue(), engine);
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic car = vehicle.addInstance("Car");
		Generic red = color.addInstance("Red");
		assert vehicleColor == vehicleColor.adjustMeta(Collections.emptyList(), "CarRed", Arrays.asList(car, red)) : engine.adjustMeta(Collections.emptyList(), "CarRed", Arrays.asList(car, red));
	}

	public void test007_AdjustMeta_TypeLevel_Relation_TwoComponents_oneComponentSpecializedByInheritance() {
		Engine engine = new Engine();
		engine.addInstance(engine.getValue(), engine);
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic color2 = engine.addInstance(color, "Color2");
		Generic car = vehicle.addInstance("Car");
		Generic red = color2.addInstance("Red");
		assert vehicleColor.equals(vehicleColor.adjustMeta(Collections.emptyList(), "CarRed", Arrays.asList(car, red))) : engine.adjustMeta(Collections.emptyList(), "CarRed", Arrays.asList(car, red));
	}

	public void test008_AdjustMeta_TypeLevel_Relation_TwoComponents_oneComponentSpecializedByInstanciation() {
		Engine engine = new Engine();
		engine.addInstance(engine.getValue(), engine);
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic color2 = color.addInstance("Color2");
		Generic car = vehicle.addInstance("Car");
		Generic red = color2.addInstance("Red");
		assert vehicleColor.equals(vehicleColor.adjustMeta(Collections.emptyList(), "CarRed", Arrays.asList(car, red))) : engine.adjustMeta(Collections.emptyList(), "CarRed", Arrays.asList(car, red));
	}

	public void test009_AdjustMeta_TypeLevel_Relation_TwoComponents_TwoComponentSpecializedByInheritance() {
		Engine engine = new Engine();
		engine.addInstance(engine.getValue(), engine);
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic vehicle2 = engine.addInstance(vehicle, "Vehicle2");
		Generic color2 = engine.addInstance(color, "Color2");
		Generic car = vehicle2.addInstance("Car");
		Generic red = color2.addInstance("Red");
		assert vehicleColor.equals(vehicleColor.adjustMeta(Collections.emptyList(), "CarRed", Arrays.asList(car, red))) : engine.adjustMeta(Collections.emptyList(), "CarRed", Arrays.asList(car, red));
	}

	public void test010_AdjustMeta_TypeLevel_Relation_TwoComponents_TwoComponentSpecializedByInstanciation() {
		Engine engine = new Engine();
		engine.addInstance(engine.getValue(), engine);
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic vehicle2 = engine.addInstance("Vehicle2");
		Generic color2 = color.addInstance("Color2");
		Generic car = vehicle2.addInstance("Car");
		Generic red = color2.addInstance("Red");
		assert vehicleColor.equals(vehicleColor.adjustMeta(Collections.emptyList(), "CarRed", Arrays.asList(car, red))) : engine.adjustMeta(Collections.emptyList(), "CarRed", Arrays.asList(car, red));
	}

	public void test011_AdjustMeta_TypeLevel_Relation_TwoComponents_TwoComponentSpecialized() {
		Engine engine = new Engine();
		engine.addInstance(engine.getValue(), engine);
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic vehicle2 = engine.addInstance(vehicle, "Vehicle2");
		Generic color2 = color.addInstance("Color2");
		Generic car = vehicle2.addInstance("Car");
		Generic red = color2.addInstance("Red");
		assert vehicleColor.equals(vehicleColor.adjustMeta(Collections.emptyList(), "CarRed", Arrays.asList(car, red))) : engine.adjustMeta(Collections.emptyList(), "CarRed", Arrays.asList(car, red));
	}

	public void test012_AdjustMeta_TypeLevel_Relation_ThreeComponents() {
		Engine engine = new Engine();
		engine.addInstance(engine.getValue(), engine);
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic vehicle2 = engine.addInstance(vehicle, "Vehicle2");
		Generic color2 = color.addInstance("Color2");
		Generic finition = engine.addInstance("Finition");
		Generic car = vehicle2.addInstance("Car");
		Generic red = color2.addInstance("Red");

		assert vehicleColor.equals(vehicleColor.adjustMeta(Collections.emptyList(), "CarRed", Arrays.asList(car, red, finition))) : engine.adjustMeta(Collections.emptyList(), "CarRed", Arrays.asList(car, red, finition));
	}

	public void test013_AdjustMeta_TypeLevel_Relation_ThreeComponents() {
		Engine engine = new Engine();
		engine.addInstance(engine.getValue(), engine);
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic vehicle2 = engine.addInstance(vehicle, "Vehicle2");
		Generic vehicleColor2 = engine.addInstance(vehicleColor, "VehicleColor2", vehicle2, color);
		Generic color2 = color.addInstance("Color2");
		Generic finition = engine.addInstance("Finition");
		Generic car = vehicle2.addInstance("Car");
		Generic red = color2.addInstance("Red");

		assert vehicleColor2.equals(vehicleColor.adjustMeta(Collections.emptyList(), "CarRed", Arrays.asList(car, red, finition))) : engine.adjustMeta(Collections.emptyList(), "CarRed", Arrays.asList(car, red, finition));
	}

	public void test014_AdjustMeta_TypeLevel_Relation_ThreeComponents() {
		Engine engine = new Engine();
		engine.addInstance(engine.getValue(), engine);
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic vehicle2 = engine.addInstance(vehicle, "Vehicle2");
		Generic color2 = color.addInstance("Color2");
		Generic vehicleColor2 = engine.addInstance(vehicleColor, "VehicleColor2", vehicle2, color2);
		Generic finition = engine.addInstance("Finition");
		Generic car = vehicle2.addInstance("Car");
		Generic red = color2.addInstance("Red");
		assert vehicleColor2.equals(vehicleColor.adjustMeta(Collections.emptyList(), "CarRed", Arrays.asList(car, red, finition))) : engine.adjustMeta(Collections.emptyList(), "CarRed", Arrays.asList(car, red, finition));
	}

	public void test015_AdjustMeta_TypeLevel_Relation_ThreeComponents() {
		Engine engine = new Engine();
		engine.addInstance(engine.getValue(), engine);
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic vehicle2 = engine.addInstance(vehicle, "Vehicle2");
		Generic color2 = color.addInstance("Color2");
		Generic finition = engine.addInstance("Finition");
		Generic car = vehicle2.addInstance("Car");
		Generic vehicleColor2 = engine.addInstance(vehicleColor, "VehicleColor2", car, color2);
		Generic red = color2.addInstance("Red");
		assert vehicleColor2.equals(vehicleColor.adjustMeta(Collections.emptyList(), "CarRed", Arrays.asList(car, red, finition))) : engine.adjustMeta(Collections.emptyList(), "CarRed", Arrays.asList(car, red, finition));
	}

	public void test016_AdjustMeta_TypeLevel_Relation_ThreeComponents() {
		Engine engine = new Engine();
		engine.addInstance(engine.getValue(), engine);
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic vehicle2 = engine.addInstance(vehicle, "Vehicle2");
		Generic vehicleColor2 = engine.addInstance(vehicleColor, "VehicleColor2", vehicle2, color);
		Generic color2 = color.addInstance("Color2");
		Generic finition = engine.addInstance("Finition");
		Generic car = vehicle2.addInstance("Car");
		Generic vehicleColor3 = engine.addInstance(vehicleColor2, "VehicleColor3", car, color2);
		Generic red = color2.addInstance("Red");
		assert vehicleColor3.equals(vehicleColor.adjustMeta(Collections.emptyList(), "CarRed", Arrays.asList(car, red, finition))) : engine.adjustMeta(Collections.emptyList(), "CarRed", Arrays.asList(car, red, finition));
	}

	public void test017_AdjustMeta_TypeLevel_Relation_ThreeComponents() {
		Engine engine = new Engine();
		engine.addInstance(engine.getValue(), engine);
		Generic vehicle = engine.addInstance("Vehicle");
		Generic vehicle2 = engine.addInstance(vehicle, "Vehicle2");
		Generic vehicleVehicle2 = engine.addInstance("VehicleVehicle2", vehicle, vehicle2);
		Generic power = engine.addInstance("Power", vehicle);
		Generic intensity = engine.addInstance(power, "Intensity", vehicle2);
		Generic unit = engine.addInstance("Unit", power);
		Generic intensityUnit = engine.addInstance(unit, "Unit", intensity);
		Generic car = vehicle.addInstance("Car");
		Generic bus = vehicle2.addInstance("Bus");
		power.addInstance(100, car);
		intensity.addInstance(110, bus);
		unit.addInstance("Watt", power);
		intensityUnit.addInstance("KWatt", intensity);
		Generic vehicleBus = engine.addInstance(vehicleVehicle2, "VehicleBus", vehicle, bus);
		assert vehicleBus.equals(vehicleVehicle2.adjustMeta(Collections.emptyList(), "carBus", Arrays.asList(car, bus)));
	}

	public void test018_AdjustMeta_TypeLevel_Relation() {
		Engine engine = new Engine();
		engine.addInstance(engine.getValue(), engine);
		Generic vehicle = engine.addInstance("Vehicle");
		Generic vehicle2 = engine.addInstance(vehicle, "Vehicle2");
		Generic vehicleVehicle2 = engine.addInstance("VehicleVehicle2", vehicle, vehicle2);
		Generic power = engine.addInstance("Power", vehicle);
		Generic intensity = engine.addInstance(power, "Intensity", vehicle2);
		Generic unit = engine.addInstance("Unit", power);
		Generic intensityUnit = engine.addInstance(unit, "Unit", intensity);
		Generic car = vehicle.addInstance("Car");
		Generic bus = vehicle2.addInstance("Bus");
		power.addInstance(100, car);
		Generic v110 = intensity.addInstance(110, bus);
		Generic watt = unit.addInstance("Watt", power);
		Generic kWatt = intensityUnit.addInstance(watt, "KWatt", intensity);

		Generic vehicleVehicle2IntensityUnitWatt = engine.addInstance(vehicleVehicle2, "VehicleVehicle2IntensityUnitWatt", vehicle, vehicle2, intensity, unit, watt);
		assert vehicleVehicle2IntensityUnitWatt.equals(vehicleVehicle2.adjustMeta(Collections.emptyList(), "carBus", Arrays.asList(car, bus, v110, intensityUnit, kWatt)));
	}

	public void test020_AdjustMeta_TypeLevel_Attribute() {
		Engine engine = new Engine();
		Statics.debugCurrentThread();
		Generic power = engine.addInstance("Power");
		// Generic car = engine.addInstance("Car", engine);
		assert power.isAlive();
		// assert car.isAlive();
		Generic carPower = engine.addInstance(power, "carPower", engine);
		// assert carPower.equals(power.adjustMeta(Collections.emptyList(), 235, Collections.singletonList(car)));
	}
}
