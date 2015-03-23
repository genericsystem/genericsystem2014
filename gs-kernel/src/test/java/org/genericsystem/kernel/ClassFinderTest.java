package org.genericsystem.kernel;

import java.util.Arrays;

import org.genericsystem.api.core.exceptions.AliveConstraintViolationException;
import org.testng.annotations.Test;

@Test
public class ClassFinderTest extends AbstractTest {

	public void test1() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic vehiclePower = engine.addInstance("VehiclePower", vehicle);
		Generic carPower = engine.addInstance("CarPower", car);
		assert car.getAttributes(engine).containsAll(Arrays.asList(vehiclePower, carPower)) : car.getAttributes(engine);
		// assert car.getAttributes(engine).size() == 2;
	}

	public void test2() {
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
		assert !car233.getCurrentCache().computeDependencies(car233).contains(v233);
	}
}
