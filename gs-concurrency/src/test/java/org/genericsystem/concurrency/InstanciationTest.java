package org.genericsystem.concurrency;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.genericsystem.api.exception.ExistsException;
import org.genericsystem.kernel.Statics;
import org.testng.annotations.Test;

@Test
public class InstanciationTest extends AbstractTest {

	public void test001_Engine_constructor() {
		Engine Engine = new Engine();
		assert Engine.getMeta().equals(Engine);
		assert Engine.getSupers().isEmpty();
		assert Engine.getComposites().size() == 0;
		assert Statics.ENGINE_VALUE.equals(Engine.getValue());
		assert Engine.isAlive();
		assert Engine.isMeta();
	}

	public void test002_addInstance_Engine() {
		Engine Engine = new Engine();
		Generic car = Engine.addInstance("Car");
		assert car.isThrowExistException();
		assert Engine.getInstance("Car") == car;
		assert car.getMeta().equals(Engine);
		assert car.getSupers().isEmpty();
		assert car.getComposites().isEmpty();
		assert "Car".equals(car.getValue());
		assert car.isAlive();
		assert car.isStructural();
		assert car.isInstanceOf(Engine);
		assert !car.inheritsFrom(Engine);
	}

	public void test002_setInstance_Engine() {
		Engine Engine = new Engine();
		Generic car = Engine.setInstance("Car");
		assert !car.isThrowExistException();
		assert Engine.getInstance("Car") == car;
		assert car.getMeta().equals(Engine);
		assert car.getSupers().isEmpty();
		assert car.getComposites().isEmpty();
		assert "Car".equals(car.getValue());
		assert car.isAlive();
		assert car.isStructural();
		assert car.isInstanceOf(Engine);
		assert !car.inheritsFrom(Engine);
	}

	public void test003_addInstance_2instances() {
		Engine Engine = new Engine();
		Generic car = Engine.addInstance("Car");
		Generic robot = Engine.addInstance("Robot");
		assert car.getMeta().equals(Engine);
		assert car.getSupers().isEmpty();
		assert car.getComposites().isEmpty();
		assert "Car".equals(car.getValue());
		assert car.isAlive();
		assert car.isStructural();
		assert car.isInstanceOf(Engine);
		assert !car.inheritsFrom(Engine);
		assert robot.getMeta().equals(Engine);
		assert robot.getSupers().isEmpty();
		assert robot.getComponents().isEmpty();
		assert "Robot".equals(robot.getValue());
		assert robot.isAlive();
		assert robot.isStructural();
		assert robot.isInstanceOf(Engine);
		assert !robot.inheritsFrom(Engine);
	}

	public void test004_addInstance_sameValueParameter() {
		Engine Engine = new Engine();
		Generic car = Engine.addInstance("Car");
		new RollbackCatcher() {

			@Override
			public void intercept() {
				Generic car2 = Engine.addInstance("Car");
			}
		}.assertIsCausedBy(ExistsException.class);
	}

	public void test005_setInstance_exisitingInstance() {
		Engine Engine = new Engine();
		Generic car = Engine.addInstance("Car");
		Generic car2 = Engine.setInstance("Car");
		assert car2.isThrowExistException();
		assert car == car2;
		assert car.getMeta().equals(Engine);
		assert car.getSupers().isEmpty();
		assert car.getComposites().isEmpty();
		assert "Car".equals(car.getValue());
		assert car.isAlive();
		assert car.isStructural();
		assert car.isInstanceOf(Engine);
		assert !car.inheritsFrom(Engine);
	}

	public void test006_addInstance_override() {
		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic car = Engine.addInstance(Arrays.asList(vehicle), "Car");
		assert vehicle.getMeta().equals(Engine);
		assert car.getMeta().equals(Engine);
		assert Engine.getSupers().isEmpty();
		assert vehicle.getSupers().isEmpty();
		assert car.getSupers().size() == 1;
		assert car.isInstanceOf(Engine);
		assert !car.inheritsFrom(Engine);
		assert car.inheritsFrom(vehicle);
		assert !car.isInstanceOf(vehicle);
		assert !vehicle.isInstanceOf(car);
		assert Engine.isAlive();
		assert vehicle.isAlive();
		assert car.isAlive();
	}

	public void test007_addInstance_selfInheriting() {
		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		new RollbackCatcher() {

			@Override
			public void intercept() {
				Engine.addInstance(Arrays.asList(vehicle), "Vehicle");

			}
		}.assertIsCausedBy(ExistsException.class);
	}

	public void test008_addInstance_multipleOverrides() {
		Engine Engine = new Engine();
		Generic car = Engine.addInstance("Car");
		Generic robot = Engine.addInstance("Robot");
		Generic transformer = Engine.addInstance(Arrays.asList(car, robot), "Transformer");
		assert transformer.isThrowExistException();
		assert car.getMeta().equals(Engine);
		assert robot.getMeta().equals(Engine);
		assert transformer.getMeta().equals(Engine);
		assert Engine.getSupers().isEmpty();
		assert car.getSupers().isEmpty();
		assert robot.getSupers().isEmpty();
		assert transformer.getSupers().size() == 2;
		assert transformer.getSupers().stream().anyMatch(car::equals); // isAlive test
		assert transformer.getSupers().stream().anyMatch(robot::equals);
		assert car.getComposites().isEmpty();
		assert robot.getComposites().isEmpty();
		assert transformer.getComposites().isEmpty();
		assert Engine.isAlive();
		assert car.isAlive();
		assert robot.isAlive();
		assert transformer.isAlive();
	}

	public void test008_setInstance_multipleOverrides() {
		Engine Engine = new Engine();
		Generic car = Engine.addInstance("Car");
		Generic robot = Engine.addInstance("Robot");
		Generic transformer = Engine.setInstance(Arrays.asList(car, robot), "Transformer");
		assert !transformer.isThrowExistException();
		assert car.getMeta().equals(Engine);
		assert robot.getMeta().equals(Engine);
		assert transformer.getMeta().equals(Engine);
		assert Engine.getSupers().isEmpty();
		assert car.getSupers().isEmpty();
		assert robot.getSupers().isEmpty();
		assert transformer.getSupers().size() == 2;
		assert transformer.getSupers().stream().anyMatch(car::equals); // isAlive test
		assert transformer.getSupers().stream().anyMatch(robot::equals);
		assert car.getComposites().isEmpty();
		assert robot.getComposites().isEmpty();
		assert transformer.getComposites().isEmpty();
		assert Engine.isAlive();
		assert car.isAlive();
		assert robot.isAlive();
		assert transformer.isAlive();
	}

	public void test009_addInstance_multipleOverrides() {
		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic car = Engine.addInstance(Arrays.asList(vehicle), "Car");
		Generic device = Engine.addInstance("Device");
		Generic robot = Engine.addInstance(Arrays.asList(device), "Robot");
		Generic transformer = Engine.addInstance(Arrays.asList(car, robot), "Transformer");
		assert car.getMeta().equals(Engine);
		assert vehicle.getMeta().equals(Engine);
		assert device.getMeta().equals(Engine);
		assert robot.getMeta().equals(Engine);
		assert transformer.getMeta().equals(Engine);
		assert Engine.getSupers().isEmpty();
		assert vehicle.getSupers().isEmpty();
		assert car.getSupers().size() == 1;
		assert device.getSupers().isEmpty();
		assert robot.getSupers().size() == 1;
		assert transformer.getSupers().size() == 2;
		assert transformer.getSupers().stream().anyMatch(car::equals);
		assert transformer.getSupers().stream().anyMatch(robot::equals);
		car.getSupers().stream().anyMatch(vehicle::equals);
		robot.getSupers().stream().anyMatch(device::equals);
		Predicate<Generic> condition = x -> Statics.concat(transformer.getSupers().stream(), superGeneric -> Stream.concat(Stream.of(superGeneric), superGeneric.getSupers().stream())).anyMatch(x::equals);
		assert condition.test(vehicle);
		assert condition.test(car);
		assert condition.test(robot);
		assert condition.test(device);
		assert Engine.isAlive();
		assert vehicle.isAlive();
		assert car.isAlive();
		assert device.isAlive();
		assert robot.isAlive();
		assert transformer.isAlive();
	}

	public void test010_addInstance_multipleOverrides() {
		Engine Engine = new Engine();
		Generic vehicle = Engine.addInstance("Vehicle");
		Generic car = Engine.addInstance(Arrays.asList(vehicle), "Car");
		Generic device = Engine.addInstance("Device");
		Generic robot = Engine.addInstance(Arrays.asList(device), "Robot");
		Generic transformer = Engine.addInstance(Arrays.asList(car, robot), "Transformer");
		Generic transformer2 = Engine.addInstance(Arrays.asList(transformer), "Transformer2");
		assert car.getMeta().equals(Engine);
		assert vehicle.getMeta().equals(Engine);
		assert device.getMeta().equals(Engine);
		assert robot.getMeta().equals(Engine);
		assert transformer.getMeta().equals(Engine);
		assert transformer2.getMeta().equals(Engine);
		assert Engine.getSupers().isEmpty();
		assert vehicle.getSupers().isEmpty();
		assert car.getSupers().size() == 1;
		assert device.getSupers().isEmpty();
		assert robot.getSupers().size() == 1;
		assert transformer.getSupers().size() == 2;
		assert transformer.getSupers().stream().anyMatch(car::equals);
		assert transformer.getSupers().stream().anyMatch(robot::equals);
		car.getSupers().stream().anyMatch(vehicle::equals);
		robot.getSupers().stream().anyMatch(device::equals);
		Predicate<Generic> condition = x -> Statics.concat(transformer.getSupers().stream(), superGeneric -> Stream.concat(Stream.of(superGeneric), superGeneric.getSupers().stream())).anyMatch(x::equals);
		assert condition.test(vehicle);
		assert condition.test(car);
		assert condition.test(robot);
		assert condition.test(device);
		assert Engine.isAlive();
		assert vehicle.isAlive();
		assert car.isAlive();
		assert device.isAlive();
		assert robot.isAlive();
		assert transformer.isAlive();
	}
}
