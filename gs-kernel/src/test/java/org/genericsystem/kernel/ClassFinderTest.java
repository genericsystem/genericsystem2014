package org.genericsystem.kernel;

import java.util.Arrays;
import java.util.Collections;

import org.genericsystem.kernel.exceptions.NotAliveException;
import org.testng.annotations.Test;

@Test
public class ClassFinderTest extends AbstractTest {

	public void test1() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex vehiclePower = engine.addInstance("VehiclePower", vehicle);
		Vertex carPower = engine.addInstance("CarPower", car);
		assert car.getAttributes(engine).containsAll(Arrays.asList(vehiclePower, carPower)) : car.getAttributes(engine);
		// assert car.getAttributes(engine).size() == 2;
	}

	public void test2() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex vehiclePower = engine.addInstance("Power", vehicle);
		Vertex carPower = engine.addInstance("Power", car);
		assert car.getAttributes(engine).contains(carPower);
		// assert car.getAttributes(engine).size() == 1 : car.getAttributes(engine);
	}

	public void test5() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex vehiclePower = engine.addInstance("VehiclePower", vehicle);
		Vertex carPower = engine.addInstance(vehiclePower, "CarPower", car);
		assert car.getAttributes(engine).contains(carPower);
		// assert car.getAttributes(engine).size() == 1 : car.getAttributes(engine);
	}

	public void test6() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex sportCar = engine.addInstance(car, "SportCar");
		Vertex vehiclePower = engine.addInstance("VehiclePower", vehicle);
		Vertex carPower = engine.addInstance(vehiclePower, "CarPower", car);
		Vertex sportCarPower = engine.addInstance(vehiclePower, "SportCarPower", sportCar);
		assert sportCar.getAttributes(engine).containsAll(Arrays.asList(carPower, sportCarPower)) : car.getAttributes(engine) + " " + sportCarPower.info();
		// assert sportCar.getAttributes(engine).size() == 2;
	}

	public void test7() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex robot = engine.addInstance("robot");
		Vertex transformer = engine.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		Vertex vehiclePower = engine.addInstance("Power", vehicle);
		Vertex robotPower = engine.addInstance("Power", robot);
		assert transformer.getAttributes(engine).containsAll(Arrays.asList(robotPower, vehiclePower)) : transformer.getAttributes(engine);
		// assert transformer.getAttributes(engine).size() == 2;
	}

	public void test8() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex robot = engine.addInstance("robot");
		Vertex transformer = engine.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		Vertex vehiclePower = engine.addInstance("VehiclePower", vehicle);
		Vertex robotPower = engine.addInstance("RobotPower", robot);
		Vertex transformerPower = engine.addInstance(Arrays.asList(vehiclePower, robotPower), "TransformerPower", transformer);
		assert transformer.getAttributes(engine).contains(transformerPower) : transformer.getAttributes(engine);
		// assert transformer.getAttributes(engine).size() == 1;
	}

	public void test9() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex robot = engine.addInstance("robot");
		Vertex transformer = engine.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		Vertex vehiclePower = engine.addInstance("Power", vehicle);
		Vertex robotPower = engine.addInstance("Power", robot);
		Vertex transformerPower = engine.addInstance("Power", transformer);
		assert transformer.getAttributes(engine).contains(transformerPower) : transformer.getAttributes(engine);
		// assert transformer.getAttributes(engine).size() == 1;
		// assert transformer.getAttributes(robot).size() == 0 : transformer.getAttributes(robot);
	}

	public void test10() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		vehicle.remove();
		new RollbackCatcher() {
			@Override
			public void intercept() {
				engine.addInstance(vehicle, "Car");
			}
		}.assertIsCausedBy(NotAliveException.class);
	}

	public void test11() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		vehicle.remove();
		new RollbackCatcher() {
			@Override
			public void intercept() {
				vehicle.addInstance("myVehicle");
			}
		}.assertIsCausedBy(NotAliveException.class);
	}

	public void test12() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");
		assert car.computeDependencies().contains(car);
		assert !car.computeDependencies().contains(vehicle);
		assert !car.computeDependencies().contains(engine);
		assert vehicle.computeDependencies().contains(car);
		assert vehicle.computeDependencies().contains(vehicle);
		assert !vehicle.computeDependencies().contains(engine);
	}

	public void test13() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex sportCar = engine.addInstance(car, "SportCar");
		assert car.computeDependencies().contains(car);
		assert !car.computeDependencies().contains(vehicle);
		assert car.computeDependencies().contains(sportCar);
		assert !car.computeDependencies().contains(engine);
		assert vehicle.computeDependencies().contains(car);
		assert vehicle.computeDependencies().contains(vehicle);
		assert !vehicle.computeDependencies().contains(engine);
		assert vehicle.computeDependencies().contains(sportCar);
		// assert false : engine.computeAllDependencies();
	}

	public void test14() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex myCar = car.addInstance("myCar");
		assert !myCar.isAncestorOf(engine);
		assert engine.isAncestorOf(myCar);
		assert car.computeDependencies().contains(car);
		assert !car.computeDependencies().contains(vehicle);
		assert car.computeDependencies().contains(myCar);
		assert !car.computeDependencies().contains(engine);
		assert vehicle.computeDependencies().contains(car);
		assert vehicle.computeDependencies().contains(vehicle);
		assert !vehicle.computeDependencies().contains(engine);
		assert vehicle.computeDependencies().contains(myCar);
		// assert false : engine.computeAllDependencies();
	}

	public void test15() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex power = engine.addInstance("Power", car);
		Vertex unit = engine.addInstance("Unit", power);
		assert vehicle.isAncestorOf(unit);
		assert car.computeDependencies().contains(car);
		assert !car.computeDependencies().contains(vehicle);
		assert car.computeDependencies().contains(power);
		assert car.computeDependencies().contains(unit);
		assert !car.computeDependencies().contains(engine);
		assert vehicle.computeDependencies().contains(car);
		assert vehicle.computeDependencies().contains(vehicle);
		assert !vehicle.computeDependencies().contains(engine);
		assert vehicle.computeDependencies().contains(power);
		assert vehicle.computeDependencies().contains(unit);
		// assert false : engine.computeAllDependencies();
	}

	public void test16() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex vehiclePower = engine.addInstance("Power", vehicle);
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex myCar = car.addInstance("myCar");
		Vertex v233 = vehiclePower.addInstance(233, myCar);
		assert v233.isAncestorOf(v233);
		assert myCar.isAncestorOf(v233);
		assert car.isAncestorOf(v233);
		assert vehiclePower.isAncestorOf(v233);
		assert vehicle.isAncestorOf(v233);

		Vertex car233 = vehiclePower.buildInstance(null, false, Collections.emptyList(), 233, Arrays.asList(car));
		assert v233.dependsFrom(vehiclePower, 233, Arrays.asList(car));
		assert !car233.isAlive();
		assert v233.isAlive();
		assert !car233.isAncestorOf(v233);
		assert !car233.computeDependencies().contains(v233);
	}
}
