package org.genercisystem.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.genericsystem.api.exception.ConsistencyConstraintViolationException;
import org.genericsystem.impl.Engine;
import org.genericsystem.impl.Generic;
import org.testng.annotations.Test;

@Test
public class UpdatableServiceTest extends AbstractTest {

	public void test001_setValue_Type() {
		Generic engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic vehicle2 = vehicle.updateValue("Vehicle2");
		assert "Vehicle2".equals(vehicle2.getValue());
		assert vehicle2.isAlive();
	}

	public void test003_setValue_InstanceOfType() {
		Generic engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		String valueCar = "Car";
		Generic car = vehicle.addInstance(valueCar);
		String newValue = "elciheV";
		Generic newVehicle = vehicle.updateValue(newValue);
		assert newValue.equals(newVehicle.getValue());
		assert valueCar.equals(car.getValue());
		assert engine == newVehicle.getMeta();
		// assert engine.computeDependencies().contains(newVehicle);
		Generic newCar = newVehicle.getInstances().iterator().next();
		assert newValue.equals(newCar.getMeta().getValue());
	}

	public void test004_setValue_noCollateralDommage() {
		Generic engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		vehicle.addInstance("Car");
		String caveValue = "Cave";
		Generic cave = engine.addInstance(caveValue);
		vehicle.updateValue("elciheV");
		assert caveValue.equals(cave.getValue());
		assert engine == cave.getMeta();
		assert cave.getInstances().size() == 0;
		assert cave.isAlive();
	}

	public void test007_setValue_Type() {
		Generic engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		vehicle.addInstance("myVehicle");
		vehicle.updateValue("newVehicle");

		Generic newVehicle2 = engine.getInstance("newVehicle");
		assert newVehicle2 != null;
		assert engine.equals(newVehicle2.getMeta());

		Generic myVehicle2 = newVehicle2.getInstance("myVehicle");
		assert myVehicle2 != null;
		assert newVehicle2.equals(myVehicle2.getMeta());
	}

	public void test008_setValue_Type() {
		Generic engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("myVehicle");
		myVehicle.updateValue("newMyVehicle");

		assert vehicle.equals(vehicle.getInstance("newMyVehicle").getMeta());
	}

	public void test020_setValue_Inheritance() {
		Generic engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "car");
		Generic newVehicle = vehicle.updateValue("Vehicle2");
		// log.info("" + newVehicle.getAttributes(engine));
		// newVehicle.getSupersStream().forEach(attribute -> log.info(attribute.info()));
		assert newVehicle.isAlive();
		assert !vehicle.isAlive();
		assert !car.isAlive();

		assert "Vehicle2".equals(newVehicle.getValue());
		assert engine.equals(newVehicle.getMeta());
		// assert engine.computeDependencies().contains(newVehicle);
		// assert newVehicle.computeDependencies().size() == 2;
		// assert newVehicle.computeDependencies().contains(newVehicle);
		// Generic newOptions = newVehicle.computeDependencies().stream().collect(Collectors.toList()).get(0);
		// assert newOptions.isAlive();
		// if ("Vehicle2".equals(newOptions.getValue()))
		// newOptions = newVehicle.computeDependencies().stream().collect(Collectors.toList()).get(1);
		// assert engine.equals(newOptions.getMeta());
		// assert car.getValue().equals(newOptions.getValue());
		// List<Generic> newOptionsSupers = newOptions.getSupers();
		// assert newOptionsSupers.size() == 1;
		// Generic newVehicleFromNewOptions = newOptionsSupers.get(0);
		// assert "Vehicle2".equals(newVehicleFromNewOptions.getValue());
	}

	public void test040_setValue_Component() {
		Generic engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		String valuePower = "Power";
		Generic power = engine.addInstance(valuePower, vehicle);
		String newValue = "elciheV";

		Generic newVehicle = vehicle.updateValue(newValue);

		assert newValue.equals(newVehicle.getValue());
		assert !power.isAlive();
		assert engine.equals(newVehicle.getMeta());
		// assert engine.computeDependencies().contains(newVehicle);
		Generic newPower = engine.getInstance("Power", newVehicle);
		assert newPower.getComposites().size() == 1;
		Generic componentOfPower = newPower.getComposites().get(0);
		assert newVehicle.getValue().equals(componentOfPower.getValue());
		assert engine.equals(componentOfPower.getMeta());
	}

	public void test060_setValue_Type_Inheritance_Component() {
		Generic engine = new Engine();
		Generic machine = engine.addInstance("Machine");
		Generic vehicle = engine.addInstance(machine, "Vehicle");
		Generic power = engine.addInstance("Power", vehicle);
		Generic car = vehicle.addInstance("Car");
		Generic newMachine = machine.updateValue("NewMachine");

		assert engine.isAlive();
		assert !machine.isAlive();
		assert !vehicle.isAlive();
		assert !power.isAlive();
		assert !car.isAlive();

		assert engine.equals(engine.getMeta());
		assert engine.equals(machine.getMeta());
		assert engine.equals(vehicle.getMeta());
		assert engine.getRoot().getMetaAttribute().equals(power.getMeta());
		assert vehicle.equals(car.getMeta());

		assert "NewMachine".equals(newMachine.getValue());
		assert newMachine.getComposites().size() == 0;
		assert newMachine.getSupers().size() == 0;
		assert newMachine.getInstances().size() == 0;
		assert newMachine.getInheritings().size() == 1;

		Generic newVehicle = engine.getInstance("Vehicle");
		assert newVehicle != null;
		assert newVehicle.getComposites().size() == 0;
		assert newVehicle.getSupers().size() == 1;
		assert newVehicle.getInstances().size() == 1;
		assert newVehicle.getInheritings().size() == 0;

		Generic newPower = engine.getInstance("Power", newVehicle);
		assert newPower != null;
		assert newPower.getComposites().size() == 1;
		assert newPower.getSupers().size() == 0;
		assert newPower.getInstances().size() == 0;
		assert newPower.getInheritings().size() == 0;

		Generic newCar = newVehicle.getInstance("Car");
		assert newCar != null;
		assert newCar.getComposites().size() == 0;
		assert newCar.getSupers().size() == 0;
		assert newCar.getInstances().size() == 0;
		assert newCar.getInheritings().size() == 0;
	}

	public void test100_addSuper_Type() {
		// given
		Generic engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance("Car");

		// when
		Generic result = car.updateSupers(vehicle);
		assert result.isAlive();
		// then
		assert engine.isAlive();
		assert vehicle.isAlive();
		assert !car.isAlive();

		// assert engine.getAllInstances().count() == 2;

		Generic newVehicle = engine.getInstance("Vehicle");
		assert newVehicle.getInheritings().size() == 1 : newVehicle.getInheritings().stream().collect(Collectors.toList()) + result.info();
		assert engine.getInstance("Car").getSupers().size() == 1;
	}

	public void test101_addSuper_TypeBetweenTwoTypes() {
		// given
		Generic engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic fourWheels = engine.addInstance(vehicle, "FourWheels");

		// when
		car.updateSupers(fourWheels);

		// then
		assert engine.isAlive();
		assert vehicle.isAlive();
		assert !car.isAlive();

		// LinkedHashSet<Generic> engineDependencies = engine.computeDependencies();
		// assert engineDependencies.size() == 4;
		// assert engine.getAllInstances().count() == 3;

		// Generic newVehicle = engine.getInstance("Vehicle");
		// LinkedHashSet<Generic> newVehicleDependencies = newVehicle.computeDependencies();
		// assert newVehicleDependencies.size() == 3;
		// assert newVehicle.getInheritings().size() == 1;
		//
		// Generic newFourWheels = engine.getInstance("FourWheels");
		// LinkedHashSet<Generic> newFourWheelsDependencies = newFourWheels.computeDependencies();
		// assert newFourWheelsDependencies.size() == 2;
		// assert newFourWheels.getInheritings().size() == 1;
		// assert newFourWheels.getSupersStream().count() == 1;
		//
		// Generic newCar = engine.getInstance("Car");
		// assert newCar.computeDependencies().size() == 1;
		// assert newCar.getSupersStream().count() == 1;
	}

	public void test102_addSuper_AlreadySuper() {
		// given
		Generic engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");

		// when
		car.updateSupers(vehicle);

		// then
		assert engine.isAlive();
		assert vehicle.isAlive();
		assert car.isAlive();

		// LinkedHashSet<Generic> engineDependencies = engine.computeDependencies();
		// assert engineDependencies.size() == 3 : engineDependencies.size();
		// assert engine.getAllInstances().count() == 2;
		//
		// Generic newVehicle = engine.getInstance("Vehicle");
		// LinkedHashSet<Generic> newVehicleDependencies = newVehicle.computeDependencies();
		// assert newVehicleDependencies.size() == 2;
		// assert newVehicle.getInheritings().size() == 1;
		//
		// Generic newCar = engine.getInstance("Car");
		// assert newCar.computeDependencies().size() == 1;
		// assert newCar.getSupersStream().count() == 1;
	}

	public void test103_addSuper_NoRegressionOnDataModel() {
		// given
		Generic engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic animals = engine.addInstance("Animals");
		Generic myVehicle = vehicle.addInstance("MyVehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic power = engine.addInstance("Power", car);
		Generic myCar = car.addInstance("MyCar");
		Generic color = engine.addInstance("Color");
		Generic red = color.addInstance("Red");
		Generic green = color.addInstance("Green");
		Generic blue = color.addInstance("Blue");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic myCarRed = vehicleColor.addInstance("MyCarRed", myCar, red);
		Generic myVehicleGreen = vehicleColor.addInstance("MyVehicleGreen", myVehicle, green);

		// when
		Generic fourWheels = engine.addInstance(vehicle, "FourWheels");
		car.updateSupers(fourWheels);

		// then
		assert engine.isAlive();
		assert vehicle.isAlive();
		assert animals.isAlive();
		assert myVehicle.isAlive();
		assert !car.isAlive();
		assert !power.isAlive();
		assert !myCar.isAlive();
		assert color.isAlive();
		assert red.isAlive();
		assert green.isAlive();
		assert blue.isAlive();
		assert vehicleColor.isAlive();
		assert !myCarRed.isAlive();
		assert myVehicleGreen.isAlive();
		assert fourWheels.isAlive();

		Generic newVehicle = engine.getInstance("Vehicle");
		// LinkedHashSet<Generic> newVehicleDependencies = newVehicle.computeDependencies();
		// assert newVehicleDependencies.size() == 9;
		// assert newVehicle.getInheritings().size() == 1;

		Generic newFourWheels = engine.getInstance("FourWheels");
		// assert newFourWheels.computeAllDependencies().size() == 5;
		// assert newFourWheels.computeDependencies().containsAll(Arrays.asList(car, myCar, fourWheels)) : newFourWheels.computeDependencies();
		// assert newFourWheels.getInheritings().size() == 1;
		// assert newFourWheels.getSupersStream().count() == 1;
		//
		// Generic newCar = engine.getInstance("Car");
		// assert newCar.computeDependencies().size() == 4;
		// assert newCar.getSupersStream().count() == 1;
	}

	public void test200_replaceComponent() {
		Generic engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic myVehicle = vehicle.addInstance("MyVehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic power = engine.addInstance("Power", car);
		Generic myCar = car.addInstance("MyCar");
		Generic color = engine.addInstance("Color");
		Generic red = color.addInstance("Red");
		Generic green = color.addInstance("Green");
		Generic blue = color.addInstance("Blue");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic myCarRed = vehicleColor.addInstance("MyCarRed", myCar, red);
		Generic myVehicleGreen = vehicleColor.addInstance("MyVehicleGreen", myVehicle, green);

		// when
		myCarRed.updateComponents(myCar, blue);

		// then
		assert engine.isAlive();
		assert vehicle.isAlive();
		assert myVehicle.isAlive();
		assert car.isAlive();
		assert power.isAlive();
		assert myCar.isAlive();
		assert color.isAlive();
		assert red.isAlive();
		assert green.isAlive();
		assert blue.isAlive();
		assert vehicleColor.isAlive();
		assert !myCarRed.isAlive();
		assert myVehicleGreen.isAlive();

		Generic newCarBlue = vehicleColor.getInstance("MyCarRed", myCar, blue);
		List<Generic> newCarBlueComponents = newCarBlue.getComposites();
		assert newCarBlueComponents.size() == 2;
	}

	public void test201_replaceComponent_KO() {
		Generic engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic myCar = car.addInstance("MyCar");
		Generic color = engine.addInstance("Color");
		Generic red = color.addInstance("Red");
		Generic blue = color.addInstance("Blue");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic myCarRed = vehicleColor.addInstance("MyCarRed", myCar, red);

		new RollbackCatcher() {
			@Override
			public void intercept() {
				// when
				myCarRed.updateComponents(blue);
			}
			// then
		}.assertIsCausedBy(IllegalArgumentException.class);
	}

	public void test300_replaceComponentWithValueModification() {
		Generic engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic myCar = car.addInstance("MyCar");
		Generic color = engine.addInstance("Color");
		Generic red = color.addInstance("Red");
		Generic blue = color.addInstance("Blue");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic myCarRed = vehicleColor.addInstance("MyCarRed", myCar, red);

		// when
		myCarRed.update("MyCarBlue", myCar, blue);

		// then
		assert vehicleColor.getInstance("MyCarRed", myCar, blue) == null;
		assert vehicleColor.getInstance("MyCarRed", myCar, red) == null;
		assert vehicleColor.getInstance("MyCarBlue", myCar, red) == null;

		Generic newCarBlue = vehicleColor.getInstance("MyCarBlue", myCar, blue);
		// assert newCarBlue.computeDependencies().size() == 1;
		List<Generic> newCarBlueComponents = newCarBlue.getComposites();
		assert newCarBlueComponents.size() == 2;
	}

	public void test301_replaceComponentWithValueModification_InsistentExceptionKO() {
		Generic engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic myCar = car.addInstance("MyCar");
		Generic color = engine.addInstance("Color");
		Generic red = color.addInstance("Red");
		Generic green = color.addInstance("Green");
		Generic blue = color.addInstance("Blue");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic myCarRed = vehicleColor.addInstance("MyCarRed", myCar, red);

		new RollbackCatcher() {
			@Override
			public void intercept() {
				// when
				myCarRed.update("MyCarBlue", green, blue);
			}
			// then
		}.assertIsCausedBy(ConsistencyConstraintViolationException.class);
	}

}
