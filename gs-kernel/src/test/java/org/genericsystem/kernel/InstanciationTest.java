package org.genericsystem.kernel;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.genericsystem.api.exception.ExistsException;
import org.testng.annotations.Test;

@Test
public class InstanciationTest extends AbstractTest {

	public void test001_Root_constructor() {
		Root root = new Root();
		assert root.getMeta().equals(root);
		assert root.getComposites().isEmpty();
		assert Statics.ENGINE_VALUE.equals(root.getValue());
		assert root.isAlive();
		assert root.isMeta();
	}

	public void test002_addInstance_root() {
		Root root = new Root();
		Vertex car = root.addInstance("Car");
		assert car.isThrowExistException();
		assert root.getInstance("Car") == car;
		assert car.getMeta().equals(root);
		assert car.getSupers().isEmpty();
		assert car.getComposites().isEmpty();
		assert "Car".equals(car.getValue());
		assert car.isAlive();
		assert car.isStructural();
		assert car.isInstanceOf(root);
		assert !car.inheritsFrom(root);
	}

	public void test002_setInstance_root() {
		Root root = new Root();
		Vertex car = root.setInstance("Car");
		assert !car.isThrowExistException();
		assert root.getInstance("Car") == car;
		assert car.getMeta().equals(root);
		assert car.getSupers().isEmpty();
		assert car.getComposites().isEmpty();
		assert "Car".equals(car.getValue());
		assert car.isAlive();
		assert car.isStructural();
		assert car.isInstanceOf(root);
		assert !car.inheritsFrom(root);
	}

	public void test003_addInstance_2instances() {
		Root root = new Root();
		Vertex car = root.addInstance("Car");
		Vertex robot = root.addInstance("Robot");
		assert car.getMeta().equals(root);
		assert car.getSupers().isEmpty();
		assert car.getComposites().isEmpty();
		assert "Car".equals(car.getValue());
		assert car.isAlive();
		assert car.isStructural();
		assert car.isInstanceOf(root);
		assert !car.inheritsFrom(root);
		assert robot.getMeta().equals(root);
		assert robot.getSupers().isEmpty();
		assert robot.getComposites().isEmpty();
		assert "Robot".equals(robot.getValue());
		assert robot.isAlive();
		assert robot.isStructural();
		assert robot.isInstanceOf(root);
		assert !robot.inheritsFrom(root);
	}

	public void test004_addInstance_sameValueParameter() {
		Root root = new Root();
		Vertex car = root.addInstance("Car");
		catchAndCheckCause(() -> root.addInstance("Car"), ExistsException.class);
	}

	public void test005_setInstance_exisitingInstance() {
		Root root = new Root();
		Vertex car = root.addInstance("Car");
		Vertex car2 = root.setInstance("Car");
		assert car2.isThrowExistException();
		assert car == car2;
		assert car.getMeta().equals(root);
		assert car.getSupers().isEmpty();
		assert car.getComposites().isEmpty();
		assert "Car".equals(car.getValue());
		assert car.isAlive();
		assert car.isStructural();
		assert car.isInstanceOf(root);
		assert !car.inheritsFrom(root);
	}

	public void test006_addInstance_override() {
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex car = root.addInstance(Arrays.asList(vehicle), "Car");
		assert vehicle.getMeta().equals(root);
		assert car.getMeta().equals(root);
		assert root.getSupers().isEmpty();
		assert vehicle.getSupers().isEmpty();
		assert car.getSupers().size() == 1;
		assert car.isInstanceOf(root);
		assert !car.inheritsFrom(root);
		assert car.inheritsFrom(vehicle);
		assert !car.isInstanceOf(vehicle);
		assert !vehicle.isInstanceOf(car);
		assert root.isAlive();
		assert vehicle.isAlive();
		assert car.isAlive();
	}

	public void test007_addInstance_selfInheriting() {
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		catchAndCheckCause(() -> root.addInstance(vehicle, "Vehicle"), ExistsException.class);
	}

	public void test008_addInstance_multipleOverrides() {
		Root root = new Root();
		Vertex car = root.addInstance("Car");
		Vertex robot = root.addInstance("Robot");
		Vertex transformer = root.addInstance(Arrays.asList(car, robot), "Transformer");
		assert transformer.isThrowExistException();
		assert car.getMeta().equals(root);
		assert robot.getMeta().equals(root);
		assert transformer.getMeta().equals(root);
		assert root.getSupers().isEmpty();
		assert car.getSupers().isEmpty();
		assert robot.getSupers().isEmpty();
		assert transformer.getSupers().size() == 2;
		assert transformer.getSupers().stream().anyMatch(car::equals); // isAlive test
		assert transformer.getSupers().stream().anyMatch(robot::equals);
		assert car.getComposites().isEmpty();
		assert robot.getComposites().isEmpty();
		assert transformer.getComposites().isEmpty();
		assert root.isAlive();
		assert car.isAlive();
		assert robot.isAlive();
		assert transformer.isAlive();
	}

	public void test008_setInstance_multipleOverrides() {
		Root root = new Root();
		Vertex car = root.addInstance("Car");
		Vertex robot = root.addInstance("Robot");
		Vertex transformer = root.setInstance(Arrays.asList(car, robot), "Transformer");
		assert !transformer.isThrowExistException();
		assert car.getMeta().equals(root);
		assert robot.getMeta().equals(root);
		assert transformer.getMeta().equals(root);
		assert root.getSupers().isEmpty();
		assert car.getSupers().isEmpty();
		assert robot.getSupers().isEmpty();
		assert transformer.getSupers().size() == 2;
		assert transformer.getSupers().stream().anyMatch(car::equals); // isAlive test
		assert transformer.getSupers().stream().anyMatch(robot::equals);
		assert car.getComposites().isEmpty();
		assert robot.getComposites().isEmpty();
		assert transformer.getComposites().isEmpty();
		assert root.isAlive();
		assert car.isAlive();
		assert robot.isAlive();
		assert transformer.isAlive();
	}

	public void test009_addInstance_multipleOverrides() {
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex car = root.addInstance(Arrays.asList(vehicle), "Car");
		Vertex device = root.addInstance("Device");
		Vertex robot = root.addInstance(Arrays.asList(device), "Robot");
		Vertex transformer = root.addInstance(Arrays.asList(car, robot), "Transformer");
		assert car.getMeta().equals(root);
		assert vehicle.getMeta().equals(root);
		assert device.getMeta().equals(root);
		assert robot.getMeta().equals(root);
		assert transformer.getMeta().equals(root);
		assert root.getSupers().isEmpty();
		assert vehicle.getSupers().isEmpty();
		assert car.getSupers().size() == 1;
		assert device.getSupers().isEmpty();
		assert robot.getSupers().size() == 1;
		assert transformer.getSupers().size() == 2;
		assert transformer.getSupers().stream().anyMatch(car::equals);
		assert transformer.getSupers().stream().anyMatch(robot::equals);
		car.getSupers().stream().anyMatch(vehicle::equals);
		robot.getSupers().stream().anyMatch(device::equals);
		Predicate<Vertex> condition = x -> Statics.concat(transformer.getSupers().stream(), superVertex -> Stream.concat(Stream.of(superVertex), superVertex.getSupers().stream())).anyMatch(x::equals);
		assert condition.test(vehicle);
		assert condition.test(car);
		assert condition.test(robot);
		assert condition.test(device);
		assert root.isAlive();
		assert vehicle.isAlive();
		assert car.isAlive();
		assert device.isAlive();
		assert robot.isAlive();
		assert transformer.isAlive();
	}

	public void test010_addInstance_multipleOverrides() {
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex car = root.addInstance(Arrays.asList(vehicle), "Car");
		Vertex device = root.addInstance("Device");
		Vertex robot = root.addInstance(Arrays.asList(device), "Robot");
		Vertex transformer = root.addInstance(Arrays.asList(car, robot), "Transformer");
		Vertex transformer2 = root.addInstance(Arrays.asList(transformer), "Transformer2");
		assert car.getMeta().equals(root);
		assert vehicle.getMeta().equals(root);
		assert device.getMeta().equals(root);
		assert robot.getMeta().equals(root);
		assert transformer.getMeta().equals(root);
		assert transformer2.getMeta().equals(root);
		assert root.getSupers().isEmpty();
		assert vehicle.getSupers().isEmpty();
		assert car.getSupers().size() == 1;
		assert device.getSupers().isEmpty();
		assert robot.getSupers().size() == 1;
		assert transformer.getSupers().size() == 2;
		assert transformer.getSupers().stream().anyMatch(car::equals);
		assert transformer.getSupers().stream().anyMatch(robot::equals);
		car.getSupers().stream().anyMatch(vehicle::equals);
		robot.getSupers().stream().anyMatch(device::equals);
		Predicate<Vertex> condition = x -> Statics.concat(transformer.getSupers().stream(), superVertex -> Stream.concat(Stream.of(superVertex), superVertex.getSupers().stream())).anyMatch(x::equals);
		assert condition.test(vehicle);
		assert condition.test(car);
		assert condition.test(robot);
		assert condition.test(device);
		assert root.isAlive();
		assert vehicle.isAlive();
		assert car.isAlive();
		assert device.isAlive();
		assert robot.isAlive();
		assert transformer.isAlive();
	}
}
