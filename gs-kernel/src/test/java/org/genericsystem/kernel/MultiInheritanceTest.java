package org.genericsystem.kernel;

import java.util.Arrays;

import org.genericsystem.api.core.exceptions.ExistsException;
import org.testng.annotations.Test;

@Test
public class MultiInheritanceTest extends AbstractTest {

	public void test_multiInheritance() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic vehicleSizable = engine.addInstance("Sizable", vehicle);
		Generic robot = engine.addInstance("Robot");
		Generic robotSizable = engine.addInstance("Sizable", robot);
		Generic transformer = engine.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		// assert transformer.getAttributes(engine).size() == 2;
		assert transformer.getAttributes(engine).contains(vehicleSizable);
		assert transformer.getAttributes(engine).contains(robotSizable);
		Generic transformerSizable = engine.addInstance("Sizable", transformer);
		// assert transformer.getAttributes(engine).size() == 1 : transformer.getAttributes(engine);
		assert transformer.getAttributes(engine).contains(transformerSizable);
	}

	public void test_multiInheritanceWithDiamond() {
		Generic engine = new Root();
		Generic object = engine.addInstance("Object");
		Generic objectSizable = engine.addInstance("Sizable", object);
		Generic vehicle = engine.addInstance(Arrays.asList(object), "Vehicle");
		assert vehicle.inheritsFrom(object);
		Generic vehicleSizable = engine.addInstance("Sizable", vehicle);
		assert vehicleSizable.inheritsFrom(objectSizable);
		Generic robot = engine.addInstance(Arrays.asList(object), "Robot");
		assert robot.inheritsFrom(object);
		Generic robotSizable = engine.addInstance("Sizable", robot);
		assert robotSizable.inheritsFrom(objectSizable);
		Generic transformer = engine.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		assert transformer.inheritsFrom(vehicle);
		assert transformer.inheritsFrom(robot);
		// assert transformer.getAttributes(engine).size() == 2;
		assert transformer.getAttributes(engine).contains(vehicleSizable);
		assert transformer.getAttributes(engine).contains(robotSizable);
		Generic transformerSizable = engine.addInstance("Sizable", transformer);
		// assert transformer.getAttributes(engine).size() == 1;
		assert transformer.getAttributes(engine).contains(transformerSizable);
	}

	public void test_meta() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic vehiclePower = engine.addInstance("Power", vehicle);
		Generic percent = engine.addInstance("Percent");
		Generic vehiclePercent = engine.addInstance(Arrays.asList(vehiclePower), "Power", vehicle, percent);
		// assert vehicle.getAttributes(engine).size() == 0 : vehicle.getAttributes(engine);
	}

	public void test001_orderSupers() {
		Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic robot = engine.addInstance("Robot");
		engine.addInstance(Arrays.asList(car, robot), "Transformer");
		catchAndCheckCause(() -> engine.addInstance(Arrays.asList(robot, car), "Transformer"), ExistsException.class);
	}

	public void test002_orderSupers() {
		Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic robot = engine.addInstance("Robot");
		Generic transformer = engine.setInstance(Arrays.asList(car, robot), "Transformer");
		Generic transformer2 = engine.setInstance(Arrays.asList(robot, car), "Transformer");
		assert transformer.equals(transformer2);
	}
}
