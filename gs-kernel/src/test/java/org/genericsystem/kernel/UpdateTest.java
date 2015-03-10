package org.genericsystem.kernel;

import org.genericsystem.api.defaults.exceptions.PropertyConstraintViolationException;
import org.genericsystem.api.exception.MetaRuleConstraintViolationException;
import org.testng.annotations.Test;

@Test
public class UpdateTest extends AbstractTest {

	public void test001_updateValue() {
		Root root = new Root();
		Generic car = root.addInstance("Car");
		assert "Car".equals(car.getValue());
		Generic carRename = car.update("CarRename");
		assert !car.isAlive();
		assert "CarRename".equals(carRename.getValue());
	}

	public void test002_updateValue() {
		Root root = new Root();
		Generic car = root.addInstance("Car");
		assert "Car".equals(car.getValue());
		Generic carRename = car.updateValue("CarRename");
		assert !car.isAlive();
		assert "CarRename".equals(carRename.getValue());
	}

	public void test002_updateMeta() {
		Root root = new Root();
		Generic car = root.addInstance("Car");
		Generic power = car.addAttribute("Power");
		Generic myCar = car.addInstance("MyCar");

		Generic myCarV233 = myCar.addHolder(power, "myCarV233");

		assert myCar.getMeta().equals(car);
		Generic carUpdate = car.updateValue("CarUpdate");
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
		Generic car = root.addInstance("Car");
		Generic power = car.addAttribute("Power");
		Generic myCar = car.addInstance("MyCar");
		Generic v233 = myCar.addHolder(power, 233);

		assert myCar.getComposites().contains(v233);
		assert myCar.getComposites().size() == 1;
		assert v233.getValue().equals(233);

		Generic v455 = v233.updateValue(455);

		assert !v233.isAlive();
		assert myCar.getComposites().contains(v455);
		assert myCar.getComposites().size() == 1;
		assert v455.getValue().equals(455);
	}

	public void test005_updateSuper() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic car = root.addInstance(vehicle, "Car");

		assert car.getSupers().contains(vehicle);
		Generic vehicleBis = root.addInstance("VehicleBis");
		Generic carBis = car.updateSupers(vehicleBis);

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
		Generic car = root.addInstance("Car");
		Generic power = car.addAttribute("Power");
		Generic myCar = car.addInstance("MyCar");
		Generic v233 = myCar.addHolder(power, 233);
		Generic powerType = root.addInstance("PowerType");
		catchAndCheckCause(() -> power.update("carPower", powerType), MetaRuleConstraintViolationException.class);
	}

	public void test007_structurel_WithInheritings_AndInstances() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic car = root.addInstance(vehicle, "Car");
		Generic power = car.addAttribute("Power");
		Generic myCar = car.addInstance("myCar");
		Generic v233 = myCar.addHolder(power, 233);

		Generic vehicleUpdate = vehicle.update("VehicleUpdate");

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

	public void test008_updateToAlreadyExists() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic car = root.addInstance("Car");
		Generic power = car.addAttribute("Power");
		Generic myCar = car.addInstance("myCar");
		Generic v233 = myCar.addHolder(power, 233);

		Generic carUpdate = car.update("Vehicle");

		assert carUpdate.equals(vehicle) : carUpdate.info();
		assert !car.isAlive();
		assert !myCar.isAlive();

		assert vehicle.getInstance("myCar") != null;
		assert vehicle.getInstance("myVehicle") != null : carUpdate.getInstances().info();
		assert vehicle.getInstances().size() == 2;
	}

	public void test009_updateToAlreadyExists() {
		Root root = new Root();
		Generic vehicle = root.addInstance("Vehicle");
		Generic power = vehicle.addAttribute("Power");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic v233 = myVehicle.addHolder(power, 233);

		Generic car = root.addInstance("Car");
		assert !root.getCurrentCache().computeDependencies(car).contains(power);
		Generic powerBis = car.addAttribute("Power");
		Generic myCar = car.addInstance("myCar");
		Generic v233Bis = myCar.addHolder(powerBis, 233);

		Generic carUpdate = car.update("Vehicle");
		assert power.isAlive();

		assert carUpdate.equals(vehicle);
		assert !car.isAlive();
		assert !myCar.isAlive();

		assert vehicle.getInstance("myCar") != null;
		assert vehicle.getInstance("myVehicle") != null;
		assert vehicle.getInstances().size() == 2;

		myCar = vehicle.getInstance("myCar");
		myVehicle = vehicle.getInstance("myVehicle");
		assert vehicle.isAlive();
		assert power.isAlive();
		// assert false : vehicle.getAttributes().first().info();
		assert vehicle.getAttributes().contains(power);
		assert v233.isAlive();

		assert !powerBis.isAlive();
		assert !v233Bis.isAlive();

		assert !myCar.getComposites().isEmpty();
		assert !myVehicle.getHolders(power).isEmpty();
		assert !myCar.getHolders(power).isEmpty();

		assert myVehicle.getHolders(power).first().getValue().equals(233);
		assert myVehicle.getHolders(power).first().getValue().equals(233);
	}

	public void test010_propertyConstraint() {
		Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic myCar = car.addInstance("myCar");

		Generic color = engine.addInstance("Color");
		Generic red = color.addInstance("red");
		Generic yellow = color.addInstance("yellow");

		Generic carColor = car.addAttribute("carColor", color);
		carColor.enablePropertyConstraint();

		Generic myCarRed = carColor.addInstance("myCarRed", myCar, red);
		carColor.addInstance("myCarYellow", myCar, yellow);
		catchAndCheckCause(() -> myCarRed.update("myCarRed", myCar, yellow), PropertyConstraintViolationException.class);
	}

	public void test011_propertyConstraint() {
		Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic myCar = car.addInstance("myCar");

		Generic color = engine.addInstance("Color");
		color.addInstance("red");
		Generic yellow = color.addInstance("yellow");

		Generic carColor = car.addAttribute("carColor", color);
		carColor.enablePropertyConstraint();

		carColor.addInstance("myCarYellow", myCar, yellow);
		catchAndCheckCause(() -> carColor.addInstance("myCarRed", myCar, yellow), PropertyConstraintViolationException.class);
	}
}
