package org.genericsystem.cache;

import org.testng.annotations.Test;

@Test
public class CacheTest extends AbstractTest {

	public void testTypeInheritings() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		assert vehicle.isAlive();
		Generic car = engine.addInstance(vehicle, "Car");
		assert vehicle.getInheritings().stream().anyMatch(car::equals);
	}

	public void testCacheInheritings() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		assert vehicle.getVertex() == null;
		Generic car = engine.addInstance(vehicle, "Car");
		assert vehicle.getInheritings().filter(car::equiv).size() == 1;
	}
}
