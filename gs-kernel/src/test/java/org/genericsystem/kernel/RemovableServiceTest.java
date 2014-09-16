package org.genericsystem.kernel;

import org.genericsystem.api.exception.ReferentialIntegrityConstraintViolationException;
import org.testng.annotations.Test;

@Test
public class RemovableServiceTest extends AbstractTest {

	public void test100_remove_instance_NormalStrategy() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex myVehicule = engine.addInstance("MyVehicule");

		// when
		myVehicule.remove();

		// then
		assert vehicle.isAlive();
		assert !myVehicule.isAlive();
		// assert engine.computeAllDependencies().stream().count() == 2;
		assert engine.computeDependencies().contains(engine);
		assert engine.computeDependencies().contains(vehicle);
		assert vehicle.computeDependencies().stream().count() == 1;
		assert vehicle.computeDependencies().contains(vehicle);
	}

	public void test101_remove_instance_NormalStrategy() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex myVehicule1 = vehicle.addInstance("MyVehicule1");
		Vertex myVehicule2 = vehicle.addInstance("MyVehicule2");
		Vertex myVehicule3 = vehicle.addInstance("MyVehicule3");

		// when
		myVehicule2.remove();
		myVehicule1.remove();

		// then
		assert vehicle.isAlive();
		assert !myVehicule1.isAlive();
		assert !myVehicule2.isAlive();
		assert myVehicule3.isAlive();
		// assert engine.computeAllDependencies().stream().count() == 3;
		assert engine.computeDependencies().contains(engine);
		assert engine.computeDependencies().contains(vehicle);
		assert vehicle.computeDependencies().stream().count() == 2;
		assert vehicle.computeDependencies().contains(vehicle);
		assert vehicle.computeDependencies().contains(myVehicule3);
		assert myVehicule3.computeDependencies().stream().count() == 1;
		assert myVehicule3.computeDependencies().contains(myVehicule3);
	}

	public void test102_remove_typeWithInstance() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		vehicle.addInstance("MyVehicule");

		// when
		new RollbackCatcher() {
			@Override
			public void intercept() {
				// when
				vehicle.remove();
			}
			// then
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	}

	public void test103_remove_SubType() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");

		// when
		car.remove();

		// then
		assert vehicle.isAlive();
		assert !car.isAlive();
		// assert engine.computeAllDependencies().stream().count() == 2;
		assert engine.computeDependencies().contains(engine);
		assert engine.computeDependencies().contains(vehicle);
		assert vehicle.computeDependencies().stream().count() == 1;
		assert vehicle.computeDependencies().contains(vehicle);
	}

	public void test104_remove_attribute() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex power = engine.addInstance("Power", vehicle);

		// when
		vehicle.remove();
		// then
		assert engine.isAlive();
		assert !vehicle.isAlive();
		assert !power.isAlive();
	}

	public void test105_remove_attribute_withInstance_KO() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		engine.addInstance("Power", vehicle);
		vehicle.addInstance("Car");

		// when
		new RollbackCatcher() {
			@Override
			public void intercept() {
				// when
				vehicle.remove();
			}
			// then
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	}

	public void test105_remove_attribute_attribute_KO() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex power = engine.addInstance("Power", vehicle);
		Vertex unit = engine.addInstance("Unit", power);

		assert vehicle.isAlive();
		assert power.isAlive();
		assert unit.isAlive();
		vehicle.remove();
		assert !vehicle.isAlive();
		assert !power.isAlive();
		assert !unit.isAlive();
	}

	public void test106_remove_TypeWithSubType_KO() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		engine.addInstance(vehicle, "Car");

		new RollbackCatcher() {
			@Override
			public void intercept() {
				// when
				vehicle.remove();
			}
			// then
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	}

	public void test107_remove_relation_KO() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("red");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		vehicleColor.addInstance("CarRed", car, red);

		new RollbackCatcher() {
			@Override
			public void intercept() {
				// when
				vehicleColor.remove();
			}
			// then
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	}

	public void test108_remove_relationFromTarget() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("red");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Vertex carRed = vehicleColor.addInstance("CarRed", car, red);

		// when
		red.remove();

		// then
		assert engine.isAlive();
		assert vehicle.isAlive();
		assert car.isAlive();
		assert color.isAlive();
		assert !red.isAlive();
		assert vehicleColor.isAlive();
		assert !carRed.isAlive();
	}

	public void test109_remove_link() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("red");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Vertex carRed = vehicleColor.addInstance("CarRed", car, red);

		// when
		carRed.remove();

		// then
		assert engine.isAlive();
		assert vehicle.isAlive();
		assert car.isAlive();
		assert color.isAlive();
		assert red.isAlive();
		assert vehicleColor.isAlive();
		assert !carRed.isAlive();
	}

	public void test120_remove_Type_ForceStrategy() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");

		// when
		vehicle.forceRemove();

		// then
		assert !vehicle.isAlive();
		// assert engine.computeAllDependencies().stream().count() == 1;
		assert engine.computeDependencies().contains(engine);
	}

	public void test121_remove_typeWithInstance_ForceStrategy() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex myVehicle = vehicle.addInstance("MyVehicule");

		// when
		vehicle.forceRemove();
		// then
		assert !vehicle.isAlive();
		assert !myVehicle.isAlive();
		// assert engine.computeAllDependencies().stream().count() == 1;
		assert engine.computeDependencies().contains(engine);
	}

	public void test122_remove_TypeWithSubType_ForceStrategy() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");

		// when
		vehicle.forceRemove();

		// then
		assert !vehicle.isAlive();
		assert !car.isAlive();
		// assert engine.computeAllDependencies().stream().count() == 1;
		assert engine.computeDependencies().contains(engine);
	}

	public void test123_remove_attribute_ForceStrategy() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex power = engine.addInstance("Power", vehicle);

		// when
		vehicle.forceRemove();

		// then
		assert engine.isAlive();
		assert !vehicle.isAlive();
		assert !power.isAlive();
		// assert engine.computeAllDependencies().stream().count() == 1;
		assert engine.computeDependencies().contains(engine);
		assert !engine.computeDependencies().contains(vehicle);
		assert !engine.computeDependencies().contains(power);
	}

	public void test124_remove_relation_ForceStrategy() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("red");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Vertex carRed = vehicleColor.addInstance("CarRed", car, red);

		// when
		vehicleColor.forceRemove();

		// then
		assert engine.isAlive();
		assert vehicle.isAlive();
		assert car.isAlive();
		assert color.isAlive();
		assert red.isAlive();
		assert !vehicleColor.isAlive();
		assert !carRed.isAlive();
	}

	public void test125_remove_instanceBaseOfRelation_ForceStrategy() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("red");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Vertex carRed = vehicleColor.addInstance("CarRed", car, red);

		// when
		car.forceRemove();

		// then
		assert engine.isAlive();
		assert vehicle.isAlive();
		assert !car.isAlive();
		assert color.isAlive();
		assert red.isAlive();
		assert vehicleColor.isAlive();
		assert !carRed.isAlive();
	}

	public void test126_remove_instanceBaseOfRelation_ForceStrategy() {
		// given
		Vertex engine = new Root();
		Vertex vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.addInstance(vehicle, "Car");
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("red");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		Vertex carRed = vehicleColor.addInstance("CarRed", car, red);

		// when
		red.forceRemove();

		// then
		assert engine.isAlive();
		assert vehicle.isAlive();
		assert car.isAlive();
		assert color.isAlive();
		assert !red.isAlive();
		assert vehicleColor.isAlive();
		assert !carRed.isAlive();
	}

	public void test127_remove_typeWithlinks_ForceStrategy() {
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
		Vertex myCarRed = vehicleColor.addInstance("myCarRed", myCar, red);
		Vertex myVehicleGreen = vehicleColor.addInstance("myCarRed", myVehicle, green);

		// when
		vehicle.forceRemove();

		// then
		assert engine.isAlive();
		assert !vehicle.isAlive();
		assert animals.isAlive();
		assert !myVehicle.isAlive();
		assert !car.isAlive();
		assert !power.isAlive();
		assert !myCar.isAlive();
		assert color.isAlive();
		assert red.isAlive();
		assert green.isAlive();
		assert blue.isAlive();
		assert !vehicleColor.isAlive();
		assert !myCarRed.isAlive();
		assert !myVehicleGreen.isAlive();
	}

	// public void test130_remove_Type_ConserveStrategy() {
	// // given
	// Vertex engine = new Root();
	// Vertex vehicle = engine.addInstance("Vehicle");
	//
	// // when
	// vehicle.remove(RemoveStrategy.CONSERVE);
	//
	// // then
	// assert !vehicle.isAlive();
	// // assert engine.computeAllDependencies().stream().count() == 1;
	// assert engine.computeAllDependencies().contains(engine);
	// }
	//
	// public void test131_remove_SubType_ConserveStrategy() {
	// // given
	// Vertex engine = new Root();
	// Vertex vehicle = engine.addInstance("Vehicle");
	// Vertex car = vehicle.addInstance("Car");
	//
	// // when
	// vehicle.remove(RemoveStrategy.CONSERVE);
	//
	// // then
	// assert !vehicle.isAlive();
	// assert !car.isAlive();
	//
	// List<Vertex> engineDependencies = engine.computeAllDependencies().stream().collect(Collectors.toList());
	// // assert engineDependencies.size() == 2;
	// Vertex newCar = engine.getInstance("Car");
	// assert newCar.isAlive();
	// assert "Car".equals(newCar.getValue());
	// assert newCar.computeAllDependencies().size() == 1;
	// assert newCar.computeAllDependencies().contains(newCar);
	// }
	//
	// public void test132_remove_with2SubTypes_ConserveStrategy() {
	// // given
	// Vertex engine = new Root();
	// Vertex vehicle = engine.addInstance("Vehicle");
	// Vertex car = engine.addInstance(vehicle, "Car");
	// Vertex automatic = engine.addInstance(vehicle, "Automatic");
	//
	// // when
	// vehicle.remove(RemoveStrategy.CONSERVE);
	//
	// // then
	// assert !vehicle.isAlive();
	// assert !car.isAlive();
	// assert !automatic.isAlive();
	//
	// List<Vertex> engineDependencies = engine.computeAllDependencies().stream().collect(Collectors.toList());
	// // assert engineDependencies.size() == 3;
	// // assert engine.getAllInstances().count() == 2 : engine.getAllInstances();
	//
	// Vertex newCar = engine.getInstance("Car");
	// assert newCar.isAlive();
	// assert "Car".equals(newCar.getValue());
	// assert newCar.computeAllDependencies().size() == 1;
	// assert newCar.getAllInstances().count() == 0;
	// assert newCar.computeAllDependencies().contains(newCar);
	//
	// Vertex newAutomatic = engine.getInstance("Automatic");
	// assert newAutomatic.isAlive();
	// assert "Automatic".equals(newAutomatic.getValue());
	// assert newAutomatic.computeAllDependencies().size() == 1;
	// assert newAutomatic.getAllInstances().count() == 0;
	// assert newAutomatic.computeAllDependencies().contains(newAutomatic);
	// }
	//
	// public void test133_remove_SubSubTypes_ConserveStrategy() {
	// // given
	// Vertex engine = new Root();
	// Vertex vehicle = engine.addInstance("Vehicle");
	// Vertex car = engine.addInstance(vehicle, "Car");
	// Vertex automatic = engine.addInstance(car, "Automatic");
	//
	// // when
	// vehicle.remove(RemoveStrategy.CONSERVE);
	//
	// // then
	// assert !vehicle.isAlive();
	// assert !car.isAlive();
	// assert !automatic.isAlive();
	//
	// List<Vertex> engineDependencies = engine.computeAllDependencies().stream().collect(Collectors.toList());
	// // assert engineDependencies.size() == 3;
	// // assert engine.getAllInstances().count() == 2;
	//
	// Vertex newCar = engine.getInstance("Car");
	// assert newCar.isAlive();
	// assert "Car".equals(newCar.getValue());
	// assert newCar.computeAllDependencies().size() == 2;
	// assert newCar.getSupersStream().count() == 0;
	//
	// Vertex newAutomatic = engine.getInstance("Automatic");
	// assert newAutomatic.isAlive();
	// assert "Automatic".equals(newAutomatic.getValue());
	// assert newAutomatic.computeAllDependencies().size() == 1;
	// assert newAutomatic.getSupersStream().count() == 1;
	// assert newAutomatic.getSupers().contains(newCar);
	// }
	//
	// public void test134_remove_TypeWithAttribute_ConserveStrategy() {
	// // given
	// Vertex engine = new Root();
	// Vertex vehicle = engine.addInstance("Vehicle");
	// Vertex car = vehicle.addInstance("Car");
	// Vertex options = engine.addInstance(vehicle, "Options");
	//
	// // when
	// vehicle.remove(RemoveStrategy.CONSERVE);
	//
	// // then
	// assert !vehicle.isAlive();
	// assert !options.isAlive();
	// assert !car.isAlive();
	//
	// List<Vertex> engineDependencies = engine.computeAllDependencies().stream().collect(Collectors.toList());
	// // assert engineDependencies.size() == 3;
	// // assert engine.getAllInstances().count() == 2;
	//
	// Vertex newCar = engine.getInstance("Car");
	// Vertex newOptions = engine.getInstance("Options");
	// assert newCar != null;
	// assert newCar.getInheritings().stream().count() == 1;
	// assert newOptions.equals(newCar.getInheritings().stream().collect(Collectors.toList()).get(0));
	//
	// assert newOptions != null;
	// assert newOptions.getSupersStream().count() == 1;
	// assert newCar.equals(newOptions.getSupers().get(0));
	//
	// }
}
