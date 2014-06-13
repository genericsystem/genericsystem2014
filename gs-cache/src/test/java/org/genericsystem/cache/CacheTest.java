package org.genericsystem.cache;

import org.testng.annotations.Test;

@Test
public class CacheTest extends AbstractTest {

	public void test001_getInheritings() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		assert vehicle.isAlive();
		assert vehicle.getVertex() == null;
		Generic car = engine.addInstance(vehicle, "Car");

		assert vehicle.getInheritings().stream().anyMatch(car::equals);
	}

	public void test001_getInstances() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		assert vehicle.isAlive();
		assert vehicle.getVertex() == null;
		assert engine.getInstances().stream().anyMatch(g -> g.equals(vehicle));
	}

	public void test001_getMetaComposites() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic powerVehicle = engine.addInstance("power", vehicle);
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic myVehicle123 = powerVehicle.addInstance("myVehicle123", myVehicle);

		assert myVehicle.getMetaComposites(powerVehicle).stream().anyMatch(g -> g.equals(myVehicle123));
	}

	public void test002_flush() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(vehicle, "Car");
		assert vehicle.isAlive();
		assert vehicle.getVertex() == null;
		engine.getCurrentCache().flush();
		assert vehicle.isAlive();
		assert vehicle.getMeta().isAlive();
		assert vehicle.getMeta().getVertex() != null;
		assert vehicle.getVertex() != null;
		assert vehicle.getVertex().getInheritings().stream().anyMatch(car.getVertex()::equals);
	}

	public void test002_clear() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		engine.getCurrentCache().clear();
		assert !engine.getInstances().stream().anyMatch(g -> g.equals(vehicle));
	}

	public void test003_mountNewCache() {
		Engine engine = new Engine();
		Cache<Generic> currentCache = engine.getCurrentCache();
		Cache<Generic> mountNewCache = currentCache.mountNewCache();
		assert mountNewCache.getSubContext() == currentCache;
		Generic vehicle = engine.addInstance("Vehicle");
		assert currentCache == mountNewCache.flushAndUnmount();
		assert vehicle.getVertex() == null;
		currentCache.flush();
		assert vehicle.getVertex() != null;
	}
}
