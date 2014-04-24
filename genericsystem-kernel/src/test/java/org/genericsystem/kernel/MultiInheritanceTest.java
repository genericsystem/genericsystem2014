package org.genericsystem.kernel;

import org.testng.annotations.Test;

@Test
public class MultiInheritanceTest extends AbstractTest {

	public void multiInheritanceWithDiamond() {
		Vertex engine = new Engine();
		Vertex object = engine.addInstance("Object");
		Vertex objectSizable = engine.addInstance("Sizable", object);
		Vertex vehicle = engine.addInstance(new Vertex[] { object }, "Vehicle");
		assert vehicle.inheritsFrom(object);
		Vertex vehicleSizable = engine.addInstance("Sizable", vehicle);
		assert vehicleSizable.inheritsFrom(objectSizable);
		Vertex robot = engine.addInstance(new Vertex[] { object }, "Robot");
		assert robot.inheritsFrom(object);
		Vertex robotSizable = engine.addInstance("Sizable", robot);
		assert robotSizable.inheritsFrom(objectSizable);
		Vertex transformer = engine.addInstance(new Vertex[] { vehicle, robot }, "Transformer");
		assert transformer.inheritsFrom(vehicle);
		assert transformer.inheritsFrom(robot);
		assert transformer.getAttributes(engine).size() == 2;
		assert transformer.getAttributes(engine).contains(vehicleSizable);
		assert transformer.getAttributes(engine).contains(robotSizable);
		Vertex transformerSizable = engine.addInstance("Sizable", transformer);
		assert transformer.getAttributes(engine).size() == 1;
		assert transformer.getAttributes(engine).contains(transformerSizable);
	}

	public void multiInheritance() {
		Vertex engine = new Engine();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex vehicleSizable = engine.addInstance("Sizable", vehicle);
		Vertex robot = engine.addInstance("Robot");
		Vertex robotSizable = engine.addInstance("Sizable", robot);
		Vertex transformer = engine.addInstance(new Vertex[] { vehicle, robot }, "Transformer");
		assert transformer.getAttributes(engine).size() == 2;
		assert transformer.getAttributes(engine).contains(vehicleSizable);
		assert transformer.getAttributes(engine).contains(robotSizable);
		Vertex transformerSizable = engine.addInstance("Sizable", transformer);
		assert transformer.getAttributes(engine).size() == 1;
		assert transformer.getAttributes(engine).contains(transformerSizable);
	}
}
