package org.genericsystem.kernel;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

@Test
public class UpdatableServiceTest extends AbstractTest {

	public void test001_setValue_Type() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex vehicle2 = vehicle.updateValue("Vehicle2");
		assert "Vehicle2".equals(vehicle2.getValue());
		assert vehicle2.isAlive();
	}

	public void test003_setValue_InstanceOfType() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		String valueCar = "Car";
		Vertex car = vehicle.addInstance(valueCar);
		String newValue = "elciheV";
		Vertex newVehicle = vehicle.updateValue(newValue);
		assert newValue.equals(newVehicle.getValue());
		assert valueCar.equals(car.getValue());
		assert engine == newVehicle.getMeta();
		assert engine.computeDependencies().contains(newVehicle);
		Vertex newCar = newVehicle.getInstances().iterator().next();
		assert newValue.equals(newCar.getMeta().getValue());
	}

	public void test004_setValue_noCollateralDommage() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		vehicle.addInstance("Car");
		String caveValue = "Cave";
		Vertex cave = engine.addInstance(caveValue);
		vehicle.updateValue("elciheV");
		assert caveValue.equals(cave.getValue());
		assert engine == cave.getMeta();
		assert cave.getInstances().size() == 0;
		assert cave.isAlive();
	}

	public void test007_setValue_Type() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		String valueCar = "Car";
		Vertex car = vehicle.addInstance(valueCar);
		String valueNewBeetle = "NewBeetle";
		Vertex newBeetle = car.addInstance(valueNewBeetle);
		String newValue = "elciheV";

		Vertex newVehicle = vehicle.updateValue(newValue);

		LinkedHashSet<Vertex> engineAliveDependencies = newVehicle.computeDependencies();
		assert engineAliveDependencies.size() == 3;
		assert !engineAliveDependencies.contains(vehicle);
		assert !engineAliveDependencies.contains(car);
		assert !engineAliveDependencies.contains(newBeetle);

		Vertex vertex1asNewVehicle = engine.getInstance(newValue);
		assert vertex1asNewVehicle != null;
		assert engine.equals(vertex1asNewVehicle.getMeta());

		Vertex vertex2asNewCar = vertex1asNewVehicle.getInstance(valueCar);
		assert vertex2asNewCar != null;
		assert vertex1asNewVehicle.equals(vertex2asNewCar.getMeta());

		Vertex vertex3asNewNewBeetle = vertex2asNewCar.getInstance(valueNewBeetle);
		assert vertex3asNewNewBeetle != null;
		assert vertex2asNewCar.equals(vertex3asNewNewBeetle.getMeta());
	}

	public void test008_setValue_Type() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		String valueCar = "Car";
		Vertex car = vehicle.addInstance(valueCar);
		String valueNewBeetle = "NewBeetle";
		Vertex newBeetle = car.addInstance(valueNewBeetle);
		String newValue = "raC";

		Vertex newCar = car.updateValue(newValue);

		LinkedHashSet<Vertex> engineAliveDependencies = newCar.computeDependencies();
		assert engineAliveDependencies.size() == 2;
		assert !engineAliveDependencies.contains(car);
		assert !engineAliveDependencies.contains(newBeetle);

		Vertex vertex1asNewCar = vehicle.getInstance(newValue);
		assert vertex1asNewCar != null;
		assert vehicle.equals(vertex1asNewCar.getMeta());

		Vertex vertex2asNewNewBeetle = vertex1asNewCar.getInstance(valueNewBeetle);
		assert vertex2asNewNewBeetle != null;
		assert vertex1asNewCar.equals(vertex2asNewNewBeetle.getMeta());
	}

	public void test020_setValue_Inheritance() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		String valueOptions = "Options";
		Vertex options = engine.addInstance(vehicle, valueOptions);
		String newValue = "elciheV";

		Vertex newVehicle = vehicle.updateValue(newValue);

		assert newVehicle.isAlive();
		assert !vehicle.isAlive();
		assert !options.isAlive();

		assert newValue.equals(newVehicle.getValue());
		assert engine.equals(newVehicle.getMeta());
		assert engine.computeDependencies().contains(newVehicle);
		assert newVehicle.computeDependencies().size() == 2;
		assert newVehicle.computeDependencies().contains(newVehicle);
		Vertex newOptions = newVehicle.computeDependencies().stream().collect(Collectors.toList()).get(0);
		assert newOptions.isAlive();
		if (newValue.equals(newOptions.getValue()))
			newOptions = newVehicle.computeDependencies().stream().collect(Collectors.toList()).get(1);
		assert engine.equals(newOptions.getMeta());
		assert options.getValue().equals(newOptions.getValue());
		List<Vertex> newOptionsSupers = newOptions.getSupers();
		assert newOptionsSupers.size() == 1;
		Vertex newVehicleFromNewOptions = newOptionsSupers.get(0);
		assert newValue.equals(newVehicleFromNewOptions.getValue());
	}

	public void test040_setValue_Component() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		String valuePower = "Power";
		Vertex power = engine.addInstance(valuePower, vehicle);
		String newValue = "elciheV";

		Vertex newVehicle = vehicle.updateValue(newValue);

		assert newValue.equals(newVehicle.getValue());
		assert !power.isAlive();
		assert engine.equals(newVehicle.getMeta());
		assert engine.computeDependencies().contains(newVehicle);
		Vertex newPower = engine.getInstance("Power", newVehicle);
		assert newPower.getComposites().size() == 1;
		Vertex componentOfPower = newPower.getComposites().get(0);
		assert newVehicle.getValue().equals(componentOfPower.getValue());
		assert engine.equals(componentOfPower.getMeta());
	}

	public void test060_setValue_Type_Inheritance_Component() {
		Vertex engine = new Root();
		Vertex machine = engine.addInstance("Machine");
		Vertex vehicle = engine.addInstance(machine, "Vehicle");
		String valuePower = "Power";
		Vertex power = engine.addInstance(valuePower, vehicle);
		Vertex car = vehicle.addInstance("Car");
		String newValue = "enihcaM";

		Vertex newMachine = machine.updateValue(newValue);

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
		assert newMachine.getComposites().size() == 0;
		assert newMachine.getSupers().isEmpty();
		assert newMachine.getInstances().size() == 0;
		assert newMachine.getInheritings().size() == 1;

		Vertex newVehicle = engine.getInstance("Vehicle");
		assert newVehicle != null;
		assert newVehicle.getComposites().size() == 0;
		assert newVehicle.getSupers().size() == 1;
		assert newVehicle.getInstances().size() == 1;
		assert newVehicle.getInheritings().size() == 0;

		Vertex newPower = engine.getInstance("Power", newVehicle);
		assert newPower != null;
		assert newPower.getComposites().size() == 1;
		assert newPower.getSupers().size() == 0;
		assert newPower.getInstances().size() == 0;
		assert newPower.getInheritings().size() == 0;

		Vertex newCar = newVehicle.getInstance("Car");
		assert newCar != null;
		assert newCar.getComposites().size() == 0;
		assert newCar.getSupers().size() == 0;
		assert newCar.getInstances().size() == 0;
		assert newCar.getInheritings().size() == 0;
	}

	public void test100_addSuper_Type() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance("Car");

		// when
		car.updateSupers(vehicle);

		// then
		assert engine.isAlive();
		assert vehicle.isAlive();
		assert !car.isAlive();

		// assert engine.getAllInstances().count() == 2;

		Vertex newVehicle = engine.getInstance("Vehicle");
		assert newVehicle.getInheritings().size() == 1;
		assert engine.getInstance("Car").getSupers().size() == 1;
	}

	public void test101_addSuper_TypeBetweenTwoTypes() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex fourWheels = engine.addInstance(vehicle, "FourWheels");

		// when
		car.updateSupers(fourWheels);

		// then
		assert engine.isAlive();
		assert vehicle.isAlive();
		assert !car.isAlive();

		LinkedHashSet<Vertex> engineDependencies = engine.computeDependencies();
		// assert engineDependencies.size() == 4;
		// assert engine.getAllInstances().count() == 3;

		Vertex newVehicle = engine.getInstance("Vehicle");
		LinkedHashSet<Vertex> newVehicleDependencies = newVehicle.computeDependencies();
		assert newVehicleDependencies.size() == 3;
		assert newVehicle.getInheritings().size() == 1;

		Vertex newFourWheels = engine.getInstance("FourWheels");
		LinkedHashSet<Vertex> newFourWheelsDependencies = newFourWheels.computeDependencies();
		assert newFourWheelsDependencies.size() == 2;
		assert newFourWheels.getInheritings().size() == 1;
		assert newFourWheels.getSupers().size() == 1;

		Vertex newCar = engine.getInstance("Car");
		assert newCar.computeDependencies().size() == 1;
		assert newCar.getSupers().size() == 1;
	}

	public void test102_addSuper_AlreadySuper() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");

		// when
		car.updateSupers(vehicle);

		// then
		assert engine.isAlive();
		assert vehicle.isAlive();
		assert !car.isAlive();

		LinkedHashSet<Vertex> engineDependencies = engine.computeDependencies();
		// assert engineDependencies.size() == 3 : engineDependencies.size();
		// assert engine.getAllInstances().count() == 2;

		Vertex newVehicle = engine.getInstance("Vehicle");
		LinkedHashSet<Vertex> newVehicleDependencies = newVehicle.computeDependencies();
		assert newVehicleDependencies.size() == 2;
		assert newVehicle.getInheritings().size() == 1;

		Vertex newCar = engine.getInstance("Car");
		assert newCar.computeDependencies().size() == 1;
		assert newCar.getSupers().size() == 1;
	}

	public void test103_addSuper_NoRegressionOnDataModel() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex animals = engine.addInstance("Animals");
		Vertex myVehicle = vehicle.addInstance("MyVehicle");
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex power = engine.addInstance("Power", car);
		Vertex myCar = car.addInstance("MyCar");
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("Red");
		Vertex green = color.addInstance("Green");
		Vertex blue = color.addInstance("Blue");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Vertex myCarRed = vehicleColor.addInstance("MyCarRed", myCar, red);
		Vertex myVehicleGreen = vehicleColor.addInstance("MyVehicleGreen", myVehicle, green);

		// when
		Vertex fourWheels = engine.addInstance(vehicle, "FourWheels");
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

		Vertex newVehicle = engine.getInstance("Vehicle");
		LinkedHashSet<Vertex> newVehicleDependencies = newVehicle.computeDependencies();
		assert newVehicleDependencies.size() == 9;
		assert newVehicle.getInheritings().size() == 1;

		Vertex newFourWheels = engine.getInstance("FourWheels");
		assert newFourWheels.computeDependencies().size() == 5;
		assert newFourWheels.getInheritings().size() == 1;
		assert newFourWheels.getSupers().size() == 1;

		Vertex newCar = engine.getInstance("Car");
		assert newCar.computeDependencies().size() == 4;
		assert newCar.getSupers().size() == 1;
	}

	public void test200_replaceComponent() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("MyVehicle");
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex power = engine.addInstance("Power", car);
		Vertex myCar = car.addInstance("MyCar");
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("Red");
		Vertex green = color.addInstance("Green");
		Vertex blue = color.addInstance("Blue");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Vertex myCarRed = vehicleColor.addInstance("MyCarRed", myCar, red);
		Vertex myVehicleGreen = vehicleColor.addInstance("MyVehicleGreen", myVehicle, green);

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

		Vertex newCarBlue = vehicleColor.getInstance("MyCarRed", myCar, blue);
		List<Vertex> newCarBlueComponents = newCarBlue.getComposites();
		assert newCarBlueComponents.size() == 2;
	}

	public void test201_replaceComponent_KO() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex myCar = car.addInstance("MyCar");
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("Red");
		Vertex blue = color.addInstance("Blue");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Vertex myCarRed = vehicleColor.addInstance("MyCarRed", myCar, red);

		catchAndCheckCause(() -> myCarRed.updateComponents(blue), IllegalArgumentException.class);

	}

	public void test300_replaceComponentWithValueModification() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex myCar = car.addInstance("MyCar");
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("Red");
		Vertex blue = color.addInstance("Blue");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Vertex myCarRed = vehicleColor.addInstance("MyCarRed", myCar, red);

		// when
		myCarRed.update("MyCarBlue", myCar, blue);

		// then
		assert vehicleColor.getInstance("MyCarRed", myCar, blue) == null;
		assert vehicleColor.getInstance("MyCarRed", myCar, red) == null;
		assert vehicleColor.getInstance("MyCarBlue", myCar, red) == null;

		Vertex newCarBlue = vehicleColor.getInstance("MyCarBlue", myCar, blue);
		assert newCarBlue.computeDependencies().size() == 1;
		List<Vertex> newCarBlueComponents = newCarBlue.getComposites();
		assert newCarBlueComponents.size() == 2;
	}

	public void test301_replaceComponentWithValueModification_InsistentExceptionKO() {
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex myCar = car.addInstance("MyCar");
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("Red");
		Vertex green = color.addInstance("Green");
		Vertex blue = color.addInstance("Blue");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Vertex myCarRed = vehicleColor.addInstance("MyCarRed", myCar, red);

		catchAndCheckCause(() -> myCarRed.update("MyCarBlue", green, blue), IllegalStateException.class);
	}

	public void test021_AddInstance_AttributeWithSameNameAlreadyExisting() {
		Root engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex carPower = engine.setInstance("Power", car);
		Vertex power = engine.addInstance("Power");

		assert !carPower.isAlive();
		assert power.isAlive();
		assert !car.getAttributes(engine).contains(carPower);
	}
}
