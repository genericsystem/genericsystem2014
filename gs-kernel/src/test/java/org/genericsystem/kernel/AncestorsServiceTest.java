package org.genericsystem.kernel;

import java.util.Arrays;

import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Vertex;
import org.testng.annotations.Test;

@Test
public class AncestorsServiceTest extends AbstractTest {

	public void isAncestorOfByInheritence() {

		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex car = root.addInstance(vehicle, "Car");
		Vertex device = root.addInstance("Device");
		Vertex robot = root.addInstance(device, "Robot");
		Vertex transformer = root.addInstance(Arrays.asList(car, robot), "Transformer");
		Vertex transformer2 = root.addInstance(transformer, "Transformer2");

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

		assert root.isAncestorOf(root);
		assert transformer2.isAncestorOf(transformer2);
		assert transformer.isAncestorOf(transformer);
		assert vehicle.isAncestorOf(vehicle);
		assert car.isAncestorOf(car);
		assert robot.isAncestorOf(robot);
		assert device.isAncestorOf(device);

		assert root.isAncestorOf(device);
		assert root.isAncestorOf(robot);
		assert root.isAncestorOf(vehicle);
		assert root.isAncestorOf(car);
		assert root.isAncestorOf(transformer);
		assert root.isAncestorOf(transformer2);

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

		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex car = root.addInstance(vehicle, "Car");
		Vertex microcar = root.addInstance(car, "Microcar");

		assert vehicle.isAncestorOf(car);
		assert vehicle.isAncestorOf(microcar);
		assert car.isAncestorOf(microcar);
		assert microcar.isAncestorOf(microcar);

		assert vehicle.isAncestorOf(vehicle);
		assert car.isAncestorOf(car);
		assert microcar.isAncestorOf(microcar);
	}

	public void isAncestorOfViaComponent() {
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex power = root.addInstance("Power", vehicle);
		Vertex car = root.addInstance(vehicle, "Car");
		Vertex airConditioner = root.addInstance("AirConditioner", car);
		Vertex microcar = root.addInstance(car, "microcar");
		Vertex radio = root.addInstance("Radio", microcar);

		assert vehicle.isAncestorOf(radio);
		assert vehicle.isAncestorOf(airConditioner);
		assert vehicle.isAncestorOf(power);
		assert car.isAncestorOf(car);
		assert car.isAncestorOf(car);
		assert microcar.isAncestorOf(radio);

		assert !car.isAncestorOf(power);
		assert !microcar.isAncestorOf(power);
		assert !microcar.isAncestorOf(airConditioner);

		assert root.isAncestorOf(power);
		assert root.isAncestorOf(airConditioner);
		assert root.isAncestorOf(radio);
	}

	public void isAncestorOfViaComponent2() {
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex power = root.addInstance("Power", vehicle);
		Vertex car = root.addInstance(vehicle, "Car");
		Vertex airConditioner = root.addInstance("AirConditioner", car);
		Vertex button = root.addInstance("button", airConditioner);

		Vertex microcar = root.addInstance(car, "microcar");
		Vertex radio = root.addInstance("Radio", microcar);

		assert vehicle.isAncestorOf(button);
		assert !microcar.isAncestorOf(button);
		assert root.isAncestorOf(button);
	}

	// public void isAncestorOfViaComponent3() {
	// Root root = new Root();
	// Vertex vehicle = root.addInstance("Vehicle");
	// Vertex car = root.addInstance(vehicle, "Car");
	// Vertex microcar = root.addInstance(car, "microcar");
	//
	// Vertex airConditioner = root.addInstance("AirConditioner", car);
	// Vertex button = root.addInstance("button", airConditioner);
	// Vertex pushButton = root.addInstance(button, "PushButton");
	// Vertex color = root.addInstance("color", pushButton);
	//
	// log.info(pushButton.info());
	// assert vehicle.isAncestorOf(color);
	// assert vehicle.isAncestorOf(color);
	// assert car.isAncestorOf(color);
	// assert !microcar.isAncestorOf(color);
	// assert root.isAncestorOf(color);
	//
	// assert vehicle.isAncestorOf(pushButton);
	// assert !microcar.isAncestorOf(pushButton);
	// assert root.isAncestorOf(pushButton);
	// }
}
