package org.genercisystem.impl;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.genericsystem.impl.Engine;
import org.genericsystem.impl.Generic;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.exceptions.ExistsException;
import org.testng.annotations.Test;

@Test
public class InstanciationTest extends AbstractTest {

	public void testEngineInstanciation() {
		Engine engine = new Engine();
		assert engine.getMeta().equals(engine);
		assert engine.getSupersStream().count() == 0;
		assert engine.getComponentsStream().count() == 0;
		assert Statics.ENGINE_VALUE.equals(engine.getValue());
		assert engine.isAlive();
		assert engine.isMeta();
	}

	public void testTypeInstanciation() {
		Engine engine = new Engine();
		Generic car = engine.addInstance("Car");

		assert car.getMeta().equals(engine);
		assert car.getSupersStream().count() == 0;
		assert car.getComponentsStream().count() == 0;
		assert "Car".equals(car.getValue());
		assert car.isAlive();
		assert car.isStructural();
		assert car.isInstanceOf(engine);
		assert !car.inheritsFrom(engine);
	}

	public void testTwoTypeInstanciationDifferentNames() {
		Engine engine = new Engine();
		Generic car = engine.addInstance("Car");
		Generic robot = engine.addInstance("Robot");

		assert car.getMeta().equals(engine);
		assert car.getSupersStream().count() == 0;
		assert car.getComponentsStream().count() == 0;
		assert "Car".equals(car.getValue());
		assert car.isAlive();
		assert car.isStructural();
		assert car.isInstanceOf(engine);
		assert !car.inheritsFrom(engine);

		assert robot.getMeta().equals(engine);
		assert robot.getSupersStream().count() == 0;
		assert robot.getComponentsStream().count() == 0;
		assert "Robot".equals(robot.getValue());
		assert robot.isAlive();
		assert robot.isStructural();
		assert robot.isInstanceOf(engine);
		assert !robot.inheritsFrom(engine);
	}

	public void testTwoTypeInstanciationSameNamesAddInstance() {
		Engine engine = new Engine();
		engine.addInstance("Car");

		new RollbackCatcher() {

			@Override
			public void intercept() {
				engine.addInstance("Car");
			}
		}.assertIsCausedBy(ExistsException.class);
	}

	public void testTwoTypeInstanciationSameNamesSetInstance() {
		Engine engine = new Engine();
		Generic car = engine.addInstance("Car");
		Generic car2 = engine.setInstance("Car");

		// log.info(engine.info());
		// log.info(car.info());
		// log.info(car2.info());

		assert car.equals(car2);
		assert car.getMeta().equals(engine);
		assert car.getSupersStream().count() == 0;
		assert car.getComponentsStream().count() == 0;
		assert "Car".equals(car.getValue());
		assert car.isAlive();
		assert car.isStructural();
		assert car.isInstanceOf(engine);
		assert !car.inheritsFrom(engine);

	}

	public void testTwoTypeInstanciationWithInheritance() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(Arrays.asList(vehicle), "Car");
		// log.info(engine.info());
		// log.info(vehicle.info());
		// log.info(car.info());

		assert vehicle.getMeta().equals(engine);
		assert car.getMeta().equals(engine);

		assert engine.getSupersStream().count() == 0;
		assert vehicle.getSupersStream().count() == 0;
		assert car.getSupersStream().count() == 1;

		assert car.isInstanceOf(engine);
		assert !car.inheritsFrom(engine);

		assert car.inheritsFrom(vehicle);
		assert !car.isInstanceOf(vehicle);
		assert !vehicle.isInstanceOf(car);

		// isAlive test
		assert engine.isAlive();
		assert vehicle.isAlive();
		assert car.isAlive();

	}

	public void testTypeInstanciationWithSelfInheritance() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		// log.info(vehicle.info());
		new RollbackCatcher() {

			@Override
			public void intercept() {
				engine.addInstance(Arrays.asList(vehicle), "Vehicle");

			}
		}.assertIsCausedBy(IllegalStateException.class);
	}

	public void test3TypeInstanciationWithMultipleInheritence() {
		Engine engine = new Engine();
		Generic car = engine.addInstance("Car");
		Generic robot = engine.addInstance("Robot");
		Generic transformer = engine.addInstance(Arrays.asList(car, robot), "Transformer");

		// log.info(car.info());
		// log.info(robot.info());
		// log.info(transformer.info());

		assert car.getMeta().equals(engine);
		assert robot.getMeta().equals(engine);
		assert transformer.getMeta().equals(engine);

		assert engine.getSupersStream().count() == 0;
		assert car.getSupersStream().count() == 0;
		assert robot.getSupersStream().count() == 0;
		assert transformer.getSupersStream().count() == 2;

		assert transformer.getSupersStream().anyMatch(car::equals); // isAlive test
		assert transformer.getSupersStream().anyMatch(robot::equals);
		//
		assert car.getComponentsStream().count() == 0;
		assert robot.getComponentsStream().count() == 0;
		assert transformer.getComponentsStream().count() == 0;
		//
		assert engine.isAlive();
		assert car.isAlive();
		assert robot.isAlive();
		assert transformer.isAlive();

	}

	public void test5TypeInstanciationWithMultipleInheritence() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(Arrays.asList(vehicle), "Car");
		Generic device = engine.addInstance("Device");
		Generic robot = engine.addInstance(Arrays.asList(device), "Robot");
		Generic transformer = engine.addInstance(Arrays.asList(car, robot), "Transformer");

		// log.info(vehicle.info());
		// log.info(car.info());
		// log.info(device.info());
		// log.info(robot.info());
		// log.info(transformer.info());

		assert car.getMeta().equals(engine);
		assert vehicle.getMeta().equals(engine);
		assert device.getMeta().equals(engine);
		assert robot.getMeta().equals(engine);
		assert transformer.getMeta().equals(engine);

		assert engine.getSupersStream().count() == 0;
		assert vehicle.getSupersStream().count() == 0;
		assert car.getSupersStream().count() == 1;
		assert device.getSupersStream().count() == 0;
		assert robot.getSupersStream().count() == 1;
		assert transformer.getSupersStream().count() == 2;

		assert transformer.getSupersStream().anyMatch(car::equals);
		assert transformer.getSupersStream().anyMatch(robot::equals);

		car.getSupersStream().anyMatch(vehicle::equals);
		robot.getSupersStream().anyMatch(device::equals);
		Statics.concat(transformer.getSupersStream(), superGeneric -> Stream.concat(Stream.of(superGeneric), superGeneric.getSupersStream()));

		final Predicate<Generic> condition = x -> Statics.concat(transformer.getSupersStream(), superGeneric -> Stream.concat(Stream.of(superGeneric), superGeneric.getSupersStream())).anyMatch(x::equals);

		assert condition.test(vehicle);
		assert condition.test(car);
		assert condition.test(robot);
		assert condition.test(device);

		assert engine.isAlive();
		assert vehicle.isAlive();
		assert car.isAlive();
		assert device.isAlive();
		assert robot.isAlive();
		assert transformer.isAlive();
	}

	public void test6TypeInstanciationWithMultipleInheritence() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(Arrays.asList(vehicle), "Car");
		Generic device = engine.addInstance("Device");
		Generic robot = engine.addInstance(Arrays.asList(device), "Robot");
		Generic transformer = engine.addInstance(Arrays.asList(car, robot), "Transformer");
		Generic transformer2 = engine.addInstance(Arrays.asList(transformer), "Transformer2");

		// log.info(vehicle.info());
		// log.info(car.info());
		// log.info(device.info());
		// log.info(robot.info());
		// log.info(transformer.info());

		assert car.getMeta().equals(engine);
		assert vehicle.getMeta().equals(engine);
		assert device.getMeta().equals(engine);
		assert robot.getMeta().equals(engine);
		assert transformer.getMeta().equals(engine);
		assert transformer2.getMeta().equals(engine);

		assert engine.getSupersStream().count() == 0;
		assert vehicle.getSupersStream().count() == 0;
		assert car.getSupersStream().count() == 1;
		assert device.getSupersStream().count() == 0;
		assert robot.getSupersStream().count() == 1;
		assert transformer.getSupersStream().count() == 2;

		assert transformer.getSupersStream().anyMatch(car::equals);
		assert transformer.getSupersStream().anyMatch(robot::equals);

		car.getSupersStream().anyMatch(vehicle::equals);
		robot.getSupersStream().anyMatch(device::equals);
		Statics.concat(transformer.getSupersStream(), superGeneric -> Stream.concat(Stream.of(superGeneric), superGeneric.getSupersStream()));

		final Predicate<Generic> condition = x -> Statics.concat(transformer.getSupersStream(), superGeneric -> Stream.concat(Stream.of(superGeneric), superGeneric.getSupersStream())).anyMatch(x::equals);

		assert condition.test(vehicle);
		assert condition.test(car);
		assert condition.test(robot);
		assert condition.test(device);

		assert engine.isAlive();
		assert vehicle.isAlive();
		assert car.isAlive();
		assert device.isAlive();
		assert robot.isAlive();
		assert transformer.isAlive();
	}
}
