package org.genericsystem.kernel;

import java.util.Arrays;
import org.genericsystem.api.exception.ExistsException;
import org.testng.annotations.Test;

@Test
public class BindingServiceTest extends AbstractTest {

	public void test001_addInstance() {
		// given
		Vertex engine = new Root();

		// when
		Vertex vehicle = engine.addInstance("Vehicle");

		// then
		assert "Vehicle".equals(vehicle.getValue());
		assert vehicle.isAlive();
	}

	public void test002_addSameValueKO() {
		// given
		Vertex engine = new Root();
		engine.addInstance("Vehicle");

		new RollbackCatcher() {
			@Override
			public void intercept() {
				// when
				engine.addInstance("Vehicle");
			}
			// then
		}.assertIsCausedBy(ExistsException.class);
	}

	public void test003_allInheritingsTest() {
		// given
		Vertex engine = new Root();
		Vertex animal = engine.addInstance("Animal");// Alone type
		Vertex machine = engine.addInstance("Machine");
		Vertex vehicle = engine.addInstance(machine, "Vehicle");
		Vertex robot = engine.addInstance(machine, "Robot");
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex bike = engine.addInstance(vehicle, "Bike");
		Vertex transformer = engine.addInstance(Arrays.asList(robot, car), "Transformer");
		Vertex plasticTransformer = engine.addInstance(transformer, "PlasticTransformer");
		// then
		assert !machine.getAllInheritings().contains(animal) : machine.getAllInheritings().info();
		assert machine.getAllInheritings().containsAll(Arrays.asList(machine, vehicle, robot, car, bike, transformer, plasticTransformer)) : machine.getAllInheritings().info();
		assert machine.getAllInheritings().size() == 7 : machine.getAllInheritings().info();

	}

	// public void test004_getInheritingsBygetAttributesTest() {
	// // given
	// Vertex engine = new Root();
	// Vertex animal = engine.addInstance("Animal");// Alone type
	// Vertex machine = engine.addInstance("Machine");
	// Vertex vehicle = engine.addInstance(machine, "Vehicle");
	// Vertex robot = engine.addInstance(machine, "Robot");
	// Vertex car = engine.addInstance(vehicle, "Car");
	// Vertex bike = engine.addInstance(vehicle, "Bike");
	// Vertex transformer = engine.addInstance(Arrays.asList(robot, car), "Transformer");
	// Vertex plasticTransformer = engine.addInstance(transformer, "PlasticTransformer");
	//
	// assert false : machine.getAttributes(engine).info();
	//
	// }

}
