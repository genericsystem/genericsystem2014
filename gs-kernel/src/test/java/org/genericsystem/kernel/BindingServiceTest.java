package org.genericsystem.kernel;

import java.util.Arrays;
import org.genericsystem.api.exception.ExistsException;
import org.genericsystem.api.exception.SingularConstraintViolationException;
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

		catchAndCheckCause(() -> engine.addInstance("Vehicle"), ExistsException.class);
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

	public void test000_NoInheritanceFromType() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex power = engine.addInstance("Power");

		Vertex myBmw = vehicle.addInstance("myBmw");

		assert myBmw.getAttributes(power).size() == 0 : myBmw.getAttributes(power).info();
	}

	public void test000_InheritanceFromType() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex power = engine.addInstance("Power");
		Vertex vehiclePower = vehicle.addAttribute(power, "VehiclePower");

		Vertex myBmw = vehicle.addInstance("myBmw");
		Vertex v233 = myBmw.addHolder(vehiclePower, 233);

		assert myBmw.getHolders(vehiclePower).contains(v233);
		assert myBmw.getHolders(vehiclePower).size() == 1;

		assert myBmw.getHolders(power).contains(v233);
		assert myBmw.getHolders(power).size() == 1;

	}

	public void test001InheritanceFromType() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex power = engine.addInstance("Power");
		Vertex vehiclePower = vehicle.addAttribute(power, "VehiclePower");

		Vertex myBmw = vehicle.addInstance("myBmw");
		Vertex v233 = myBmw.addHolder(power, 233);

		assert myBmw.getHolders(vehiclePower).contains(v233);
		assert myBmw.getHolders(vehiclePower).size() == 1;

		assert myBmw.getHolders(power).contains(v233);
		assert myBmw.getHolders(power).size() == 1;

	}

	public void test002_InheritanceFromType() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex singular = engine.addInstance("Singular");
		singular.enableSingularConstraint(Statics.BASE_POSITION);
		assert singular.isSingularConstraintEnabled(Statics.BASE_POSITION);

		Vertex vehiclePower = vehicle.addAttribute(singular, "Power");
		assert vehiclePower.isSingularConstraintEnabled(Statics.BASE_POSITION);
		Vertex myBmw = vehicle.addInstance("myBmw");

		Vertex v233 = myBmw.addHolder(vehiclePower, 233);
		assert myBmw.getHolders(vehiclePower).contains(v233);
		assert myBmw.getHolders(vehiclePower).size() == 1;
		catchAndCheckCause(() -> myBmw.addHolder(vehiclePower, 234), SingularConstraintViolationException.class);
	}

}
