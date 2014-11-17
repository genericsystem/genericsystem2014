package org.genericsystem.concurrency;

import java.util.Arrays;

import org.testng.annotations.Test;

@Test
public class AncestorsServiceTest extends AbstractTest {

	public void isAncestorOfByInheritence() {
		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic car = Engine.addInstance(vehicle, "Car");
		Generic device = Engine.addInstance("Device");
		Generic robot = Engine.addInstance(device, "Robot");
		Generic transformer = Engine.addInstance(Arrays.asList(car, robot), "Transformer");
		Generic transformer2 = Engine.addInstance(transformer, "Transformer2");
		assert transformer.isAncestorOf(transformer2);
		assert robot.isAncestorOf(transformer);
		assert robot.isAncestorOf(transformer2);
		assert device.isAncestorOf(robot);
		assert device.isAncestorOf(transformer);
		assert device.isAncestorOf(transformer2);
		assert car.isAncestorOf(transformer);
		assert car.isAncestorOf(transformer2);
		assert vehicle.isAncestorOf(car);
		assert vehicle.isAncestorOf(transformer);
		assert vehicle.isAncestorOf(transformer2);
		assert Engine.isAncestorOf(Engine);
		assert transformer2.isAncestorOf(transformer2);
		assert transformer.isAncestorOf(transformer);
		assert vehicle.isAncestorOf(vehicle);
		assert car.isAncestorOf(car);
		assert robot.isAncestorOf(robot);
		assert device.isAncestorOf(device);
		assert Engine.isAncestorOf(device);
		assert Engine.isAncestorOf(robot);
		assert Engine.isAncestorOf(vehicle);
		assert Engine.isAncestorOf(car);
		assert Engine.isAncestorOf(transformer);
		assert Engine.isAncestorOf(transformer2);
		assert !device.isAncestorOf(car);
		assert !device.isAncestorOf(vehicle);
		assert !robot.isAncestorOf(car);
		assert !robot.isAncestorOf(vehicle);
		assert !vehicle.isAncestorOf(robot);
		assert !vehicle.isAncestorOf(device);
		assert !car.isAncestorOf(robot);
		assert !car.isAncestorOf(device);
		assert device.isAlive();
		assert robot.isAlive();
		assert vehicle.isAlive();
		assert car.isAlive();
		assert transformer.isAlive();
		assert transformer2.isAlive();
	}

	public void isAncestorOfByInheritenceSimpleConfiguration() {

		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic car = Engine.addInstance(vehicle, "Car");
		Generic microcar = Engine.addInstance(car, "Microcar");
		assert vehicle.isAncestorOf(car);
		assert vehicle.isAncestorOf(microcar);
		assert car.isAncestorOf(microcar);
		assert microcar.isAncestorOf(microcar);
		assert vehicle.isAncestorOf(vehicle);
		assert car.isAncestorOf(car);
		assert microcar.isAncestorOf(microcar);
	}

	public void isAncestorOfViaComposite() {
		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic power = Engine.addInstance("Power", vehicle);
		Generic car = Engine.addInstance(vehicle, "Car");
		Generic airConditioner = Engine.addInstance("AirConditioner", car);
		Generic microcar = Engine.addInstance(car, "microcar");
		Generic radio = Engine.addInstance("Radio", microcar);
		assert vehicle.isAncestorOf(radio);
		assert vehicle.isAncestorOf(airConditioner);
		assert vehicle.isAncestorOf(power);
		assert car.isAncestorOf(car);
		assert car.isAncestorOf(car);
		assert microcar.isAncestorOf(radio);
		assert !car.isAncestorOf(power);
		assert !microcar.isAncestorOf(power);
		assert !microcar.isAncestorOf(airConditioner);
		assert Engine.isAncestorOf(power);
		assert Engine.isAncestorOf(airConditioner);
		assert Engine.isAncestorOf(radio);
	}

	public void isAncestorOfViaComponent() {
		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic power = Engine.addInstance("Power", vehicle);
		Generic car = Engine.addInstance(vehicle, "Car");
		Generic airConditioner = Engine.addInstance("AirConditioner", car);
		Generic button = Engine.addInstance("button", airConditioner);
		Generic microcar = Engine.addInstance(car, "microcar");
		Generic radio = Engine.addInstance("Radio", microcar);
		assert vehicle.isAncestorOf(button);
		assert !microcar.isAncestorOf(button);
		assert Engine.isAncestorOf(button);
	}

	// public void test006_getInheritings_GenericAndItsSuperHaveNotSameMeta() {
	// Engine engine = new Engine();
	// Generic vehicle = engine.addInstance("Vehicle");
	// Generic vehiclePower = engine.addInstance("VehiclePower", vehicle);
	// Generic car = vehicle.addInstance("Car");
	// Generic carPower = engine.addInstance(vehiclePower, "CarPower", vehicle);
	// Generic vehicleStandard = vehicle.addInstance("VehicleStandard");
	// Generic v235 = vehiclePower.addInstance(235, vehicleStandard);
	// Generic myBmw = car.addInstance("myBmw");
	// Generic v236 = carPower.addInstance(v235, 236, myBmw);
	// }

	// public void isAncestorOfViaComposite3() {
	// Engine Engine = new Engine();
	// Generic vehicle = Engine.addInstance("Vehicle");
	// Generic car = Engine.addInstance(vehicle, "Car");
	// Generic microcar = Engine.addInstance(car, "microcar");
	//
	// Generic airConditioner = Engine.addInstance("AirConditioner", car);
	// Generic button = Engine.addInstance("button", airConditioner);
	// Generic pushButton = Engine.addInstance(button, "PushButton");
	// Generic color = Engine.addInstance("color", pushButton);
	//
	// log.info(pushButton.info());
	// assert vehicle.isAncestorOf(color);
	// assert vehicle.isAncestorOf(color);
	// assert car.isAncestorOf(color);
	// assert !microcar.isAncestorOf(color);
	// assert Engine.isAncestorOf(color);
	//
	// assert vehicle.isAncestorOf(pushButton);
	// assert !microcar.isAncestorOf(pushButton);
	// assert Engine.isAncestorOf(pushButton);
	// }
}
