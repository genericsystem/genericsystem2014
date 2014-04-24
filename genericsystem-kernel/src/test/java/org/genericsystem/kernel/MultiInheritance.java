package org.genericsystem.kernel;

import org.testng.annotations.Test;

@Test
public class MultiInheritance extends AbstractTest {

	public void multiInheritanceWithDiamond() {
		Vertex engine = new Engine();
		Vertex object = engine.addInstance("Object");
		Vertex objectSizable = engine.addInstance("Sizable", object);
		Vertex vehicle = engine.addInstance(new Vertex[] { object }, "Vehicle");
		Vertex vehicleSizable = engine.addInstance("Sizable", vehicle);
		assert vehicleSizable.inheritsFrom(objectSizable);
		Vertex robot = engine.addInstance(new Vertex[] { object }, "Robot");
		Vertex robotSizable = engine.addInstance("Sizable", robot);
		assert robotSizable.inheritsFrom(objectSizable);
		assert vehicle.inheritsFrom(object);
		// assert vehicle.inheritsFrom(engine) : vehicle.info();
		// assert robot.inheritsFrom(engine);
		assert robot.inheritsFrom(object);
		Vertex transformer = engine.addInstance(new Vertex[] { vehicle, robot }, "Transformer");
		assert transformer.inheritsFrom(vehicle);
		assert transformer.inheritsFrom(robot);
		for (Vertex v : transformer.getAttributes(engine))
			log.info("a " + v.info());
		engine.addInstance("Sizable", transformer);
		log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		for (Vertex v : transformer.getAttributes(engine))
			log.info("b " + v.info());
	}

	public void multiInheritance() {
		Vertex engine = new Engine();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex vehicleSizable = engine.addInstance("Sizable", vehicle);
		Vertex robot = engine.addInstance("Robot");
		Vertex robotSizable = engine.addInstance("Sizable", robot);
		Vertex transformer = engine.addInstance(new Vertex[] { vehicle, robot }, "Transformer");
		assert transformer.inheritsFrom(vehicle);
		assert transformer.inheritsFrom(robot);
		for (Vertex v : transformer.getAttributes(engine))
			log.info("a " + v.info());
		Vertex transformerSizable = engine.addInstance("Sizable", transformer);
		assert transformerSizable.inheritsFrom(robotSizable);
		assert transformerSizable.inheritsFrom(vehicleSizable);
		log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		for (Vertex v : transformer.getAttributes(engine))
			log.info("b " + v.info());
	}
}
