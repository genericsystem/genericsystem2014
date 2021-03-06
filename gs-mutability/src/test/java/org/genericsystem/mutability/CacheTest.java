package org.genericsystem.mutability;

import java.util.stream.Collectors;

import org.testng.annotations.Test;

@Test
public class CacheTest extends AbstractTest {

	public void test000() {
		Engine engine = new Engine();
		Cache cache = engine.getCurrentCache();
		Generic vehicle = engine.addInstance("Vehicle");
		assert vehicle.isAlive();
		cache.flush();
		assert vehicle.isAlive();
		cache.flush();
		assert vehicle.isAlive();
		cache.clear();
		assert vehicle.isAlive();
		cache.clear();
		assert vehicle.isAlive();
	}

	public void test001() {
		Engine engine = new Engine();
		Cache cache = engine.getCurrentCache();
		Generic vehicle = engine.addInstance("Vehicle");
		assert vehicle.isAlive();
		cache.clear();
		assert !vehicle.isAlive();
		cache.flush();
		assert !vehicle.isAlive();
		cache.clear();
		assert !vehicle.isAlive();
	}

	public void test002() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		assert vehicle.isAlive();
		engine.getCurrentCache().mount();
		assert vehicle.isAlive();
		engine.getCurrentCache().clear();
		assert vehicle.isAlive();
	}

	public void test0021() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		assert vehicle.isAlive();
		engine.getCurrentCache().mount();
		assert vehicle.isAlive();
		engine.getCurrentCache().unmount();
		assert vehicle.isAlive();
	}

	public void test003() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		assert vehicle.isAlive();
		engine.getCurrentCache().mount();
		vehicle.remove();
		assert !vehicle.isAlive();
		engine.getCurrentCache().clear();
		assert vehicle.isAlive();
	}

	public void test0031() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		assert vehicle.isAlive();
		engine.getCurrentCache().mount();
		vehicle.remove();
		assert !vehicle.isAlive();
		engine.getCurrentCache().unmount();
		assert vehicle.isAlive();
	}

	public void test004() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		assert vehicle.isAlive();
		engine.getCurrentCache().mount();
		assert vehicle.isAlive();
		Generic generic = vehicle.update("Vehicle2");
		assert generic == vehicle;
		assert "Vehicle2".equals(vehicle.getValue());
		assert vehicle.isAlive();
		engine.getCurrentCache().clear();
		assert vehicle.isAlive();
		assert "Vehicle".equals(vehicle.getValue());
	}

	public void test0041() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		assert vehicle.isAlive();
		engine.getCurrentCache().mount();
		assert vehicle.isAlive();
		Generic generic = vehicle.update("Vehicle2");
		assert generic == vehicle;
		assert "Vehicle2".equals(vehicle.getValue());
		assert vehicle.isAlive();
		engine.getCurrentCache().unmount();
		assert vehicle.isAlive();
		assert "Vehicle".equals(vehicle.getValue());
	}

	public void test005() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		assert vehicle.isAlive();
		Generic generic = vehicle.update("Vehicle2");
		assert generic == vehicle;
		assert "Vehicle2".equals(vehicle.getValue());
		assert vehicle.isAlive();
	}

	public void test001_getInheritings() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		assert vehicle.isAlive();
		Generic car = engine.addInstance(vehicle, "Car");
		assert vehicle.getInheritings().stream().anyMatch(car::equals);
	}

	public void test001_getInstances() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		assert vehicle.isAlive();
		assert engine.getInstances().stream().anyMatch(g -> g.equals(vehicle));
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
		assert !engine.getInstances().stream().anyMatch(g -> g.equals(vehicle));
	}

	public void test002_mountNewCache() {
		Engine engine = new Engine();
		Cache cache = engine.newCache().start();
		Cache currentCache = engine.getCurrentCache();
		assert cache == currentCache;
		currentCache.mount();
		engine.addInstance("Vehicle");
		currentCache.unmount();
		currentCache.flush();
	}

	public void test005_TwoComponentsWithSameMetaInDifferentCaches_remove() {
		Engine engine = new Engine();
		Cache currentCache = engine.getCurrentCache();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic vehiclePower = engine.addInstance("vehiclePower", vehicle);
		currentCache.mount();
		assert vehiclePower.isAlive();
		assert !vehicle.getComposites().isEmpty() : vehicle.getComposites().stream().collect(Collectors.toList());
		vehiclePower.remove();
		assert !vehiclePower.isAlive();
		assert vehicle.getComposites().isEmpty() : vehicle.getComposites().stream().collect(Collectors.toList());
		currentCache.flush();
		assert vehicle.isAlive();
		assert !vehiclePower.isAlive();
		assert vehicle.getComposites().isEmpty() : vehicle.getComposites().stream().collect(Collectors.toList());
	}
}
