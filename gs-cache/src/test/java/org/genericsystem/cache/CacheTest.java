package org.genericsystem.cache;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Vertex;
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
		assert vehicle.getMetaComposites(engine.getMetaAttribute()).contains(powerVehicle);
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic myVehicle123 = powerVehicle.addInstance("123", myVehicle);
		assert myVehicle.getMetaComposites(powerVehicle).contains(myVehicle123);
	}

	public void test001_getSuperComposites() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic powerVehicle = engine.addInstance("power", vehicle);
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic vehicle256 = powerVehicle.addInstance("256", vehicle);
		Generic myVehicle123 = powerVehicle.addInstance(vehicle256, "123", myVehicle);
		assert myVehicle123.inheritsFrom(vehicle256);
		assert myVehicle.getSuperComposites(vehicle256).contains(myVehicle123);
	}

	public void test002_getSuperComposites() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic powerVehicle = engine.addInstance("power", vehicle);
		powerVehicle.enablePropertyConstraint();
		assert powerVehicle.isPropertyConstraintEnabled();
		Generic myVehicle = vehicle.addInstance("myVehicle");
		Generic vehicle256 = powerVehicle.addInstance("256", vehicle);
		Generic myVehicle123 = powerVehicle.addInstance("123", myVehicle);
		assert !vehicle256.equiv(myVehicle123);
		assert myVehicle123.inheritsFrom(vehicle256);
		assert myVehicle.getSuperComposites(vehicle256).contains(myVehicle123);
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
		Cache<Generic, Engine, Vertex, Root> currentCache = engine.getCurrentCache();
		Cache<Generic, Engine, Vertex, Root> mountNewCache = currentCache.mountNewCache();
		assert mountNewCache.getSubContext() == currentCache;
		Generic vehicle = engine.addInstance("Vehicle");
		assert currentCache == mountNewCache.flushAndUnmount();
		assert ((AbstractGeneric<Generic, Engine, Vertex, Root>) vehicle).getVertex() == null;
		currentCache.flush();
		assert vehicle.getVertex() != null;
	}

	public void test004_TwoCompositesWithSameMetaInDifferentCaches() {
		Engine engine = new Engine();
		Cache<Generic, Engine, Vertex, Root> currentCache = engine.getCurrentCache();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic color = engine.addInstance("Color");
		Generic vehicleColor = color.addInstance("vehicleColor", vehicle);
		Cache<Generic, Engine, Vertex, Root> mountNewCache = currentCache.mountNewCache();
		Generic vehicleColor2 = color.addInstance("vehicleColor2", vehicle);
		assert vehicle.getMetaComposites(color).containsAll(Arrays.asList(vehicleColor, vehicleColor2)) : vehicle.getMetaComposites(color);
		mountNewCache.flush();
		assert vehicle.isAlive();
		assert color.isAlive();
		assert vehicleColor.isAlive();
		assert vehicleColor2.isAlive();
		assert vehicle.getMetaComposites(color).containsAll(Arrays.asList(vehicleColor, vehicleColor2)) : vehicle.getMetaComposites(color);
	}

	public void test005_TwoCompositesWithSameMetaInDifferentCaches_remove() {
		Engine engine = new Engine();
		Cache<Generic, Engine, Vertex, Root> currentCache = engine.getCurrentCache();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic vehiclePower = engine.addInstance("vehiclePower", vehicle);
		Cache<Generic, Engine, Vertex, Root> mountNewCache = currentCache.mountNewCache();
		assert vehiclePower.isAlive();
		assert !vehicle.getMetaComposites(engine.getMetaAttribute()).isEmpty() : vehicle.getMetaComposites(engine.getMetaAttribute()).stream().collect(Collectors.toList());
		vehiclePower.remove();
		assert vehicle.getMetaComposites(engine.getMetaAttribute()).isEmpty() : vehicle.getMetaComposites(engine.getMetaAttribute()).stream().collect(Collectors.toList());
		mountNewCache.flush();
		assert vehicle.isAlive();
		assert !vehiclePower.isAlive();
		assert vehicle.getMetaComposites(engine.getMetaAttribute()).isEmpty() : vehicle.getMetaComposites(engine.getMetaAttribute()).stream().collect(Collectors.toList());
	}
}
