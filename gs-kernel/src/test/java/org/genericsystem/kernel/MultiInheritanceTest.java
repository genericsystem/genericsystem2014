package org.genericsystem.kernel;

import java.util.Arrays;

import org.testng.annotations.Test;

@Test
public class MultiInheritanceTest extends AbstractTest {

	public void multiInheritance() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex vehicleSizable = engine.addInstance("Sizable", vehicle);
		Vertex robot = engine.addInstance("Robot");
		Vertex robotSizable = engine.addInstance("Sizable", robot);
		Vertex transformer = engine.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		// assert transformer.getAttributes(engine).size() == 2;
		assert transformer.getAttributes(engine).contains(vehicleSizable);
		assert transformer.getAttributes(engine).contains(robotSizable);
		Vertex transformerSizable = engine.addInstance("Sizable", transformer);
		// assert transformer.getAttributes(engine).size() == 1 : transformer.getAttributes(engine);
		assert transformer.getAttributes(engine).contains(transformerSizable);
	}

	public void multiInheritanceWithDiamond() {
		Vertex engine = new Root();
		Vertex object = engine.addInstance("Object");
		Vertex objectSizable = engine.addInstance("Sizable", object);
		Vertex vehicle = engine.addInstance(Arrays.asList(object), "Vehicle");
		assert vehicle.inheritsFrom(object);
		Vertex vehicleSizable = engine.addInstance("Sizable", vehicle);
		assert vehicleSizable.inheritsFrom(objectSizable);
		Vertex robot = engine.addInstance(Arrays.asList(object), "Robot");
		assert robot.inheritsFrom(object);
		Vertex robotSizable = engine.addInstance("Sizable", robot);
		assert robotSizable.inheritsFrom(objectSizable);
		Vertex transformer = engine.addInstance(Arrays.asList(vehicle, robot), "Transformer");
		assert transformer.inheritsFrom(vehicle);
		assert transformer.inheritsFrom(robot);
		// assert transformer.getAttributes(engine).size() == 2;
		assert transformer.getAttributes(engine).contains(vehicleSizable);
		assert transformer.getAttributes(engine).contains(robotSizable);
		Vertex transformerSizable = engine.addInstance("Sizable", transformer);
		// assert transformer.getAttributes(engine).size() == 1;
		assert transformer.getAttributes(engine).contains(transformerSizable);
	}

	public void meta() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex vehiclePower = engine.addInstance("Power", vehicle);
		Vertex percent = engine.addInstance("Percent");
		Vertex vehiclePercent = engine.addInstance(Arrays.asList(vehiclePower), "Power", vehicle, percent);
		// assert vehicle.getAttributes(engine).size() == 0 : vehicle.getAttributes(engine);
	}
}
