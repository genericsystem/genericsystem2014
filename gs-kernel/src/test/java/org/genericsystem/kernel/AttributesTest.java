package org.genericsystem.kernel;

import java.util.Arrays;
import java.util.Collections;

import org.genericsystem.api.exception.MetaRuleConstraintViolationException;
import org.testng.annotations.Test;

@Test
public class AttributesTest extends AbstractTest {

	public void test001_getAttribute() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic powerVehicle = vehicle.addAttribute("power");
		assert powerVehicle == vehicle.getAttribute("power", vehicle);
	}

	public void test001b_getAttribute() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic powerVehicle = vehicle.addAttribute("power");
		assert powerVehicle == vehicle.getAttribute("power");
	}

	public void test002_getAttribute() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic powerVehicle = vehicle.addAttribute("power");
		assert powerVehicle == myVehicle.getAttribute("power", vehicle);
	}

	public void test003_getAttribute() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic car = root.addInstance(vehicle, "Car");
		Generic powerVehicle = vehicle.addAttribute("power");
		assert powerVehicle == car.getAttribute("power", vehicle);
	}

	public void test004_getAttribute() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic car = root.addInstance(vehicle, "Car");
		Generic myCar = car.addInstance("myCar");
		Generic powerVehicle = vehicle.addAttribute("power");
		assert powerVehicle == myCar.getAttribute("power", vehicle);
	}

	public void test005_getAttribute() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic color = root.addInstance("Color");
		Generic colorVehicle = vehicle.addAttribute("colorVehicle", color);
		assert colorVehicle == vehicle.getAttribute("colorVehicle", vehicle, color);
	}

	public void test006_getAttribute() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic color = root.addInstance("Color");
		Generic ultraColor = root.addInstance(color, "UltraColor");
		vehicle.addAttribute("colorVehicle", color);
		Generic ultraColorVehicle = vehicle.addAttribute("colorVehicle", ultraColor);
		assert ultraColorVehicle == vehicle.getAttribute("colorVehicle", vehicle, color);
		assert ultraColorVehicle == vehicle.getAttribute("colorVehicle", vehicle, ultraColor);
	}

	public void test007_getAttribute() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic color = root.addInstance("Color");
		Generic ultraColor = root.addInstance(color, "UltraColor");
		vehicle.addAttribute("colorVehicle", color);
		Generic ultraColorVehicle = vehicle.addAttribute("colorVehicle", ultraColor);
		assert ultraColorVehicle == myVehicle.getAttribute("colorVehicle", vehicle, color);
		assert ultraColorVehicle == myVehicle.getAttribute("colorVehicle", vehicle, ultraColor);
	}

	public void test007b_getAttribute() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic color = root.addInstance("Color");
		Generic ultraColor = root.addInstance(color, "UltraColor");
		vehicle.addAttribute("colorVehicle", color);
		Generic ultraColorVehicle = vehicle.addAttribute("colorVehicle", ultraColor);
		assert ultraColorVehicle == myVehicle.getAttribute("colorVehicle", color);
		assert ultraColorVehicle == myVehicle.getAttribute("colorVehicle", ultraColor);
	}

	public void test001_getInstance() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		assert vehicle.getLevel() == 1 : vehicle.getLevel();
		Generic power = root.addInstance("Power", vehicle);
		assert root.getInstance("Power", vehicle) == power;
		// assert root.selectInstances("Power").count() == 1;
		// assert root.selectInstances("Power").anyMatch(x -> x.equals(power));
		assert power.getComponents().size() == 1;
		assert vehicle.equals(power.getComponents().get(0));
		assert power.isAlive();
	}

	public void test002_getInstance() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		assert vehicle.getLevel() == 1 : vehicle.getLevel();
		Generic powerType = root.addInstance("Power");
		Generic power = root.addInstance("Power", vehicle);
		assert root.getInstance("Power", vehicle) == power;
		// assert root.selectInstances("Power").count() == 2;
		// assert root.selectInstances("Power").anyMatch(x -> x.equals(powerType));
		// assert root.selectInstances("Power").anyMatch(x -> x.equals(power));
		assert power.getComponents().size() == 1;
		assert vehicle.equals(power.getComponents().get(0));
		assert power.isAlive();
	}

	public void test003_isDependencyOf_ByComponent() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic car = root.addInstance(vehicle, "Car");
		Generic carPower = root.addInstance("Power", car);
		Generic carPowerUnit = root.addInstance("Unit", carPower);
		assert carPower.isDependencyOf(root, Collections.emptyList(), "Power", Collections.singletonList(vehicle));
		assert carPowerUnit.isDependencyOf(root, Collections.emptyList(), "Power", Collections.singletonList(vehicle));
		assert !carPowerUnit.inheritsFrom(root, "Power", Collections.singletonList(vehicle));
		Generic vehiclePower = root.addInstance("Power", vehicle);
		assert root.getInstance("Power", car).getSupers().stream().anyMatch(x -> x.equals(vehiclePower));
	}

	public void test003_isDependencyOf_ByMeta() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic power = vehicle.addAttribute("Power");
		assert !power.getMeta().equals(root);
		assert power.getMeta().equals(root.getMetaAttribute());
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic myVehicle123 = myVehicle.addHolder(power, "myVehicle123");

		assert myVehicle123.isDependencyOf(root, Collections.emptyList(), "Power", Collections.singletonList(vehicle));
		assert !myVehicle123.inheritsFrom(root, "Power", Collections.singletonList(vehicle));
	}

	public void test004() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		catchAndCheckCause(() -> myVehicle.addAttribute("Power", vehicle), MetaRuleConstraintViolationException.class);
	}

	public void test1AttributWith2LevelsInheritance1AttributOnParent() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic power = root.addInstance("Power", vehicle);
		Generic car = root.addInstance(vehicle, "Car");
		// assert vehicle.getAttributes(root).size() == 1 : vehicle.getAttributes(root);
		assert vehicle.getAttributes(root).contains(power);
		// assert car.getAttributes(root).size() == 1;
		assert car.getAttributes(root).contains(power);
	}

	public void test1AttributWith2LevelsInheritance1AttributOnFistChild() {

		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic car = root.addInstance(vehicle, "Car");
		Generic power = root.addInstance("Power", car);
		assert root.getLevel() == 0;
		assert vehicle.getLevel() == 1;
		assert car.getLevel() == 1;
		assert power.getLevel() == 1;
		assert car.getAttributes(root).contains(power);
	}

	public void test1AttributWith3LevelsInheritance1AttributOnParent() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic power = root.addInstance("Power", vehicle);
		Generic car = root.addInstance(vehicle, "Car");
		Generic microcar = root.addInstance(car, "Microcar");
		// assert vehicle.getAttributes(root).size() == 1;
		assert vehicle.getAttributes(root).contains(power);
		// assert car.getAttributes(root).size() == 1;
		assert car.getAttributes(root).contains(power);
		// assert microcar.getAttributes(root).size() == 1;
		assert microcar.getAttributes(root).contains(power);
	}

	public void test1AttributWith3LevelsInheritance1AttributOnFirstChild() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic car = root.addInstance(vehicle, "Car");
		Generic power = root.addInstance("Power", car);
		Generic microcar = root.addInstance(car, "Microcar");
		// assert vehicle.getAttributes(root).size() == 0;
		// assert car.getAttributes(root).size() == 1;
		assert car.getAttributes(root).contains(power);
		// assert microcar.getAttributes(root).size() == 1;
		assert microcar.getAttributes(root).contains(power);
	}

	public void test1AttributWith3LevelsInheritance1AttributOnSecondChild() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic car = root.addInstance(vehicle, "Car");
		Generic microcar = root.addInstance(car, "Microcar");
		Generic power = root.addInstance("Power", microcar);
		// assert vehicle.getAttributes(root).size() == 0;
		// assert car.getAttributes(root).size() == 0;
		// assert microcar.getAttributes(root).size() == 1;
		assert microcar.getAttributes(root).contains(power);
	}

	public void test2Attributs() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic power = root.addInstance("Power", vehicle);
		Generic airconditioner = root.addInstance("AirConditioner", vehicle);
		// assert vehicle.getAttributes(root).size() == 2;
		assert vehicle.getAttributes(root).contains(power);
		assert vehicle.getAttributes(root).contains(airconditioner);
		assert power.isAlive();
		assert airconditioner.isAlive();
	}

	public void test2AttributsWith2LevelsInheritance2AttributsOnParent() {

		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic power = root.addInstance("Power", vehicle);
		Generic airconditioner = root.addInstance("AirConditioner", vehicle);
		Generic car = root.addInstance(vehicle, "Car");
		// assert vehicle.getAttributes(root).size() == 2;
		assert vehicle.getAttributes(root).contains(power);
		assert vehicle.getAttributes(root).contains(airconditioner);
		// assert car.getAttributes(root).size() == 2;
		assert car.getAttributes(root).contains(power);
		assert car.getAttributes(root).contains(airconditioner);
	}

	public void test2AttributsWith2LevelsInheritance2AttributsOnFistChild() {

		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic car = root.addInstance(vehicle, "Car");
		Generic power = root.addInstance("Power", car);
		Generic airconditioner = root.addInstance("AirConditioner", car);
		// assert vehicle.getAttributes(root).size() == 0;
		// assert car.getAttributes(root).size() == 2;
		assert car.getAttributes(root).contains(power);
		assert car.getAttributes(root).contains(airconditioner);
	}

	public void test2AttributsWith2LevelsInheritance1AttributOnParentAnd1AttributOnFistChild() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic power = root.addInstance("Power", vehicle);
		Generic car = root.addInstance(vehicle, "Car");
		Generic airconditioner = root.addInstance("AirConditioner", car);
		// assert vehicle.getAttributes(root).size() == 1;
		assert vehicle.getAttributes(root).contains(power);
		// assert car.getAttributes(root).size() == 2;
		assert car.getAttributes(root).contains(power);
		assert car.getAttributes(root).contains(airconditioner);
	}

	public void test1AttributWith3LevelsInheritance2AttributOnParent() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic power = root.addInstance("Power", vehicle);
		Generic airconditioner = root.addInstance("AirConditioner", vehicle);
		Generic car = root.addInstance(vehicle, "Car");
		Generic microcar = root.addInstance(car, "Microcar");
		// assert vehicle.getAttributes(root).size() == 2;
		assert vehicle.getAttributes(root).contains(power);
		assert vehicle.getAttributes(root).contains(airconditioner);
		// assert car.getAttributes(root).size() == 2;
		assert car.getAttributes(root).contains(power);
		assert car.getAttributes(root).contains(airconditioner);
		// assert microcar.getAttributes(root).size() == 2;
		assert microcar.getAttributes(root).contains(power);
		assert microcar.getAttributes(root).contains(airconditioner);
	}

	public void test1AttributWith3LevelsInheritance2AttributFirstChild() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic car = root.addInstance(vehicle, "Car");
		Generic power = root.addInstance("Power", car);
		Generic airconditioner = root.addInstance("AirConditioner", car);
		Generic microcar = root.addInstance(car, "Microcar");
		// assert vehicle.getAttributes(root).size() == 0;
		// assert car.getAttributes(root).size() == 2;
		assert car.getAttributes(root).contains(power);
		assert car.getAttributes(root).contains(airconditioner);
		// assert microcar.getAttributes(root).size() == 2;
		assert microcar.getAttributes(root).contains(power);
		assert microcar.getAttributes(root).contains(airconditioner);
	}

	public void test1AttributWith3LevelsInheritance2AttributOnSecondChild() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic car = root.addInstance(vehicle, "Car");
		Generic microcar = root.addInstance(car, "Microcar");
		Generic power = root.addInstance("Power", microcar);
		Generic airconditioner = root.addInstance("AirConditioner", microcar);
		// assert vehicle.getAttributes(root).size() == 0;
		// assert car.getAttributes(root).size() == 0;
		// assert microcar.getAttributes(root).size() == 2;
		assert microcar.getAttributes(root).contains(power);
		assert microcar.getAttributes(root).contains(airconditioner);
	}

	public void test1AttributWith3LevelsInheritance1AttributOnParent1AttributOnFirstChild() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic power = root.addInstance("Power", vehicle);
		Generic car = root.addInstance(vehicle, "Car");
		Generic airconditioner = root.addInstance("AirConditioner", car);
		Generic microcar = root.addInstance(car, "Microcar");
		// assert vehicle.getAttributes(root).size() == 1;
		assert vehicle.getAttributes(root).contains(power);
		// assert car.getAttributes(root).size() == 2;
		assert car.getAttributes(root).contains(power);
		assert car.getAttributes(root).contains(airconditioner);
		// assert microcar.getAttributes(root).size() == 2;
		assert microcar.getAttributes(root).contains(power);
		assert microcar.getAttributes(root).contains(airconditioner);
	}

	public void test1AttributWith3LevelsInheritance1AttributOnParent1AttributOnSecondChild() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic power = root.addInstance("Power", vehicle);
		Generic car = root.addInstance(vehicle, "Car");
		Generic microcar = root.addInstance(car, "Microcar");
		Generic airconditioner = root.addInstance("AirConditioner", microcar);
		// assert vehicle.getAttributes(root).size() == 1;
		assert vehicle.getAttributes(root).contains(power);
		// assert car.getAttributes(root).size() == 1;
		assert car.getAttributes(root).contains(power);
		// assert microcar.getAttributes(root).size() == 2;
		assert microcar.getAttributes(root).contains(power);
		assert microcar.getAttributes(root).contains(airconditioner);
	}

	public void test1AttributWith3LevelsInheritance1AttributFirstChild1AttributOnSecondChild() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic car = root.addInstance(vehicle, "Car");
		Generic power = root.addInstance("Power", car);
		Generic microcar = root.addInstance(car, "Microcar");
		Generic airconditioner = root.addInstance("AirConditioner", microcar);
		// assert vehicle.getAttributes(root).size() == 0;
		// assert car.getAttributes(root).size() == 1;
		assert car.getAttributes(root).contains(power);
		// assert microcar.getAttributes(root).size() == 2;
		assert microcar.getAttributes(root).contains(power);
		assert microcar.getAttributes(root).contains(airconditioner);
	}

	public void test1AttributWith2LevelsInheritance2ChildrenAt2ndLevel1AttributOnParent() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic power = root.addInstance("Power", vehicle);
		Generic car = root.addInstance(vehicle, "Car");
		Generic caravan = root.addInstance(vehicle, "Caravan");
		// assert vehicle.getAttributes(root).size() == 1;
		assert vehicle.getAttributes(root).contains(power);
		// assert car.getAttributes(root).size() == 1;
		assert car.getAttributes(root).contains(power);
		// assert caravan.getAttributes(root).size() == 1;
		assert caravan.getAttributes(root).contains(power);
	}

	public void test1AttributWith2LevelsInheritance2ChildrenAt2ndLevel1AttributOnLevel1FirstChild() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic car = root.addInstance(vehicle, "Car");
		Generic power = root.addInstance("Power", car);
		Generic caravan = root.addInstance(vehicle, "Caravan");
		// assert vehicle.getAttributes(root).size() == 0;
		// assert car.getAttributes(root).size() == 1;
		assert car.getAttributes(root).contains(power);
		// assert caravan.getAttributes(root).size() == 0;
	}

	public void test1AttributWith2LevelsInheritance2ChildrenAt2ndLevel1AttributOnLevel1SecondChild() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic car = root.addInstance(vehicle, "Car");
		Generic caravan = root.addInstance(vehicle, "Caravan");
		Generic power = root.addInstance("Power", caravan);
		// assert vehicle.getAttributes(root).size() == 0;
		// assert car.getAttributes(root).size() == 0;
		// assert caravan.getAttributes(root).size() == 1;
		assert caravan.getAttributes(root).contains(power);
	}

	public void test1AttributWith3LevelsInheritance2ChildrenAt2ndLevel1ChildAtThirdLevel1AttributOnParent() {
		Generic root = new Root();
		Generic object = root.addInstance("Object");
		Generic power = root.addInstance("Power", object);
		Generic vehicle = root.addInstance(object, "Vehicle");
		Generic robot = root.addInstance(object, "Robot");
		Generic transformer = root.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		// assert object.getAttributes(root).size() == 1;
		assert object.getAttributes(root).contains(power);
		// assert vehicle.getAttributes(root).size() == 1;
		assert vehicle.getAttributes(root).contains(power);
		// assert robot.getAttributes(root).size() == 1;
		assert robot.getAttributes(root).contains(power);
		// assert transformer.getAttributes(root).size() == 1;
		assert transformer.getAttributes(root).contains(power);
	}

	public void test1AttributWith3LevelsInheritance2ChildrenAt2ndLevel1ChildAtThirdLevel1AttributLevel1FistChild() {
		Generic root = new Root();
		Generic object = root.addInstance("Object");
		Generic vehicle = root.addInstance(object, "Vehicle");
		Generic power = root.addInstance("Power", vehicle);
		Generic robot = root.addInstance(object, "Robot");
		Generic transformer = root.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		// assert object.getAttributes(root).size() == 0;
		// assert vehicle.getAttributes(root).size() == 1;
		assert vehicle.getAttributes(root).contains(power);
		// assert robot.getAttributes(root).size() == 0;
		// assert transformer.getAttributes(root).size() == 1;
		assert transformer.getAttributes(root).contains(power);
	}

	public void test1AttributWith3LevelsInheritance2ChildrenAt2ndLevel1ChildAtThirdLevel1AttributLevel1SecondChild() {
		Generic root = new Root();
		Generic object = root.addInstance("Object");
		Generic vehicle = root.addInstance(object, "Vehicle");
		Generic robot = root.addInstance(object, "Robot");
		Generic power = root.addInstance("Power", vehicle);
		Generic transformer = root.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		// assert object.getAttributes(root).size() == 0;
		// assert vehicle.getAttributes(root).size() == 1;
		assert vehicle.getAttributes(root).contains(power);
		// assert robot.getAttributes(root).size() == 0;
		// assert transformer.getAttributes(root).size() == 1;
		assert transformer.getAttributes(root).contains(power);
	}

	public void test1AttributWith3LevelsInheritance2ChildrenAt2ndLevel1ChildAtThirdLevel1AttributLevel2Child1() {
		Generic root = new Root();
		Generic object = root.addInstance("Object");
		Generic vehicle = root.addInstance(object, "Vehicle");
		Generic robot = root.addInstance(object, "Robot");
		Generic transformer = root.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		Generic power = root.addInstance("Power", transformer);
		// assert object.getAttributes(root).size() == 0;
		// assert vehicle.getAttributes(root).size() == 0;
		// assert robot.getAttributes(root).size() == 0;
		// assert transformer.getAttributes(root).size() == 1;
		assert transformer.getAttributes(root).contains(power);
	}

	public void test2AttributsWith3LevelsInheritance2ChildrenAt2ndLevel1ChildAtThirdLevel2AttributsOnParent() {
		Generic root = new Root();
		Generic object = root.addInstance("Object");
		Generic power = root.addInstance("Power", object);
		Generic airconditioner = root.addInstance("AirConditioner", object);
		Generic vehicle = root.addInstance(object, "Vehicle");
		Generic robot = root.addInstance(object, "Robot");
		Generic transformer = root.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		// assert object.getAttributes(root).size() == 2 : object.getAttributes(root);
		assert object.getAttributes(root).contains(power);
		assert object.getAttributes(root).contains(airconditioner);
		// assert vehicle.getAttributes(root).size() == 2;
		assert vehicle.getAttributes(root).contains(power);
		assert vehicle.getAttributes(root).contains(airconditioner);
		// assert robot.getAttributes(root).size() == 2;
		assert robot.getAttributes(root).contains(power);
		assert robot.getAttributes(root).contains(airconditioner);
		// assert transformer.getAttributes(root).size() == 2;
		assert transformer.getAttributes(root).contains(power);
		assert transformer.getAttributes(root).contains(airconditioner);
	}

	public void test2AttributsWith3LevelsInheritance2ChildrenAt2ndLevel1ChildAtThirdLevel2AttributsLevel1FirstChild() {
		Generic root = new Root();
		Generic object = root.addInstance("Object");
		Generic vehicle = root.addInstance(object, "Vehicle");
		Generic power = root.addInstance("Power", vehicle);
		Generic airconditioner = root.addInstance("AirConditioner", vehicle);
		Generic robot = root.addInstance(object, "Robot");
		Generic transformer = root.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		// assert object.getAttributes(root).size() == 0;
		// assert vehicle.getAttributes(root).size() == 2;
		assert vehicle.getAttributes(root).contains(power);
		assert vehicle.getAttributes(root).contains(airconditioner);
		// assert robot.getAttributes(root).size() == 0;
		// assert transformer.getAttributes(root).size() == 2;
		assert transformer.getAttributes(root).contains(power);
		assert transformer.getAttributes(root).contains(airconditioner);
	}

	public void test2AttributsWith3LevelsInheritance2ChildrenAt2ndLevel1ChildAtThirdLevel2AttributsLevel1SecondChild() {
		Generic root = new Root();
		Generic object = root.addInstance("Object");
		Generic vehicle = root.addInstance(object, "Vehicle");
		Generic robot = root.addInstance(object, "Robot");
		Generic power = root.addInstance("Power", robot);
		Generic airconditioner = root.addInstance("AirConditioner", robot);
		Generic transformer = root.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		// assert object.getAttributes(root).size() == 0;
		// assert vehicle.getAttributes(root).size() == 0;
		// assert robot.getAttributes(root).size() == 2;
		assert robot.getAttributes(root).contains(power);
		assert robot.getAttributes(root).contains(airconditioner);
		// assert transformer.getAttributes(root).size() == 2;
		assert transformer.getAttributes(root).contains(power);
		assert transformer.getAttributes(root).contains(airconditioner);
	}

	public void test2AttributsWith3LevelsInheritance2ChildrenAt2ndLevel1ChildAtThirdLevel2AttributsLevel2() {
		Generic root = new Root();
		Generic object = root.addInstance("Object");
		Generic vehicle = root.addInstance(object, "Vehicle");
		Generic robot = root.addInstance(object, "Robot");
		Generic transformer = root.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		Generic power = root.addInstance("Power", transformer);
		Generic airconditioner = root.addInstance("AirConditioner", transformer);
		// assert object.getAttributes(root).size() == 0;
		// assert vehicle.getAttributes(root).size() == 0;
		// assert robot.getAttributes(root).size() == 0;
		// assert transformer.getAttributes(root).size() == 2;
		assert transformer.getAttributes(root).contains(power);
		assert transformer.getAttributes(root).contains(airconditioner);
	}

}
