package org.genericsystem.concurrency;

import java.util.Arrays;

import org.testng.annotations.Test;

@Test
public class AttributesTest extends AbstractTest {

	public void test001_getInstance() {
		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		assert vehicle.getLevel() == 1 : vehicle.getLevel();
		Generic power = Engine.addInstance("Power", vehicle);
		assert power.isThrowExistException();
		assert Engine.getInstance("Power", vehicle) == power;
		// assert Engine.selectInstances("Power").count() == 1;
		// assert Engine.selectInstances("Power").anyMatch(x -> x.equals(power));
		assert power.getComposites().size() == 1;
		assert vehicle.equals(power.getComposites().get(0));
		assert power.isAlive();
	}

	public void test002_getInstance() {
		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		assert vehicle.getLevel() == 1 : vehicle.getLevel();
		Generic powerType = Engine.addInstance("Power");
		Generic power = Engine.addInstance("Power", vehicle);
		assert Engine.getInstance("Power", vehicle) == power;
		// assert Engine.selectInstances("Power").count() == 2;
		// assert Engine.selectInstances("Power").anyMatch(x -> x.equals(powerType));
		// assert Engine.selectInstances("Power").anyMatch(x -> x.equals(power));
		assert power.getComposites().size() == 1;
		assert vehicle.equals(power.getComposites().get(0));
		assert power.isAlive();
	}

	// public void test003_isDependencyOf_ByComposite() {
	// Engine Engine = new Engine();
	// Generic vehicle = Engine.addInstance("Vehicle");
	// Generic car = Engine.addInstance(vehicle, "Car");
	// Generic carPower = Engine.addInstance("Power", car);
	// Generic carPowerUnit = Engine.addInstance("Unit", carPower);
	// assert carPower.dependsFrom(Engine, "Power", Collections.singletonList(vehicle));
	// assert carPowerUnit.dependsFrom(Engine, "Power", Collections.singletonList(vehicle));
	// assert !carPowerUnit.inheritsFrom(Engine, "Power", Collections.singletonList(vehicle));
	// Generic vehiclePower = Engine.addInstance("Power", vehicle);
	// assert Engine.getInstance("Power", car).getSupersStream().anyMatch(x -> x.equals(vehiclePower));
	// }

	// public void test003_isDependencyOf_ByMeta() {
	// Engine Engine = new Engine();
	// Generic vehicle = Engine.addInstance("Vehicle");
	// Generic power = Engine.addInstance("Power", vehicle);
	// Generic myVehicle = vehicle.addInstance("myVehicle");
	// Generic p123 = power.addInstance("123", myVehicle);
	// Generic myVehicle123 = power.addInstance("myVehicle123", myVehicle, p123);
	// assert myVehicle123.dependsFrom(Engine, "Power", Collections.singletonList(vehicle));
	// assert !myVehicle123.inheritsFrom(Engine, "Power", Collections.singletonList(vehicle));
	// }

	public void test1AttributWith2LevelsInheritance1AttributOnParent() {
		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic power = Engine.addInstance("Power", vehicle);
		Generic car = Engine.addInstance(vehicle, "Car");
		// assert vehicle.getAttributes(Engine).size() == 1 : vehicle.getAttributes(Engine);
		assert vehicle.getAttributes(Engine).contains(power);
		// assert car.getAttributes(Engine).size() == 1;
		assert car.getAttributes(Engine).contains(power);
	}

	public void test1AttributWith2LevelsInheritance1AttributOnFistChild() {
		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic car = Engine.addInstance(vehicle, "Car");
		Generic power = Engine.addInstance("Power", car);
		assert Engine.getLevel() == 0;
		assert vehicle.getLevel() == 1;
		assert car.getLevel() == 1;
		assert power.getLevel() == 1;
		// assert vehicle.getAttributes(Engine).size() == 0;
		// assert car.getAttributes(Engine).size() == 1;
		assert car.getAttributes(Engine).contains(power);
	}

	public void test1AttributWith3LevelsInheritance1AttributOnParent() {
		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic power = Engine.addInstance("Power", vehicle);
		Generic car = Engine.addInstance(vehicle, "Car");
		Generic microcar = Engine.addInstance(car, "Microcar");
		// assert vehicle.getAttributes(Engine).size() == 1;
		assert vehicle.getAttributes(Engine).contains(power);
		// assert car.getAttributes(Engine).size() == 1;
		assert car.getAttributes(Engine).contains(power);
		// assert microcar.getAttributes(Engine).size() == 1;
		assert microcar.getAttributes(Engine).contains(power);
	}

	public void test1AttributWith3LevelsInheritance1AttributOnFirstChild() {
		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic car = Engine.addInstance(vehicle, "Car");
		Generic power = Engine.addInstance("Power", car);
		Generic microcar = Engine.addInstance(car, "Microcar");
		// assert vehicle.getAttributes(Engine).size() == 0;
		// assert car.getAttributes(Engine).size() == 1;
		assert car.getAttributes(Engine).contains(power);
		// assert microcar.getAttributes(Engine).size() == 1;
		assert microcar.getAttributes(Engine).contains(power);
	}

	public void test1AttributWith3LevelsInheritance1AttributOnSecondChild() {
		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic car = Engine.addInstance(vehicle, "Car");
		Generic microcar = Engine.addInstance(car, "Microcar");
		Generic power = Engine.addInstance("Power", microcar);
		// assert vehicle.getAttributes(Engine).size() == 0;
		// assert car.getAttributes(Engine).size() == 0;
		// assert microcar.getAttributes(Engine).size() == 1;
		assert microcar.getAttributes(Engine).contains(power);
	}

	public void test2Attributs() {
		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic power = Engine.addInstance("Power", vehicle);
		Generic airconditioner = Engine.addInstance("AirConditioner", vehicle);
		// assert vehicle.getAttributes(Engine).size() == 2;
		assert vehicle.getAttributes(Engine).contains(power);
		assert vehicle.getAttributes(Engine).contains(airconditioner);
		assert power.isAlive();
		assert airconditioner.isAlive();
	}

	public void test2AttributsWith2LevelsInheritance2AttributsOnParent() {

		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic power = Engine.addInstance("Power", vehicle);
		Generic airconditioner = Engine.addInstance("AirConditioner", vehicle);
		Generic car = Engine.addInstance(vehicle, "Car");
		// assert vehicle.getAttributes(Engine).size() == 2;
		assert vehicle.getAttributes(Engine).contains(power);
		assert vehicle.getAttributes(Engine).contains(airconditioner);
		// assert car.getAttributes(Engine).size() == 2;
		assert car.getAttributes(Engine).contains(power);
		assert car.getAttributes(Engine).contains(airconditioner);
	}

	public void test2AttributsWith2LevelsInheritance2AttributsOnFistChild() {

		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic car = Engine.addInstance(vehicle, "Car");
		Generic power = Engine.addInstance("Power", car);
		Generic airconditioner = Engine.addInstance("AirConditioner", car);
		// assert vehicle.getAttributes(Engine).size() == 0;
		// assert car.getAttributes(Engine).size() == 2;
		assert car.getAttributes(Engine).contains(power);
		assert car.getAttributes(Engine).contains(airconditioner);
	}

	public void test2AttributsWith2LevelsInheritance1AttributOnParentAnd1AttributOnFistChild() {
		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic power = Engine.addInstance("Power", vehicle);
		Generic car = Engine.addInstance(vehicle, "Car");
		Generic airconditioner = Engine.addInstance("AirConditioner", car);
		assert airconditioner.isThrowExistException();
		// assert vehicle.getAttributes(Engine).size() == 1;
		assert vehicle.getAttributes(Engine).contains(power);
		// assert car.getAttributes(Engine).size() == 2;
		assert car.getAttributes(Engine).contains(power);
		assert car.getAttributes(Engine).contains(airconditioner);
	}

	public void test1AttributWith3LevelsInheritance2AttributOnParent() {
		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic power = Engine.addInstance("Power", vehicle);
		Generic airconditioner = Engine.addInstance("AirConditioner", vehicle);
		Generic car = Engine.addInstance(vehicle, "Car");
		Generic microcar = Engine.addInstance(car, "Microcar");
		// assert vehicle.getAttributes(Engine).size() == 2;
		assert vehicle.getAttributes(Engine).contains(power);
		assert vehicle.getAttributes(Engine).contains(airconditioner);
		// assert car.getAttributes(Engine).size() == 2;
		assert car.getAttributes(Engine).contains(power);
		assert car.getAttributes(Engine).contains(airconditioner);
		// assert microcar.getAttributes(Engine).size() == 2;
		assert microcar.getAttributes(Engine).contains(power);
		assert microcar.getAttributes(Engine).contains(airconditioner);
	}

	public void test1AttributWith3LevelsInheritance2AttributFirstChild() {
		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic car = Engine.addInstance(vehicle, "Car");
		Generic power = Engine.addInstance("Power", car);
		Generic airconditioner = Engine.addInstance("AirConditioner", car);
		Generic microcar = Engine.addInstance(car, "Microcar");
		// assert vehicle.getAttributes(Engine).size() == 0;
		// assert car.getAttributes(Engine).size() == 2;
		assert car.getAttributes(Engine).contains(power);
		assert car.getAttributes(Engine).contains(airconditioner);
		// assert microcar.getAttributes(Engine).size() == 2;
		assert microcar.getAttributes(Engine).contains(power);
		assert microcar.getAttributes(Engine).contains(airconditioner);
	}

	public void test1AttributWith3LevelsInheritance2AttributOnSecondChild() {
		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic car = Engine.addInstance(vehicle, "Car");
		Generic microcar = Engine.addInstance(car, "Microcar");
		Generic power = Engine.addInstance("Power", microcar);
		Generic airconditioner = Engine.addInstance("AirConditioner", microcar);
		// assert vehicle.getAttributes(Engine).size() == 0;
		// assert car.getAttributes(Engine).size() == 0;
		// assert microcar.getAttributes(Engine).size() == 2;
		assert microcar.getAttributes(Engine).contains(power);
		assert microcar.getAttributes(Engine).contains(airconditioner);
	}

	public void test1AttributWith3LevelsInheritance1AttributOnParent1AttributOnFirstChild() {
		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic power = Engine.addInstance("Power", vehicle);
		Generic car = Engine.addInstance(vehicle, "Car");
		Generic airconditioner = Engine.addInstance("AirConditioner", car);
		Generic microcar = Engine.addInstance(car, "Microcar");
		// assert vehicle.getAttributes(Engine).size() == 1;
		assert vehicle.getAttributes(Engine).contains(power);
		// assert car.getAttributes(Engine).size() == 2;
		assert car.getAttributes(Engine).contains(power);
		assert car.getAttributes(Engine).contains(airconditioner);
		// assert microcar.getAttributes(Engine).size() == 2;
		assert microcar.getAttributes(Engine).contains(power);
		assert microcar.getAttributes(Engine).contains(airconditioner);
	}

	public void test1AttributWith3LevelsInheritance1AttributOnParent1AttributOnSecondChild() {
		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic power = Engine.addInstance("Power", vehicle);
		Generic car = Engine.addInstance(vehicle, "Car");
		Generic microcar = Engine.addInstance(car, "Microcar");
		Generic airconditioner = Engine.addInstance("AirConditioner", microcar);
		// assert vehicle.getAttributes(Engine).size() == 1;
		assert vehicle.getAttributes(Engine).contains(power);
		// assert car.getAttributes(Engine).size() == 1;
		assert car.getAttributes(Engine).contains(power);
		// assert microcar.getAttributes(Engine).size() == 2;
		assert microcar.getAttributes(Engine).contains(power);
		assert microcar.getAttributes(Engine).contains(airconditioner);
	}

	public void test1AttributWith3LevelsInheritance1AttributFirstChild1AttributOnSecondChild() {
		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic car = Engine.addInstance(vehicle, "Car");
		Generic power = Engine.addInstance("Power", car);
		Generic microcar = Engine.addInstance(car, "Microcar");
		Generic airconditioner = Engine.addInstance("AirConditioner", microcar);
		// assert vehicle.getAttributes(Engine).size() == 0;
		// assert car.getAttributes(Engine).size() == 1;
		assert car.getAttributes(Engine).contains(power);
		// assert microcar.getAttributes(Engine).size() == 2;
		assert microcar.getAttributes(Engine).contains(power);
		assert microcar.getAttributes(Engine).contains(airconditioner);
	}

	public void test1AttributWith2LevelsInheritance2ChildrenAt2ndLevel1AttributOnParent() {
		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic power = Engine.addInstance("Power", vehicle);
		Generic car = Engine.addInstance(vehicle, "Car");
		Generic caravan = Engine.addInstance(vehicle, "Caravan");
		// assert vehicle.getAttributes(Engine).size() == 1;
		assert vehicle.getAttributes(Engine).contains(power);
		// assert car.getAttributes(Engine).size() == 1;
		assert car.getAttributes(Engine).contains(power);
		// assert caravan.getAttributes(Engine).size() == 1;
		assert caravan.getAttributes(Engine).contains(power);
	}

	public void test1AttributWith2LevelsInheritance2ChildrenAt2ndLevel1AttributOnLevel1FirstChild() {
		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic car = Engine.addInstance(vehicle, "Car");
		Generic power = Engine.addInstance("Power", car);
		Generic caravan = Engine.addInstance(vehicle, "Caravan");
		// assert vehicle.getAttributes(Engine).size() == 0;
		// assert car.getAttributes(Engine).size() == 1;
		assert car.getAttributes(Engine).contains(power);
		// assert caravan.getAttributes(Engine).size() == 0;
	}

	public void test1AttributWith2LevelsInheritance2ChildrenAt2ndLevel1AttributOnLevel1SecondChild() {
		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic car = Engine.addInstance(vehicle, "Car");
		Generic caravan = Engine.addInstance(vehicle, "Caravan");
		Generic power = Engine.addInstance("Power", caravan);
		// assert vehicle.getAttributes(Engine).size() == 0;
		// assert car.getAttributes(Engine).size() == 0;
		// assert caravan.getAttributes(Engine).size() == 1;
		assert caravan.getAttributes(Engine).contains(power);
	}

	public void test1AttributWith3LevelsInheritance2ChildrenAt2ndLevel1ChildAtThirdLevel1AttributOnParent() {
		Generic Engine = new Engine();
		Generic object = Engine.addInstance("Object");
		Generic power = Engine.addInstance("Power", object);
		Generic vehicle = Engine.addInstance(object, "Vehicle");
		Generic robot = Engine.addInstance(object, "Robot");
		Generic transformer = Engine.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		// assert object.getAttributes(Engine).size() == 1;
		assert object.getAttributes(Engine).contains(power);
		// assert vehicle.getAttributes(Engine).size() == 1;
		assert vehicle.getAttributes(Engine).contains(power);
		// assert robot.getAttributes(Engine).size() == 1;
		assert robot.getAttributes(Engine).contains(power);
		// assert transformer.getAttributes(Engine).size() == 1;
		assert transformer.getAttributes(Engine).contains(power);
	}

	public void test1AttributWith3LevelsInheritance2ChildrenAt2ndLevel1ChildAtThirdLevel1AttributLevel1FistChild() {
		Generic Engine = new Engine();
		Generic object = Engine.addInstance("Object");
		Generic vehicle = Engine.addInstance(object, "Vehicle");
		Generic power = Engine.addInstance("Power", vehicle);
		Generic robot = Engine.addInstance(object, "Robot");
		Generic transformer = Engine.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		// assert object.getAttributes(Engine).size() == 0;
		// assert vehicle.getAttributes(Engine).size() == 1;
		assert vehicle.getAttributes(Engine).contains(power);
		// assert robot.getAttributes(Engine).size() == 0;
		// assert transformer.getAttributes(Engine).size() == 1;
		assert transformer.getAttributes(Engine).contains(power);
	}

	public void test1AttributWith3LevelsInheritance2ChildrenAt2ndLevel1ChildAtThirdLevel1AttributLevel1SecondChild() {
		Generic Engine = new Engine();
		Generic object = Engine.addInstance("Object");
		Generic vehicle = Engine.addInstance(object, "Vehicle");
		Generic robot = Engine.addInstance(object, "Robot");
		Generic power = Engine.addInstance("Power", vehicle);
		Generic transformer = Engine.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		// assert object.getAttributes(Engine).size() == 0;
		// assert vehicle.getAttributes(Engine).size() == 1;
		assert vehicle.getAttributes(Engine).contains(power);
		// assert robot.getAttributes(Engine).size() == 0;
		// assert transformer.getAttributes(Engine).size() == 1;
		assert transformer.getAttributes(Engine).contains(power);
	}

	public void test1AttributWith3LevelsInheritance2ChildrenAt2ndLevel1ChildAtThirdLevel1AttributLevel2Child1() {
		Generic Engine = new Engine();
		Generic object = Engine.addInstance("Object");
		Generic vehicle = Engine.addInstance(object, "Vehicle");
		Generic robot = Engine.addInstance(object, "Robot");
		Generic transformer = Engine.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		Generic power = Engine.addInstance("Power", transformer);
		// assert object.getAttributes(Engine).size() == 0;
		// assert vehicle.getAttributes(Engine).size() == 0;
		// assert robot.getAttributes(Engine).size() == 0;
		// assert transformer.getAttributes(Engine).size() == 1;
		assert transformer.getAttributes(Engine).contains(power);
	}

	public void test2AttributsWith3LevelsInheritance2ChildrenAt2ndLevel1ChildAtThirdLevel2AttributsOnParent() {
		Generic Engine = new Engine();
		Generic object = Engine.addInstance("Object");
		Generic power = Engine.addInstance("Power", object);
		Generic airconditioner = Engine.addInstance("AirConditioner", object);
		Generic vehicle = Engine.addInstance(object, "Vehicle");
		Generic robot = Engine.addInstance(object, "Robot");
		Generic transformer = Engine.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		// assert object.getAttributes(Engine).size() == 2 : object.getAttributes(Engine);
		assert object.getAttributes(Engine).contains(power);
		assert object.getAttributes(Engine).contains(airconditioner);
		// assert vehicle.getAttributes(Engine).size() == 2;
		assert vehicle.getAttributes(Engine).contains(power);
		assert vehicle.getAttributes(Engine).contains(airconditioner);
		// assert robot.getAttributes(Engine).size() == 2;
		assert robot.getAttributes(Engine).contains(power);
		assert robot.getAttributes(Engine).contains(airconditioner);
		// assert transformer.getAttributes(Engine).size() == 2;
		assert transformer.getAttributes(Engine).contains(power);
		assert transformer.getAttributes(Engine).contains(airconditioner);
	}

	public void test2AttributsWith3LevelsInheritance2ChildrenAt2ndLevel1ChildAtThirdLevel2AttributsLevel1FirstChild() {
		Generic Engine = new Engine();
		Generic object = Engine.addInstance("Object");
		Generic vehicle = Engine.addInstance(object, "Vehicle");
		Generic power = Engine.addInstance("Power", vehicle);
		Generic airconditioner = Engine.addInstance("AirConditioner", vehicle);
		Generic robot = Engine.addInstance(object, "Robot");
		Generic transformer = Engine.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		// assert object.getAttributes(Engine).size() == 0;
		// assert vehicle.getAttributes(Engine).size() == 2;
		assert vehicle.getAttributes(Engine).contains(power);
		assert vehicle.getAttributes(Engine).contains(airconditioner);
		// assert robot.getAttributes(Engine).size() == 0;
		// assert transformer.getAttributes(Engine).size() == 2;
		assert transformer.getAttributes(Engine).contains(power);
		assert transformer.getAttributes(Engine).contains(airconditioner);
	}

	public void test2AttributsWith3LevelsInheritance2ChildrenAt2ndLevel1ChildAtThirdLevel2AttributsLevel1SecondChild() {
		Generic Engine = new Engine();
		Generic object = Engine.addInstance("Object");
		Generic vehicle = Engine.addInstance(object, "Vehicle");
		Generic robot = Engine.addInstance(object, "Robot");
		Generic power = Engine.addInstance("Power", robot);
		Generic airconditioner = Engine.addInstance("AirConditioner", robot);
		Generic transformer = Engine.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		// assert object.getAttributes(Engine).size() == 0;
		// assert vehicle.getAttributes(Engine).size() == 0;
		// assert robot.getAttributes(Engine).size() == 2;
		assert robot.getAttributes(Engine).contains(power);
		assert robot.getAttributes(Engine).contains(airconditioner);
		// assert transformer.getAttributes(Engine).size() == 2;
		assert transformer.getAttributes(Engine).contains(power);
		assert transformer.getAttributes(Engine).contains(airconditioner);
	}

	public void test2AttributsWith3LevelsInheritance2ChildrenAt2ndLevel1ChildAtThirdLevel2AttributsLevel2() {
		Generic Engine = new Engine();
		Generic object = Engine.addInstance("Object");
		Generic vehicle = Engine.addInstance(object, "Vehicle");
		Generic robot = Engine.addInstance(object, "Robot");
		Generic transformer = Engine.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		Generic power = Engine.addInstance("Power", transformer);
		Generic airconditioner = Engine.addInstance("AirConditioner", transformer);
		// assert object.getAttributes(Engine).size() == 0;
		// assert vehicle.getAttributes(Engine).size() == 0;
		// assert robot.getAttributes(Engine).size() == 0;
		// assert transformer.getAttributes(Engine).size() == 2;
		assert transformer.getAttributes(Engine).contains(power);
		assert transformer.getAttributes(Engine).contains(airconditioner);
	}

}
