package org.genericsystem.cache;

import java.util.Arrays;
import org.testng.annotations.Test;

@Test
public class CacheTest extends AbstractTest {

	public void test3() {
		Engine engine = new Engine();

		assert engine.isRoot();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(new Generic[] { vehicle }, "Car");
		Generic vehiclePower = engine.addInstance("VehiclePower", vehicle);
		Generic carPower = engine.addInstance("CarPower", car);
		assert car.getAttributes(engine).containsAll(Arrays.asList(carPower, vehiclePower)) : car.getAttributes(engine);
		assert car.getAttributes(engine).size() == 2;
	}
}
