package org.genericsystem.cache;

import java.util.Collections;

import org.genericsystem.api.exception.AliveConstraintViolationException;
import org.genericsystem.api.exception.ExistsException;
import org.genericsystem.api.exception.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.kernel.Generic;
import org.testng.annotations.Test;

@Test
public class LazyTest extends AbstractTest {

	public void test_003() {
		Engine engine = new Engine();

		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic blue = color.addInstance("blue");

		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);
		engine.getCurrentCache().flush();
		LazyHandler myLazyCar = new LazyHandler(engine.getCurrentCache(), vehicle, Collections.emptyList(), "myLazyCar", Collections.emptyList());
		// myLazyCar.resolve();
		engine.getCurrentCache().clear();

	}

	public void test_004() {
		Engine engine = new Engine();

		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic blue = color.addInstance("blue");

		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);
		engine.getCurrentCache().flush();
		LazyHandler myLazyCarHandler = new LazyHandler(engine.getCurrentCache(), vehicle, Collections.emptyList(), "myLazyCar", Collections.emptyList());
		assert myLazyCarHandler instanceof LazyHandler;
		assert vehicle.getInstances().size() == 1;
		engine.getCurrentCache().flush();
		assert vehicle.getInstances().size() == 0;

	}

	public void test_005() {
		Engine engine = new Engine();

		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic blue = color.addInstance("blue");

		Generic vehicleColor = vehicle.addRelation("VehicleColor", color);
		engine.getCurrentCache().flush();
		Generic myLazyCar = new LazyHandler(engine.getCurrentCache(), vehicle, Collections.emptyList(), "myLazyCar", Collections.emptyList());
		assert vehicle.getInstances().size() == 1;
		assert myLazyCar instanceof LazyHandler;
		myLazyCar.addLink(vehicleColor, "myBlueCar", blue);
		engine.getCurrentCache().flush();
		assert vehicle.getInstances().size() == 1 : vehicle.getInstances().info();

	}

	public void test_006() {
		Engine engine = new Engine();

		Generic car = engine.addInstance("Car");
		Generic myLazyCar = new LazyHandler(engine.getCurrentCache(), car, Collections.emptyList(), "myLazyCar", Collections.emptyList());
		myLazyCar.remove();
		engine.getCurrentCache().flush();
	}

	public void test_007() {
		Engine engine = new Engine();
		Generic car = engine.addInstance("Car");
		new LazyHandler(engine.getCurrentCache(), car, Collections.emptyList(), "myLazyCar", Collections.emptyList());
		catchAndCheckCause(() -> car.addInstance("myLazyCar"), ExistsException.class);
	}

	public void test_isAlive_001() {
		Engine engine = new Engine();
		Generic car = engine.addInstance("Car");
		Generic myLazyCar = new LazyHandler(engine.getCurrentCache(), car, Collections.emptyList(), "myLazyCar", Collections.emptyList());
		assert myLazyCar.isAlive();
		engine.getCurrentCache().flush();
		assert !myLazyCar.isAlive();
	}

	public void test_isAlive_002() {
		Engine engine = new Engine();
		Generic lazyCar = new LazyHandler(engine.getCurrentCache(), engine, Collections.emptyList(), "LazyCar", Collections.emptyList());
		assert lazyCar.isAlive();
		Generic myCar = lazyCar.addInstance("myCar");
		assert lazyCar.isAlive();
		assert myCar.isAlive();
		engine.getCurrentCache().flush();
		assert lazyCar.isAlive();
		assert myCar.isAlive();
		assert engine.getInstance("LazyCar").isAlive() == true;
	}

	public void test_remove_001() {
		Engine engine = new Engine();
		Generic lazyCar = new LazyHandler(engine.getCurrentCache(), engine, Collections.emptyList(), "LazyCar", Collections.emptyList());
		Generic myCar = lazyCar.addInstance("myCar");
		engine.getCurrentCache().flush();
		myCar.remove();
		lazyCar.remove();
		assert !lazyCar.isAlive();
	}

	public void test_remove_002() {
		Engine engine = new Engine();
		Generic lazyCar = new LazyHandler(engine.getCurrentCache(), engine, Collections.emptyList(), "LazyCar", Collections.emptyList());
		assert lazyCar.isAlive();
		Generic myCar = lazyCar.addInstance("myCar");
		myCar.remove();
		lazyCar.remove();
		engine.getCurrentCache().flush();
		catchAndCheckCause(() -> lazyCar.remove(), AliveConstraintViolationException.class);
	}

	public void test_remove_003() {
		Engine engine = new Engine();
		Generic lazyCar = new LazyHandler(engine.getCurrentCache(), engine, Collections.emptyList(), "LazyCar", Collections.emptyList());
		lazyCar.remove();
		engine.getCurrentCache().flush();
		catchAndCheckCause(() -> lazyCar.remove(), AliveConstraintViolationException.class);
	}

	public void test_remove_004() {
		Engine engine = new Engine();
		Generic lazyCar = new LazyHandler(engine.getCurrentCache(), engine, Collections.emptyList(), "LazyCar", Collections.emptyList());
		assert lazyCar.isAlive();
		lazyCar.addInstance("myCar");
		catchAndCheckCause(() -> lazyCar.remove(), ReferentialIntegrityConstraintViolationException.class);
	}

	public void test_conserveRemove_001() {
		Engine engine = new Engine();
		Generic lazyCar = new LazyHandler(engine.getCurrentCache(), engine, Collections.emptyList(), "LazyCar", Collections.emptyList());
		Generic myCar = lazyCar.addInstance("myCar");
		engine.getCurrentCache().flush();
		myCar.remove();
		lazyCar.conserveRemove();
		assert !lazyCar.isAlive();
	}

	public void test_forceRemove_001() {
		Engine engine = new Engine();
		Generic lazyCar = new LazyHandler(engine.getCurrentCache(), engine, Collections.emptyList(), "LazyCar", Collections.emptyList());
		Generic myCar = lazyCar.addInstance("myCar");
		engine.getCurrentCache().flush();
		myCar.remove();
		lazyCar.forceRemove();
		assert !lazyCar.isAlive();
	}

	public void test_comps() {
		Engine engine = new Engine();
		Generic lazyCar = new LazyHandler(engine.getCurrentCache(), engine, Collections.emptyList(), "LazyCar", Collections.emptyList());
		Generic myCar = lazyCar.addInstance("myCar");
		engine.getCurrentCache().flush();

		Generic color = engine.addInstance("Color");
		Generic blue = color.addInstance("blue");
		Generic vehicleColor = color.addRelation("CarColor", lazyCar);
	}

}
