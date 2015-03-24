package org.genericsystem.kernel;

import java.util.Arrays;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.api.core.exceptions.ExistsException;
import org.genericsystem.defaults.exceptions.SingularConstraintViolationException;
import org.testng.annotations.Test;

@Test
public class BindingServiceTest extends AbstractTest {

	public void test001_addInstance() {
		// given
		Generic engine = new Root();

		// when
		Generic vehicle = engine.addInstance("Vehicle");

		// then
		assert "Vehicle".equals(vehicle.getValue());
		assert vehicle.isAlive();
	}

	public void test002_addSameValueKO() {
		// given
		Generic engine = new Root();
		engine.addInstance("Vehicle");

		catchAndCheckCause(() -> engine.addInstance("Vehicle"), ExistsException.class);
	}

	public void test003_allInheritingsTest() {
		// given
		Generic engine = new Root();
		Generic animal = engine.addInstance("Animal");// Alone type
		Generic machine = engine.addInstance("Machine");
		Generic vehicle = engine.addInstance(machine, "Vehicle");
		Generic robot = engine.addInstance(machine, "Robot");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic bike = engine.addInstance(vehicle, "Bike");
		Generic transformer = engine.addInstance(Arrays.asList(robot, car), "Transformer");
		Generic plasticTransformer = engine.addInstance(transformer, "PlasticTransformer");
		// then
		assert !machine.getAllInheritings().contains(animal) : machine.getAllInheritings().info();
		assert machine.getAllInheritings().containsAll(Arrays.asList(machine, vehicle, robot, car, bike, transformer, plasticTransformer)) : machine.getAllInheritings().info();
		assert machine.getAllInheritings().size() == 7 : machine.getAllInheritings().info();

	}

	public void test000_NoInheritanceFromType() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic power = engine.addInstance("Power");

		Generic myBmw = vehicle.addInstance("myBmw");

		assert myBmw.getAttributes(power).size() == 0 : myBmw.getAttributes(power).info();
	}

	public void test000_InheritanceFromType() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic power = engine.addInstance("Power");
		Generic vehiclePower = vehicle.addAttribute(power, "VehiclePower");

		Generic myBmw = vehicle.addInstance("myBmw");
		Generic v233 = myBmw.addHolder(vehiclePower, 233);

		assert myBmw.getHolders(vehiclePower).contains(v233);
		assert myBmw.getHolders(vehiclePower).size() == 1;

		assert myBmw.getHolders(power).contains(v233);
		assert myBmw.getHolders(power).size() == 1;

	}

	public void test001InheritanceFromType() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic power = engine.addInstance("Power");
		Generic vehiclePower = vehicle.addAttribute(power, "VehiclePower");

		Generic myBmw = vehicle.addInstance("myBmw");
		Generic v233 = myBmw.addHolder(power, 233);

		assert myBmw.getHolders(vehiclePower).contains(v233);
		assert myBmw.getHolders(vehiclePower).size() == 1;

		assert myBmw.getHolders(power).contains(v233);
		assert myBmw.getHolders(power).size() == 1;

	}

	public void test002_InheritanceFromType() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic singular = engine.addInstance("Singular");

		Generic vehiclePower = vehicle.addAttribute(singular, "Power");
		catchAndCheckCause(() -> singular.enableSingularConstraint(ApiStatics.BASE_POSITION), org.genericsystem.api.core.exceptions.NotFoundException.class);
	}

	public void test003_InheritanceFromType() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic singular = engine.addInstance("Singular");

		Generic vehiclePower = vehicle.addAttribute(singular, "Power");

		vehiclePower.enableSingularConstraint(ApiStatics.BASE_POSITION);
		assert vehiclePower.isSingularConstraintEnabled(ApiStatics.BASE_POSITION);
		Generic myBmw = vehicle.addInstance("myBmw");

		Generic v233 = myBmw.addHolder(vehiclePower, 233);
		assert myBmw.getHolders(vehiclePower).contains(v233);
		assert myBmw.getHolders(vehiclePower).size() == 1;
		catchAndCheckCause(() -> myBmw.addHolder(vehiclePower, 234), SingularConstraintViolationException.class);
	}

}
