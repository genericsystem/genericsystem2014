package org.genericsystem.kernel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.genericsystem.api.core.exceptions.AliveConstraintViolationException;
import org.testng.annotations.Test;

@Test
public class VertexTest extends AbstractTest {

	public void test001_getInheritings() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");

		assert vehicle.getInheritings().get().anyMatch(car::equals);
	}

	public void test001_doubleSupers() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		List<Generic> list = new ArrayList<>();

		list.add(vehicle);
		list.add(car);
		Generic sportCar = engine.addInstance(list, "SportCar");

		assert sportCar.getSupers().contains(car);
		assert sportCar.getSupers().size() == 1;
	}

	public void test002_doubleSupers() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		List<Generic> list = new ArrayList<>();
		list.add(car);
		list.add(vehicle);

		Generic sportCar = engine.addInstance(list, "SportCar");
		assert sportCar.getSupers().contains(car);
		assert sportCar.getSupers().size() == 1;
	}

	public void test001_getInstances() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		assert engine.getInstances().get().anyMatch(g -> g.equals(vehicle));
	}

	public void test001_getComponents() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic powerVehicle = engine.addInstance("power", vehicle);
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic myVehicle123 = powerVehicle.addInstance("123", myVehicle);

		assert myVehicle.getComposites().get().anyMatch(g -> g.equals(myVehicle123));
	}

	public void test002_getComponents() {
		Root engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic powerVehicle = engine.addInstance("power", vehicle);
		powerVehicle.enablePropertyConstraint();
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic vehicle256 = powerVehicle.addInstance("256", vehicle);
		Generic myVehicle123 = powerVehicle.addInstance("123", myVehicle);

		assert myVehicle.getComposites().contains(myVehicle123);
	}

	public void test() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic vehicle2 = engine.addInstance("Vehicle2");
		assert !vehicle2.inheritsFrom(vehicle);
		assert vehicle == engine.setInstance("Vehicle");
		// assert vehicle == engine.setInstance(new Vertex[] { vehicle2 }, "Vehicle");
		Generic car = engine.addInstance(Arrays.asList(vehicle), "Car");
		assert car.inheritsFrom(vehicle);
		Generic power = engine.addInstance("Power", car);
		Generic myBmw = car.addInstance("myBmw");
		assert myBmw.isInstanceOf(car);
		Generic v233 = power.addInstance(233, myBmw);
		Generic color = engine.addInstance("Color");
		Generic red = color.addInstance("red");
		Generic green = color.addInstance("green");
		Generic yellow = color.addInstance("yellow");
		assert !yellow.getSupers().stream().anyMatch(red::equals);
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		assert engine.getInstances().containsAll(Arrays.asList(vehicle, car));
		assert car.getInstances().contains(myBmw) : car.getInstances() + car.info();
		assert power.getInstances().contains(v233);
		assert car.getComposites().contains(power);
		assert car.getSupers().stream().findFirst().get() == vehicle : car.getSupers().stream().findFirst().get().info();
		assert car.getSupers().stream().anyMatch(vehicle::equals);
		assert vehicle.getInheritings().contains(car);
		assert myBmw.getComposites().contains(v233);
		assert myBmw.isInstanceOf(car);
		assert myBmw.isInstanceOf(vehicle);
		assert !myBmw.isInstanceOf(engine);
		assert vehicle.isInstanceOf(engine);
		assert !vehicle.inheritsFrom(engine) : vehicle.getLevel() + " " + engine.getLevel() + " " + vehicle.equals(engine);
		assert car.inheritsFrom(vehicle);
		assert !car.isInstanceOf(vehicle);
		assert !power.inheritsFrom(engine);
		assert !v233.inheritsFrom(power);
		assert v233.isInstanceOf(power);
		assert engine.getInstance(vehicle, "Car") != null;
		assert power.getInstance(233, myBmw) != null;
		Generic carRed = vehicleColor.addInstance("CarRed", car, red);
		Generic carGreen = vehicleColor.addInstance("CarGreen", car, green);
		assert carRed.isSuperOf(vehicleColor, Arrays.asList(carRed), "myBmwRed", Arrays.asList(myBmw, red));
		assert !carRed.isSuperOf(vehicleColor, Collections.emptyList(), "myBmwRed", Arrays.asList(myBmw, red));
		assert carRed.isSuperOf(vehicleColor, Collections.singletonList(carRed), "CarRed", Arrays.asList(myBmw, red));
		assert carGreen.isInstanceOf(vehicleColor);
		assert vehicleColor.getInstances().contains(carGreen);

		Generic myBmwYellow = vehicleColor.addInstance(carGreen, "CarRed", myBmw, yellow);
		assert carGreen.isSuperOf(vehicleColor, Collections.singletonList(carGreen), "CarRed", Arrays.asList(myBmw, yellow));
		assert myBmwYellow.inheritsFrom(carGreen);

		Generic myBmwRed = vehicleColor.addInstance(carRed, "myBmwRed", myBmw, red);
		assert carRed.isSuperOf(vehicleColor, Collections.singletonList(carRed), "myBmwRed", Arrays.asList(myBmw, red));
		assert vehicleColor.getInstances().contains(myBmwRed);
		assert myBmwRed.inheritsFrom(carRed);
		assert !yellow.inheritsFrom(red);
		assert !yellow.isInstanceOf(red);
		assert myBmwRed == vehicleColor.getInstance("myBmwRed", myBmw, red);
		assert myBmwRed == vehicleColor.setInstance("myBmwRed", myBmw, red);
		assert myBmwRed == vehicleColor.getInstance("myBmwRed", myBmw, red) : vehicleColor.getInstance("myBmwRed", myBmw, red).info();
		assert myBmwRed.inheritsFrom(carRed);
		assert car.getAttributes(engine).contains(power) : car.getAttributes(engine);
		assert car.getAttributes(engine).contains(vehicleColor) : car.getAttributes(engine);
		// assert car.getAttributes(engine).size() == 2 : car.getAttributes(engine);
		assert !myBmwRed.inheritsFrom(power);
		assert !v233.inheritsFrom(power);
		assert v233.isInstanceOf(power);

		assert myBmw.getHolders(power).contains(v233) : myBmw.getHolders(power);
		assert myBmw.getHolders(power).size() == 1 : myBmw.getHolders(power);
		assert myBmw.getValues(power).contains(233);
		assert engine.isCompositeOf(myBmw);

		// assert car.getAttributes(engine).equals(myBmw.getAttributes(engine)) : car.getAttributes(engine) + " " + myBmw.getAttributes(engine);
	}

	@Test(enabled = false)
	public void test2() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic vehicle2 = engine.addInstance("Vehicle2");
		assert vehicle == engine.setInstance("Vehicle");
		// assert vehicle != engine.setInstance(vehicle2, "Vehicle");
	}

	public void test3() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic vehiclePower = engine.addInstance("VehiclePower", vehicle);
		Generic carPower = engine.addInstance("CarPower", car);
		assert car.getAttributes(engine).containsAll(Arrays.asList(vehiclePower, carPower)) : car.getAttributes(engine);
		// assert car.getAttributes(engine).size() == 2;
	}

	public void test4() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic vehiclePower = engine.addInstance("Power", vehicle);
		Generic carPower = engine.addInstance("Power", car);
		assert car.getAttributes(engine).contains(carPower);
		// assert car.getAttributes(engine).size() == 1 : car.getAttributes(engine);
	}

	public void test5() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic vehiclePower = engine.addInstance("VehiclePower", vehicle);
		Generic carPower = engine.addInstance(vehiclePower, "CarPower", car);
		assert car.getAttributes(engine).contains(carPower);
		// assert car.getAttributes(engine).size() == 1 : car.getAttributes(engine);
	}

	public void test6() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic sportCar = engine.addInstance(car, "SportCar");
		Generic vehiclePower = engine.addInstance("VehiclePower", vehicle);
		Generic carPower = engine.addInstance(vehiclePower, "CarPower", car);
		Generic sportCarPower = engine.addInstance(vehiclePower, "SportCarPower", sportCar);
		assert sportCar.getAttributes(engine).containsAll(Arrays.asList(carPower, sportCarPower)) : car.getAttributes(engine) + " " + sportCarPower.info();
		// assert sportCar.getAttributes(engine).size() == 2;
	}

	public void test7() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic robot = engine.addInstance("robot");
		Generic transformer = engine.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		Generic vehiclePower = engine.addInstance("Power", vehicle);
		Generic robotPower = engine.addInstance("Power", robot);
		assert transformer.getAttributes(engine).containsAll(Arrays.asList(robotPower, vehiclePower)) : transformer.getAttributes(engine);
		// assert transformer.getAttributes(engine).size() == 2;
	}

	public void test8() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic robot = engine.addInstance("robot");
		Generic transformer = engine.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		Generic vehiclePower = engine.addInstance("VehiclePower", vehicle);
		Generic robotPower = engine.addInstance("RobotPower", robot);
		Generic transformerPower = engine.addInstance(Arrays.asList(vehiclePower, robotPower), "TransformerPower", transformer);
		assert transformer.getAttributes(engine).contains(transformerPower) : transformer.getAttributes(engine);
		// assert transformer.getAttributes(engine).size() == 1;
	}

	public void test9() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic robot = engine.addInstance("robot");
		Generic transformer = engine.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		Generic vehiclePower = engine.addInstance("Power", vehicle);
		Generic robotPower = engine.addInstance("Power", robot);
		Generic transformerPower = engine.addInstance("Power", transformer);
		assert transformer.getAttributes(engine).contains(transformerPower) : transformer.getAttributes(engine);
		// assert transformer.getAttributes(engine).size() == 1;
		// assert transformer.getAttributes(robot).size() == 0 : transformer.getAttributes(robot);
	}

	public void test10() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		vehicle.remove();
		catchAndCheckCause(() -> engine.addInstance(vehicle, "Car"), AliveConstraintViolationException.class);
	}

	public void test11() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		vehicle.remove();
		catchAndCheckCause(() -> vehicle.addInstance("myVehicle"), AliveConstraintViolationException.class);
	}

	public void test12() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		assert car.getCurrentCache().computeDependencies(car).contains(car);
		assert !car.getCurrentCache().computeDependencies(car).contains(vehicle);
		assert !car.getCurrentCache().computeDependencies(car).contains(engine);
		assert vehicle.getCurrentCache().computeDependencies(vehicle).contains(car);
		assert vehicle.getCurrentCache().computeDependencies(vehicle).contains(vehicle);
		assert !vehicle.getCurrentCache().computeDependencies(vehicle).contains(engine);
	}

	public void test13() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic sportCar = engine.addInstance(car, "SportCar");
		assert car.getCurrentCache().computeDependencies(car).contains(car);
		assert !car.getCurrentCache().computeDependencies(car).contains(vehicle);
		assert car.getCurrentCache().computeDependencies(car).contains(sportCar);
		assert !car.getCurrentCache().computeDependencies(car).contains(engine);
		assert vehicle.getCurrentCache().computeDependencies(vehicle).contains(car);
		assert vehicle.getCurrentCache().computeDependencies(vehicle).contains(vehicle);
		assert !vehicle.getCurrentCache().computeDependencies(vehicle).contains(engine);
		assert vehicle.getCurrentCache().computeDependencies(vehicle).contains(sportCar);
		// assert false : engine.computeAllDependencies();
	}

	public void test14() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic myCar = car.addInstance("myCar");
		assert !myCar.isAncestorOf(engine);
		assert engine.isAncestorOf(myCar);
		assert car.getCurrentCache().computeDependencies(car).contains(car);
		assert !car.getCurrentCache().computeDependencies(car).contains(vehicle);
		assert car.getCurrentCache().computeDependencies(car).contains(myCar);
		assert !car.getCurrentCache().computeDependencies(car).contains(engine);
		assert vehicle.getCurrentCache().computeDependencies(vehicle).contains(car);
		assert vehicle.getCurrentCache().computeDependencies(vehicle).contains(vehicle);
		assert !vehicle.getCurrentCache().computeDependencies(vehicle).contains(engine);
		assert vehicle.getCurrentCache().computeDependencies(vehicle).contains(myCar);
		// assert false : engine.computeAllDependencies();
	}

	public void test15() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic power = engine.addInstance("Power", car);
		Generic unit = engine.addInstance("Unit", power);
		assert vehicle.isAncestorOf(unit);
		assert car.getCurrentCache().computeDependencies(car).contains(car);
		assert !car.getCurrentCache().computeDependencies(car).contains(vehicle);
		assert car.getCurrentCache().computeDependencies(car).contains(power);
		assert car.getCurrentCache().computeDependencies(car).contains(unit);
		assert !car.getCurrentCache().computeDependencies(car).contains(engine);
		assert vehicle.getCurrentCache().computeDependencies(vehicle).contains(car);
		assert vehicle.getCurrentCache().computeDependencies(vehicle).contains(vehicle);
		assert !vehicle.getCurrentCache().computeDependencies(vehicle).contains(engine);
		assert vehicle.getCurrentCache().computeDependencies(vehicle).contains(power);
		assert vehicle.getCurrentCache().computeDependencies(vehicle).contains(unit);
		// assert false : engine.computeAllDependencies();
	}

	public void test16() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic vehiclePower = engine.addInstance("Power", vehicle);
		Generic car = engine.addInstance(vehicle, "Car");
		Generic myCar = car.addInstance("myCar");
		Generic v233 = vehiclePower.addInstance(233, myCar);
		assert v233.isAncestorOf(v233);
		assert myCar.isAncestorOf(v233);
		assert car.isAncestorOf(v233);
		assert vehiclePower.isAncestorOf(v233);
		assert vehicle.isAncestorOf(v233);
		Generic car233 = car.setHolder(vehiclePower, 233);
		assert car233.isAlive();
		assert !car233.isAncestorOf(v233);
		assert engine.getCurrentCache().computePotentialDependencies(vehiclePower, Collections.emptyList(), 233, Arrays.asList(car)).contains(car233);
	}
}
