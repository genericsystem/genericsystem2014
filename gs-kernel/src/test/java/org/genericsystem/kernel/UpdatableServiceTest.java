package org.genericsystem.kernel;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.genericsystem.api.core.exceptions.MetaRuleConstraintViolationException;
import org.testng.annotations.Test;

@Test
public class UpdatableServiceTest extends AbstractTest {

	public void test001_setValue_Type() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic vehicle2 = vehicle.updateValue("Vehicle2");
		assert "Vehicle2".equals(vehicle2.getValue());
		assert vehicle2.isAlive();
	}

	public void test003_setValue_InstanceOfType() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		String valueCar = "Car";
		Generic car = vehicle.addInstance(valueCar);
		String newValue = "elciheV";
		Generic newVehicle = vehicle.updateValue(newValue);
		assert newValue.equals(newVehicle.getValue());
		assert valueCar.equals(car.getValue());
		assert engine == newVehicle.getMeta();
		assert engine.getCurrentCache().computeDependencies(engine).contains(newVehicle);
		Generic newCar = newVehicle.getInstances().iterator().next();
		assert newValue.equals(newCar.getMeta().getValue());
	}

	public void test004_setValue_noCollateralDommage() {
		Generic engine = new Root();
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
		Generic engine = new Root();
		Generic bike = engine.addInstance("Bike");
		Generic car = engine.addInstance("Car");
		Generic myBmwBike = bike.addInstance("myBmwBike");

		Generic newBike = bike.updateValue("newBike");

		Collection<Generic> engineAliveDependencies = newBike.getCurrentCache().computeDependencies(newBike);
		assert engineAliveDependencies.size() == 2 : engineAliveDependencies.size();
		assert !engineAliveDependencies.contains(car);
		assert !engineAliveDependencies.contains(myBmwBike);

		Generic getNewBike = engine.getInstance("newBike");
		assert getNewBike != null;
		assert engine.equals(getNewBike.getMeta());

		Generic getMyBmwBike = getNewBike.getInstance("myBmwBike");
		assert getMyBmwBike != null;
		assert getNewBike.equals(getMyBmwBike.getMeta());
	}

	public void test008_setValue_Type() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance("Car");
		Generic newBeetle = car.addInstance("NewBeetle");
		Generic newCar = car.updateValue("raC");

		Collection<Generic> engineAliveDependencies = newCar.getCurrentCache().computeDependencies(newCar);
		assert engineAliveDependencies.size() == 2;
		assert !engineAliveDependencies.contains(car);
		assert !engineAliveDependencies.contains(newBeetle);

		Generic vertex1asNewCar = engine.getInstance("raC");
		assert vertex1asNewCar != null;
		assert engine.equals(vertex1asNewCar.getMeta());

		Generic vertex2asNewNewBeetle = vertex1asNewCar.getInstance("NewBeetle");
		assert vertex2asNewNewBeetle != null;
		assert vertex1asNewCar.equals(vertex2asNewNewBeetle.getMeta());
	}

	public void test020_setValue_Inheritance() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic options = engine.addInstance(vehicle, "Options");

		Generic newVehicle = vehicle.updateValue("elciheV");

		assert newVehicle.isAlive();
		assert !vehicle.isAlive();
		assert !options.isAlive();

		assert "elciheV".equals(newVehicle.getValue());
		assert engine.equals(newVehicle.getMeta());
		assert engine.getCurrentCache().computeDependencies(engine).contains(newVehicle);
		assert newVehicle.getCurrentCache().computeDependencies(newVehicle).size() == 2;
		assert newVehicle.getCurrentCache().computeDependencies(newVehicle).contains(newVehicle);
		Generic newOptions = newVehicle.getCurrentCache().computeDependencies(newVehicle).stream().collect(Collectors.toList()).get(0);
		assert newOptions.isAlive();
		if ("elciheV".equals(newOptions.getValue()))
			newOptions = newVehicle.getCurrentCache().computeDependencies(newVehicle).stream().collect(Collectors.toList()).get(1);
		assert engine.equals(newOptions.getMeta());
		assert options.getValue().equals(newOptions.getValue());
		List<Generic> newOptionsSupers = newOptions.getSupers();
		assert newOptionsSupers.size() == 1;
		Generic newVehicleFromNewOptions = newOptionsSupers.get(0);
		assert "elciheV".equals(newVehicleFromNewOptions.getValue());
	}

	public void test040_setValue_Composite() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		String valuePower = "Power";
		Generic power = engine.addInstance(valuePower, vehicle);
		String newValue = "elciheV";

		Generic newVehicle = vehicle.updateValue(newValue);

		assert newValue.equals(newVehicle.getValue());
		assert !power.isAlive();
		assert engine.equals(newVehicle.getMeta());
		assert engine.getCurrentCache().computeDependencies(engine).contains(newVehicle);
		Generic newPower = engine.getInstance("Power", newVehicle);
		assert newPower.getComponents().size() == 1;
		Generic compositeOfPower = newPower.getComponents().get(0);
		assert newVehicle.getValue().equals(compositeOfPower.getValue());
		assert engine.equals(compositeOfPower.getMeta());
	}

	public void test060_setValue_Type_Inheritance_Composite() {
		Generic engine = new Root();
		Generic machine = engine.addInstance("Machine");
		Generic vehicle = engine.addInstance(machine, "Vehicle");
		String valuePower = "Power";
		Generic power = engine.addInstance(valuePower, vehicle);
		Generic car = vehicle.addInstance("Car");
		String newValue = "enihcaM";

		Generic newMachine = machine.updateValue(newValue);

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

		assert newValue.equals(newMachine.getValue());
		assert newMachine.getComponents().size() == 0;
		assert newMachine.getSupers().isEmpty();
		assert newMachine.getInstances().size() == 0;
		assert newMachine.getInheritings().size() == 1;

		Generic newVehicle = engine.getInstance(newMachine, "Vehicle");
		assert newVehicle != null;
		assert newVehicle.getComponents().size() == 0;
		assert newVehicle.getSupers().size() == 1;
		assert newVehicle.getInstances().size() == 1;
		assert newVehicle.getInheritings().size() == 0;

		Generic newPower = engine.getInstance("Power", newVehicle);
		assert newPower != null;
		assert newPower.getComponents().size() == 1;
		assert newPower.getSupers().size() == 0;
		assert newPower.getInstances().size() == 0;
		assert newPower.getInheritings().size() == 0;

		Generic newCar = newVehicle.getInstance("Car");
		assert newCar != null;
		assert newCar.getComponents().size() == 0;
		assert newCar.getSupers().size() == 0;
		assert newCar.getInstances().size() == 0;
		assert newCar.getInheritings().size() == 0;
	}

	public void test100_addSuper_Type() {
		// given
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance("Car");

		assert !car.isSuperOf(car.getMeta(), Collections.singletonList(vehicle), car.getValue(), car.getComponents());
		// when
		car.updateSupers(vehicle);

		// then
		assert engine.isAlive();
		assert vehicle.isAlive();
		assert !car.isAlive();

		// assert engine.getAllInstances().count() == 2;

		Generic newVehicle = engine.getInstance("Vehicle");
		assert newVehicle.getInheritings().size() == 1 : newVehicle.getInheritings().info();
		assert engine.getInstance(newVehicle, "Car").getSupers().size() == 1;
	}

	public void test101_addSuper_TypeBetweenTwoTypes() {
		// given
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic fourWheels = engine.addInstance(vehicle, "FourWheels");

		// when
		car.updateSupers(fourWheels);

		// then
		assert engine.isAlive();
		assert vehicle.isAlive();
		assert !car.isAlive();

		Collection<Generic> engineDependencies = engine.getCurrentCache().computeDependencies(engine);
		// assert engineDependencies.size() == 4;
		// assert engine.getAllInstances().count() == 3;

		Generic newVehicle = engine.getInstance("Vehicle");
		Collection<Generic> newVehicleDependencies = newVehicle.getCurrentCache().computeDependencies(newVehicle);
		assert newVehicleDependencies.size() == 3;
		assert newVehicle.getInheritings().size() == 1;

		Generic newFourWheels = engine.getInstance(newVehicle, "FourWheels");
		Collection<Generic> newFourWheelsDependencies = newFourWheels.getCurrentCache().computeDependencies(newFourWheels);
		assert newFourWheelsDependencies.size() == 2;
		assert newFourWheels.getInheritings().size() == 1;
		assert newFourWheels.getSupers().size() == 1;

		Generic newCar = engine.getInstance(newFourWheels, "Car");
		assert newCar.getCurrentCache().computeDependencies(newCar).size() == 1;
		assert newCar.getSupers().size() == 1;
	}

	public void test102_addSuper_AlreadySuper() {
		// given
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");

		// when
		car.updateSupers(vehicle);

		// then
		assert engine.isAlive();
		assert vehicle.isAlive();
		assert !car.isAlive();

		Collection<Generic> engineDependencies = engine.getCurrentCache().computeDependencies(engine);
		// assert engineDependencies.size() == 3 : engineDependencies.size();
		// assert engine.getAllInstances().size() == 2 : engine.getAllInstances().info();

		Generic newVehicle = engine.getInstance("Vehicle");
		Collection<Generic> newVehicleDependencies = engine.getCurrentCache().computeDependencies(newVehicle);
		assert newVehicleDependencies.size() == 2 : newVehicleDependencies;
		assert newVehicle.getInheritings().size() == 1;

		Generic newCar = engine.getInstance(newVehicle, "Car");
		assert newCar.getCurrentCache().computeDependencies(newCar).size() == 1;
		assert newCar.getSupers().size() == 1;
	}

	public void test103_addSuper_NoRegressionOnDataModel() {
		// given
		Generic engine = new Root();
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
		Collection<Generic> newVehicleDependencies = newVehicle.getCurrentCache().computeDependencies(newVehicle);
		assert newVehicleDependencies.size() == 9;
		assert newVehicle.getInheritings().size() == 1;

		Generic newFourWheels = engine.getInstance(newVehicle, "FourWheels");
		assert newFourWheels.getCurrentCache().computeDependencies(newFourWheels).size() == 5;
		assert newFourWheels.getInheritings().size() == 1;
		assert newFourWheels.getSupers().size() == 1;

		Generic newCar = engine.getInstance(newFourWheels, "Car");
		assert newCar.getCurrentCache().computeDependencies(newCar).size() == 4;
		assert newCar.getSupers().size() == 1;
	}

	public void test200_replaceComposite() {
		Generic engine = new Root();
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
		List<Generic> newCarBlueComposites = newCarBlue.getComponents();
		assert newCarBlueComposites.size() == 2;
	}

	public void test201_replaceComposite_KO() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic myCar = car.addInstance("MyCar");
		Generic color = engine.addInstance("Color");
		Generic red = color.addInstance("Red");
		Generic blue = color.addInstance("Blue");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic myCarRed = vehicleColor.addInstance("MyCarRed", myCar, red);
		catchAndCheckCause(() -> myCarRed.updateComponents(blue), MetaRuleConstraintViolationException.class);

	}

	public void test202_replaceComposite_OK() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic myCar = car.addInstance("MyCar");
		Generic color = engine.addInstance("Color");
		Generic date = engine.addInstance("Date");
		Generic red = color.addInstance("Red");
		Generic blue = color.addInstance("Blue");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		vehicleColor.updateComponents(vehicle, color, date);

	}

	public void test300_replaceCompositeWithValueModification() {
		Generic engine = new Root();
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
		assert newCarBlue.getCurrentCache().computeDependencies(newCarBlue).size() == 1;
		List<Generic> newCarBlueComposites = newCarBlue.getComponents();
		assert newCarBlueComposites.size() == 2;
	}

	public void test301_replaceCompositeWithValueModification_InsistentExceptionOK() {
		Generic engine = new Root();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic myCar = car.addInstance("MyCar");
		Generic color = engine.addInstance("Color");
		Generic red = color.addInstance("Red");
		Generic blue = color.addInstance("Blue");
		Generic vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Generic myCarRed = vehicleColor.addInstance("MyCarRed", myCar, red);
		Generic myCarBlue = myCarRed.update("MyCarBlue", myCar, blue);
		assert !myCarRed.isAlive();
		assert myCarBlue.isAlive();
		assert myCarBlue.getMeta().equals(vehicleColor);

	}

	public void test021_AddInstance_AttributeWithSameNameAlreadyExisting() {
		Root engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic carPower = engine.setInstance("Power", car);
		Generic power = engine.addInstance("Power");

		assert !carPower.isAlive();
		assert power.isAlive();
		assert !car.getAttributes(engine).contains(carPower);
	}
}
