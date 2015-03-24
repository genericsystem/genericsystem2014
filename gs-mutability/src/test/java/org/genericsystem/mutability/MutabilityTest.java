package org.genericsystem.mutability;

import org.testng.annotations.Test;

@Test
public class MutabilityTest extends AbstractTest {

	public void test001() {
		Engine engine = new Engine();
		assert "Engine".equals(engine.getValue());
	}

	public void test002() {
		Engine engine = new Engine();
		Generic car = engine.addInstance("Car");
		assert engine.getInstances().contains(car);
		assert engine.getInstances().size() == 1;
	}

	public void test003() {
		Engine engine = new Engine();
		Generic car = engine.addInstance("Car");
		assert engine.getInstances().contains(car);
		assert engine.getInstances().size() == 1;
	}

	public void test004() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("VehicleZ");
		Generic car = engine.addInstance(vehicle, "Car");
		vehicle.updateValue("Vehicle");
		assert vehicle.isAlive();
		assert car.isAlive();// Aie, dependencies have been rebuilt and should be alive !
	}

	public void test_fusion_then_mutation() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		Generic myAudi = car.addInstance("myAudi");
		Generic myMbw = car.addInstance("myMbw");
		assert engine.getCurrentCache().unwrap(myMbw) != engine.getCurrentCache().unwrap(myAudi);

		myAudi.update("myMbw");
		assert engine.getCurrentCache().unwrap(myMbw) == engine.getCurrentCache().unwrap(myAudi);
		assert myMbw != myAudi;
		assert myAudi.isAlive();
		assert myMbw.isAlive();

		myAudi.update("myMercedes");
		assert myAudi.isAlive();
		assert myMbw.isAlive();

		assert engine.getCurrentCache().unwrap(myMbw) == engine.getCurrentCache().unwrap(myAudi);

		myAudi.update("myTruc");
		assert myAudi.isAlive();
		assert myMbw.isAlive();

		assert engine.getCurrentCache().unwrap(myMbw) == engine.getCurrentCache().unwrap(myAudi);
	}

}
