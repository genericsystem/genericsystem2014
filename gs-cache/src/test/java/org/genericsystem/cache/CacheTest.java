package org.genericsystem.cache;

import java.util.Arrays;
import org.genericsystem.cache.Generic.GenericImpl;
import org.testng.annotations.Test;

@Test
public class CacheTest extends AbstractTest {

	public void test3() {
		Engine engine = new Engine();

		assert engine.isRoot();
		GenericImpl vehicle = engine.addInstance("Vehicle");
		GenericImpl car = engine.addInstance(new GenericImpl[] { vehicle }, "Car", new GenericImpl[] {});
		GenericImpl vehiclePower = engine.addInstance("VehiclePower", vehicle);
		GenericImpl carPower = engine.addInstance("CarPower", car);
		assert car.getAttributes(engine).containsAll(Arrays.asList(vehiclePower, carPower)) : car.getAttributes(engine);
		assert car.getAttributes(engine).size() == 2;
	}
}
