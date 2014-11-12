package org.genericsystem.kernel;

import org.genericsystem.api.exception.MetaRuleConstraintViolationException;
import org.testng.annotations.Test;

@Test
public class UpdateTest extends AbstractTest {

	public void test001_updateValue() {
		Root root = new Root();
		Vertex car = root.addInstance("Car");
		assert "Car".equals(car.getValue());
		Vertex carRename = car.update("CarRename");
		assert !car.isAlive();
		assert "CarRename".equals(carRename.getValue());
	}

	public void test002_updateValue() {
		Root root = new Root();
		Vertex car = root.addInstance("Car");
		assert "Car".equals(car.getValue());
		Vertex carRename = car.updateValue("CarRename");
		assert !car.isAlive();
		assert "CarRename".equals(carRename.getValue());
	}

	public void test002_updateMeta() {
		Root root = new Root();
		Vertex car = root.addInstance("Car");
		Vertex power = car.addAttribute("Power");
		Vertex myCar = car.addInstance("MyCar");

		Vertex myCarV233 = myCar.addHolder(power, "myCarV233");

		assert myCar.getMeta().equals(car);
		Vertex carUpdate = car.updateValue("CarUpdate");
		assert carUpdate.getInstances().get().allMatch(x -> "MyCar".equals(x.getValue()));
		assert carUpdate.getInstances().get().allMatch(x -> x.getHolders(power).get().allMatch(y -> "myCarV233".equals(y.getValue())));
		assert !myCar.isAlive();
		assert !car.isAlive();
		assert root.getInstances().contains(carUpdate);
		assert root.getInstances().size() == 1;
		assert car.getMeta().equals(root);
	}

	public void test004_updateHolder() {
		Root root = new Root();
		Vertex car = root.addInstance("Car");
		Vertex power = car.addAttribute("Power");
		Vertex myCar = car.addInstance("MyCar");
		Vertex v233 = myCar.addHolder(power, 233);

		assert myCar.getComposites().contains(v233);
		assert myCar.getComposites().size() == 1;
		assert v233.getValue().equals(233);

		Vertex v455 = v233.updateValue(455);

		assert !v233.isAlive();
		assert myCar.getComposites().contains(v455);
		assert myCar.getComposites().size() == 1;
		assert v455.getValue().equals(455);
	}

	public void test005_updateSuper() {
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex car = root.addInstance(vehicle, "Car");

		assert car.getSupers().contains(vehicle);
		Vertex vehicleBis = root.addInstance("VehicleBis");
		// add au lieu de replace ??
		Vertex carBis = car.updateSupers(vehicleBis);

		assert !car.isAlive();
		assert vehicle.isAlive();

		assert car.getSupers().contains(vehicle);
		assert vehicle.getInheritings().size() == 0;

		assert carBis.isAlive();
		assert carBis.getSupers().contains(vehicleBis);

		assert vehicleBis.getInheritings().contains(carBis);
		assert vehicleBis.getInheritings().size() == 1;

	}

	public void test006_attributeToRelation() {
		Root root = new Root();
		Vertex car = root.addInstance("Car");
		Vertex power = car.addAttribute("Power");
		Vertex myCar = car.addInstance("MyCar");
		Vertex v233 = myCar.addHolder(power, 233);
		Vertex powerType = root.addInstance("PowerType");

		catchAndCheckCause(() -> power.update("carPower", powerType), MetaRuleConstraintViolationException.class);

		assert root.getInstances().contains(car);
		assert car.isAlive();
		assert car.getInstances().contains(myCar);
		// assert myCar.getHolders(power).contains(v233);
		// assert myCar.getHolders(power).size() == 1;
		// assert v233.getValue().equals(233);

	}

	public void test007_structurel_WithInheritings_AndInstances() {
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex car = root.addInstance(vehicle, "Car");
		Vertex power = car.addAttribute("Power");
		Vertex myCar = car.addInstance("myCar");
		Vertex v233 = myCar.addHolder(power, 233);

		Vertex vehicleUpdate = vehicle.update("VehicleUpdate");

		assert vehicleUpdate.isAlive();
		assert !vehicle.isAlive();
		assert root.getInstances().contains(vehicleUpdate);
		assert root.getInstances().contains(vehicleUpdate.getInheritings().first());
		assert root.getInstances().size() == 2;

		assert vehicleUpdate.getInheritings().get().allMatch(x -> "Car".equals(x.getValue()));
		assert vehicleUpdate.getInheritings().get().allMatch(x -> x.getInstances().get().allMatch(y -> "myCar".equals(y.getValue())));
		assert vehicleUpdate.getInheritings().get().allMatch(x -> x.getInstances().get().allMatch(y -> y.getHolders(power).get().allMatch(z -> z.getValue().equals(233))));

		assert v233.getValue().equals(233);

	}
}
