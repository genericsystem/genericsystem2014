package org.genericsystem.kernel;

import java.util.Arrays;
import java.util.Collections;

import org.genericsystem.kernel.exceptions.NotAliveException;
import org.testng.annotations.Test;

@Test
public class VertexTest extends AbstractTest {

	public void test001_getInheritings() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");

		assert vehicle.getInheritings().stream().anyMatch(car::equals);
	}

	public void test001_getInstances() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		assert engine.getInstances().stream().anyMatch(g -> g.equals(vehicle));
	}

	public void test001_getMetaComposites() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex powerVehicle = engine.addInstance("power", vehicle);
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex myVehicle123 = powerVehicle.addInstance("123", myVehicle);

		assert myVehicle.getMetaComposites(powerVehicle).stream().anyMatch(g -> g.equals(myVehicle123));
	}

	public void test001_getSuperComposites() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex powerVehicle = engine.addInstance("power", vehicle);
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex vehicle256 = powerVehicle.addInstance("256", vehicle);
		Vertex myVehicle123 = powerVehicle.addInstance(vehicle256, "123", myVehicle);

		assert myVehicle.getSuperComposites(vehicle256).contains(myVehicle123);
	}

	public void test002_getSuperComposites() {
		Root engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex powerVehicle = engine.addInstance("power", vehicle);
		powerVehicle.enablePropertyConstraint();
		Vertex myVehicle = vehicle.addInstance("myVehicle");
		Vertex vehicle256 = powerVehicle.addInstance("256", vehicle);
		Vertex myVehicle123 = powerVehicle.addInstance("123", myVehicle);

		assert myVehicle.getSuperComposites(vehicle256).contains(myVehicle123);
	}

	public void test() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex vehicle2 = engine.addInstance("Vehicle2");
		assert !vehicle2.inheritsFrom(vehicle);
		assert vehicle == engine.setInstance("Vehicle");
		// assert vehicle == engine.setInstance(new Vertex[] { vehicle2 }, "Vehicle");
		Vertex car = engine.addInstance(Arrays.asList(vehicle), "Car");
		assert car.inheritsFrom(vehicle);
		Vertex power = engine.addInstance("Power", car);
		Vertex myBmw = car.addInstance("myBmw");
		assert myBmw.isInstanceOf(car);
		Vertex v233 = power.addInstance(233, myBmw);
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("red");
		Vertex green = color.addInstance("green");
		Vertex yellow = color.addInstance("yellow");
		assert !yellow.getSupersStream().anyMatch(red::equals);
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		assert engine.getInstances().containsAll(Arrays.asList(vehicle, car));
		assert car.getInstances().contains(myBmw) : car.getInstances() + car.info();
		assert power.getInstances().contains(v233);
		assert car.getMetaComposites(power.getMeta()).contains(power);
		assert car.getSupersStream().findFirst().get() == vehicle : car.getSupersStream().findFirst().get().info();
		assert car.getSupersStream().anyMatch(vehicle::equals);
		assert vehicle.getInheritings().contains(car);
		assert myBmw.getMetaComposites(v233.getMeta()).contains(v233);
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
		assert engine.getInstance("Car") != null;
		assert power.getInstance(233, myBmw) != null;
		Vertex carRed = vehicleColor.addInstance("CarRed", car, red);
		Vertex carGreen = vehicleColor.addInstance("CarGreen", car, green);
		assert carRed.isSuperOf(vehicleColor, Arrays.asList(carRed), "myBmwRed", Arrays.asList(myBmw, red));
		assert !carRed.isSuperOf(vehicleColor, Collections.emptyList(), "myBmwRed", Arrays.asList(myBmw, red));
		assert carRed.isSuperOf(vehicleColor, Collections.singletonList(carRed), "CarRed", Arrays.asList(myBmw, red));
		assert carGreen.isInstanceOf(vehicleColor);
		assert vehicleColor.getInstances().contains(carGreen);

		Vertex myBmwYellow = vehicleColor.addInstance(carGreen, "CarRed", myBmw, yellow);
		assert carGreen.isSuperOf(vehicleColor, Collections.singletonList(carGreen), "CarRed", Arrays.asList(myBmw, yellow));
		assert myBmwYellow.inheritsFrom(carGreen);

		assert carRed.isSuperOf(vehicleColor, Collections.singletonList(carGreen), "CarRed", Arrays.asList(myBmw, red));
		Vertex myBmwRed = vehicleColor.addInstance(carRed, "myBmwRed", myBmw, red);
		assert vehicleColor.getInstances().contains(myBmwRed);
		assert myBmwRed.inheritsFrom(carRed);
		assert !yellow.inheritsFrom(red);
		assert !yellow.isInstanceOf(red);
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
		assert engine.isAttributeOf(myBmw);

		// assert car.getAttributes(engine).equals(myBmw.getAttributes(engine)) : car.getAttributes(engine) + " " + myBmw.getAttributes(engine);
	}

	@Test(enabled = false)
	public void test2() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex vehicle2 = engine.addInstance("Vehicle2");
		assert vehicle == engine.setInstance("Vehicle");
		// assert vehicle != engine.setInstance(vehicle2, "Vehicle");
	}

	public void test3() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex vehiclePower = engine.addInstance("VehiclePower", vehicle);
		Vertex carPower = engine.addInstance("CarPower", car);
		assert car.getAttributes(engine).containsAll(Arrays.asList(vehiclePower, carPower)) : car.getAttributes(engine);
		// assert car.getAttributes(engine).size() == 2;
	}

	public void test4() {
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
		assert !car233.isAlive();
		assert v233.isAlive();
		assert !car233.isAncestorOf(v233);
		assert v233.dependsFrom(vehiclePower, 233, Arrays.asList(car));
		assert vehiclePower.computePotentialDependencies(233, Arrays.asList(car)).contains(v233);
	}
}
