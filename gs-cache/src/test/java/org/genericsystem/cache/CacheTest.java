package org.genericsystem.cache;

import org.testng.annotations.Test;

@Test
public class CacheTest extends AbstractTest {

	public void getInheritings() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		assert vehicle.isAlive();
		assert vehicle.getVertex() == null;
		Generic car = engine.addInstance(vehicle, "Car");
		assert vehicle.getInheritings().stream().anyMatch(g -> g.equals(car));
	}

	public void getInstances() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		assert vehicle.isAlive();
		assert vehicle.getVertex() == null;
		assert engine.getInstances().stream().anyMatch(g -> g.equals(vehicle));
	}

	public void flush() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		assert vehicle.isAlive();
		assert vehicle.getVertex() == null;
		engine.getCurrentCache().flush();
		assert vehicle.isAlive();
		assert vehicle.getMeta().isAlive();
		assert vehicle.getMeta().getVertex() != null;
		assert vehicle.getVertex() != null;
	}
}
