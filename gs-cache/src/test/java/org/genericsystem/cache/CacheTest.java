package org.genericsystem.cache;

import java.util.stream.Collectors;

import org.genericsystem.api.exception.CacheNoStartedException;
import org.testng.annotations.Test;

@Test
public class CacheTest extends AbstractTest {

	public void test001_getInheritings() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		assert vehicle.isAlive();
		Generic car = engine.addInstance(vehicle, "Car");
		assert vehicle.getInheritings().get().anyMatch(car::equals);
	}

	public void test001_getInstances() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		assert vehicle.isAlive();
		assert engine.getInstances().get().anyMatch(g -> g.equals(vehicle));
	}

	public void test001_getMetaComponents() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic powerVehicle = engine.addInstance("power", vehicle);
		assert vehicle.getComposites().contains(powerVehicle);
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic myVehicle123 = powerVehicle.addInstance("123", myVehicle);
		assert myVehicle.getComposites().contains(myVehicle123);
	}

	public void test001_getSuperComponents() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic powerVehicle = engine.addInstance("power", vehicle);
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic vehicle256 = powerVehicle.addInstance("256", vehicle);
		Generic myVehicle123 = powerVehicle.addInstance(vehicle256, "123", myVehicle);
		assert myVehicle123.inheritsFrom(vehicle256);
		assert myVehicle.getComposites().contains(myVehicle123);
	}

	public void test002_getSuperComponents() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic powerVehicle = engine.addInstance("power", vehicle);
		powerVehicle.enablePropertyConstraint();
		assert powerVehicle.isPropertyConstraintEnabled();
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic vehicle256 = powerVehicle.addInstance("256", vehicle);
		Generic myVehicle123 = powerVehicle.addInstance("123", myVehicle);
		assert !vehicle256.equals(myVehicle123);
		assert myVehicle123.inheritsFrom(vehicle256);
		assert myVehicle.getComposites().contains(myVehicle123);
	}

	public void test002_flush() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		engine.addInstance(vehicle, "Car");
		assert vehicle.isAlive();
		engine.getCurrentCache().flush();
		assert vehicle.isAlive();
		assert vehicle.getMeta().isAlive();
	}

	public void test002_clear() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		engine.getCurrentCache().clear();
		assert !engine.getInstances().get().anyMatch(g -> g.equals(vehicle));
	}

	public void test001_mountNewCache_nostarted() {
		Engine engine = new Engine();
		Cache<Generic> currentCache = engine.getCurrentCache();
		Cache<Generic> mountNewCache = currentCache.mountAndStartNewCache();
		currentCache.start();

		catchAndCheckCause(() -> mountNewCache.flush(), CacheNoStartedException.class);
	}

	public void test002_mountNewCache() {
		Engine engine = new Engine();
		Cache<Generic> cache = engine.newCache().start();
		Cache<Generic> currentCache = engine.getCurrentCache();
		assert cache == currentCache;
		Cache<Generic> mountNewCache = currentCache.mountAndStartNewCache();
		engine.getCurrentCache();
		assert mountNewCache.getSubContext() == currentCache;
		assert mountNewCache != currentCache;
		engine.addInstance("Vehicle");
		assert currentCache == mountNewCache.flushAndUnmount();
		currentCache.flush();
	}

	public void test005_TwoComponentsWithSameMetaInDifferentCaches_remove() {
		Engine engine = new Engine();
		Cache<Generic> currentCache = engine.getCurrentCache();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic vehiclePower = engine.addInstance("vehiclePower", vehicle);
		Cache<Generic> mountNewCache = currentCache.mountAndStartNewCache();
		assert engine.getCurrentCache() == mountNewCache;
		assert vehiclePower.isAlive();
		assert !vehicle.getComposites().isEmpty() : vehicle.getComposites().get().collect(Collectors.toList());
		vehiclePower.remove();
		assert !vehiclePower.isAlive();
		assert vehicle.getComposites().isEmpty() : vehicle.getComposites().get().collect(Collectors.toList());
		mountNewCache.flush();
		assert vehicle.isAlive();
		assert !vehiclePower.isAlive();
		assert vehicle.getComposites().isEmpty() : vehicle.getComposites().get().collect(Collectors.toList());
	}
}
