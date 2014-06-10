package org.genericsystem.kernel;

import java.util.Collections;
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
		assert engine.computeAllDependencies().contains(newVehicle);
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

		LinkedHashSet<Vertex> engineAliveDependencies = newVehicle.computeAllDependencies();
		assert engineAliveDependencies.size() == 3;
		assert !engineAliveDependencies.contains(vehicle);
		assert !engineAliveDependencies.contains(car);
		assert !engineAliveDependencies.contains(newBeetle);

		Vertex vertex1asNewVehicle = findElement(newValue, engineAliveDependencies.stream().collect(Collectors.toList()));
		assert vertex1asNewVehicle != null;
		assert engine.equals(vertex1asNewVehicle.getMeta());

		Vertex vertex2asNewCar = findElement(valueCar, engineAliveDependencies.stream().collect(Collectors.toList()));
		assert vertex2asNewCar != null;
		assert vertex1asNewVehicle.equals(vertex2asNewCar.getMeta());

		Vertex vertex3asNewNewBeetle = findElement(valueNewBeetle, engineAliveDependencies.stream().collect(Collectors.toList()));
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

		LinkedHashSet<Vertex> engineAliveDependencies = newCar.computeAllDependencies();
		assert engineAliveDependencies.size() == 2;
		assert !engineAliveDependencies.contains(car);
		assert !engineAliveDependencies.contains(newBeetle);

		Vertex vertex1asNewCar = findElement(newValue, engineAliveDependencies.stream().collect(Collectors.toList()));
		assert vertex1asNewCar != null;
		assert vehicle.equals(vertex1asNewCar.getMeta());

		Vertex vertex2asNewNewBeetle = findElement(valueNewBeetle, engineAliveDependencies.stream().collect(Collectors.toList()));
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
		assert engine.computeAllDependencies().contains(newVehicle);
		assert newVehicle.computeAllDependencies().size() == 2;
		assert newVehicle.computeAllDependencies().contains(newVehicle);
		Vertex newOptions = newVehicle.computeAllDependencies().stream().collect(Collectors.toList()).get(0);
		assert newOptions.isAlive();
		if (newValue.equals(newOptions.getValue()))
			newOptions = newVehicle.computeAllDependencies().stream().collect(Collectors.toList()).get(1);
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
		assert engine.computeAllDependencies().contains(newVehicle);
		Vertex newPower = findElement("Power", engine.computeAllDependencies().stream().collect(Collectors.toList()));
		assert newPower.getComponentsStream().count() == 1;
		Vertex componentOfPower = newPower.getComponents().get(0);
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
		assert engine.equals(power.getMeta());
		assert vehicle.equals(car.getMeta());

		assert newValue.equals(newMachine.getValue());
		assert newMachine.getComponents().size() == 0;
		assert newMachine.getSupersStream().count() == 0;
		assert newMachine.computeAllDependencies().size() == 4;
		assert newMachine.getInstances().size() == 0;
		assert newMachine.getInheritings().size() == 1;

		assert engine.getComponents().size() == 0;
		assert engine.getSupersStream().count() == 0;
		assert engine.computeAllDependencies().size() == 5;
		assert engine.getInstances().size() == 3;
		assert engine.getInheritings().size() == 0;

		Vertex newVehicle = findElement("Vehicle", newMachine.computeAllDependencies().stream().collect(Collectors.toList()));
		assert newVehicle != null;
		assert newVehicle.getComponents().size() == 0;
		assert newVehicle.getSupersStream().count() == 1;
		assert newVehicle.computeAllDependencies().size() == 3;
		assert newVehicle.getInstances().size() == 1;
		assert newVehicle.getInheritings().size() == 0;

		Vertex newPower = findElement("Power", newMachine.computeAllDependencies().stream().collect(Collectors.toList()));
		assert newPower != null;
		assert newPower.getComponents().size() == 1;
		assert newPower.getSupersStream().count() == 0;
		assert newPower.computeAllDependencies().size() == 1;
		assert newPower.getInstances().size() == 0;
		assert newPower.getInheritings().size() == 0;

		Vertex newCar = findElement("Car", newMachine.computeAllDependencies().stream().collect(Collectors.toList()));
		assert newCar != null;
		assert newCar.getComponents().size() == 0;
		assert newCar.getSupersStream().count() == 0;
		assert newCar.computeAllDependencies().size() == 1;
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

		LinkedHashSet<Vertex> engineDependencies = engine.computeAllDependencies();
		assert engineDependencies.size() == 3;
		assert engine.getAllInstances().count() == 2;

		Vertex newVehicle = findElement("Vehicle", engineDependencies.stream().collect(Collectors.toList()));
		LinkedHashSet<Vertex> newVehicleDependencies = newVehicle.computeAllDependencies();
		assert newVehicleDependencies.size() == 2;
		assert newVehicle.getInheritings().size() == 1;

		Vertex newCar = findElement("Car", newVehicleDependencies.stream().collect(Collectors.toList()));
		assert newCar.computeAllDependencies().size() == 1;
		assert newCar.getSupersStream().count() == 1;
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

		LinkedHashSet<Vertex> engineDependencies = engine.computeAllDependencies();
		assert engineDependencies.size() == 4;
		assert engine.getAllInstances().count() == 3;

		Vertex newVehicle = findElement("Vehicle", engineDependencies.stream().collect(Collectors.toList()));
		LinkedHashSet<Vertex> newVehicleDependencies = newVehicle.computeAllDependencies();
		assert newVehicleDependencies.size() == 3;
		assert newVehicle.getInheritings().size() == 1;

		Vertex newFourWheels = findElement("FourWheels", engineDependencies.stream().collect(Collectors.toList()));
		LinkedHashSet<Vertex> newFourWheelsDependencies = newFourWheels.computeAllDependencies();
		assert newFourWheelsDependencies.size() == 2;
		assert newFourWheels.getInheritings().size() == 1;
		assert newFourWheels.getSupersStream().count() == 1;

		Vertex newCar = findElement("Car", newVehicleDependencies.stream().collect(Collectors.toList()));
		assert newCar.computeAllDependencies().size() == 1;
		assert newCar.getSupersStream().count() == 1;
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

		LinkedHashSet<Vertex> engineDependencies = engine.computeAllDependencies();
		assert engineDependencies.size() == 3;
		assert engine.getAllInstances().count() == 2;

		Vertex newVehicle = findElement("Vehicle", engineDependencies.stream().collect(Collectors.toList()));
		LinkedHashSet<Vertex> newVehicleDependencies = newVehicle.computeAllDependencies();
		assert newVehicleDependencies.size() == 2;
		assert newVehicle.getInheritings().size() == 1;

		Vertex newCar = findElement("Car", engineDependencies.stream().collect(Collectors.toList()));
		assert newCar.computeAllDependencies().size() == 1;
		assert newCar.getSupersStream().count() == 1;
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

		LinkedHashSet<Vertex> engineDependencies = engine.computeAllDependencies();
		assert engineDependencies.size() == 15;
		assert engine.getAllInstances().count() == 7;

		Vertex newVehicle = findElement("Vehicle", engineDependencies.stream().collect(Collectors.toList()));
		LinkedHashSet<Vertex> newVehicleDependencies = newVehicle.computeAllDependencies();
		assert newVehicleDependencies.size() == 9;
		assert newVehicle.getInheritings().size() == 1;

		Vertex newFourWheels = findElement("FourWheels", engineDependencies.stream().collect(Collectors.toList()));
		assert newFourWheels.computeAllDependencies().size() == 5;
		assert newFourWheels.getInheritings().size() == 1;
		assert newFourWheels.getSupersStream().count() == 1;

		Vertex newCar = findElement("Car", engineDependencies.stream().collect(Collectors.toList()));
		assert newCar.computeAllDependencies().size() == 4;
		assert newCar.getSupersStream().count() == 1;
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

		LinkedHashSet<Vertex> engineDependencies = engine.computeAllDependencies();
		assert engineDependencies.size() == 13;
		assert engine.getAllInstances().count() == 5;

		Vertex newCarBlue = findElement("MyCarRed", engineDependencies.stream().collect(Collectors.toList()));
		assert newCarBlue.computeAllDependencies().size() == 1;
		List<Vertex> newCarBlueComponents = newCarBlue.getComponents();
		assert newCarBlueComponents.size() == 2;

		Vertex blueFromNewCarBlueComponent = findElement("Blue", newCarBlueComponents.stream().collect(Collectors.toList()));
		assert blueFromNewCarBlueComponent != null;

		Vertex myCarFromNewCarBlueComponent = findElement("MyCar", newCarBlueComponents.stream().collect(Collectors.toList()));
		assert myCarFromNewCarBlueComponent != null;
	}

	public void test201_replaceComponent_KO() {
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
		myCarRed.update(Collections.emptyList(), "MyCarBlue", myCar, blue);

		// then
		LinkedHashSet<Vertex> engineDependencies = engine.computeAllDependencies();
		assert engineDependencies.size() == 9;
		assert engine.getAllInstances().count() == 4;

		Vertex newCarRed = findElement("MyCarRed", engineDependencies.stream().collect(Collectors.toList()));
		assert newCarRed == null;

		Vertex newCarBlue = findElement("MyCarBlue", engineDependencies.stream().collect(Collectors.toList()));
		assert newCarBlue.computeAllDependencies().size() == 1;
		List<Vertex> newCarBlueComponents = newCarBlue.getComponents();
		assert newCarBlueComponents.size() == 2;

		Vertex redFromNewCarBlueComponent = findElement("Red", newCarBlueComponents.stream().collect(Collectors.toList()));
		assert redFromNewCarBlueComponent == null;

		Vertex blueFromNewCarBlueComponent = findElement("Blue", newCarBlueComponents.stream().collect(Collectors.toList()));
		assert blueFromNewCarBlueComponent != null;

		Vertex myCarFromNewCarBlueComponent = findElement("MyCar", newCarBlueComponents.stream().collect(Collectors.toList()));
		assert myCarFromNewCarBlueComponent != null;
	}

	// public void test301_replaceComponentWithValueModification_KO() {
	// Vertex engine = new Root();
	// Vertex vehicle = engine.addInstance("Vehicle");
	// Vertex car = engine.addInstance(vehicle, "Car");
	// Vertex myCar = car.addInstance("MyCar");
	// Vertex color = engine.addInstance("Color");
	// Vertex red = color.addInstance("Red");
	// Vertex green = color.addInstance("Green");
	// Vertex blue = color.addInstance("Blue");
	// Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
	// Vertex myCarRed = vehicleColor.addInstance("MyCarRed", myCar, red);
	//
	// new RollbackCatcher() {
	// @Override
	// public void intercept() {
	// // when
	// myCarRed.replaceComponentWithValueModification(green, blue, "MyCarBlue");
	// }
	// // then
	// }.assertIsCausedBy(NotFoundException.class);
	// }

}
