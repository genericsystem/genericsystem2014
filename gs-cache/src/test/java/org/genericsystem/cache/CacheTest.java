package org.genericsystem.cache;

import org.genericsystem.kernel.Vertex;
import org.testng.annotations.Test;

@Test
public class CacheTest extends AbstractTest {

	public void testTypeInheritings() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(new Generic[] { vehicle }, "Car");
		assert vehicle.getInheritings().stream().anyMatch(g -> g.equals(car));
	}

	public void testCacheInheritings() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Vertex car = engine.getAlive().addInstance(new Vertex[] { vehicle.getAlive() }, "Car");
		assert vehicle.getCurrentCache().getInheritingDependencies(vehicle) == null;
		assert vehicle.getInheritings().filter(g -> g.equiv(car)).size() == 1;
		assert vehicle.getInheritings().filter(g -> g.equals(car)).size() == 1;
		assert vehicle.getCurrentCache().getInheritingDependencies(vehicle).filter(g -> g.equiv(car)).size() == 1;
		assert vehicle.getCurrentCache().getInheritingDependencies(vehicle).filter(g -> g.equals(car)).size() == 1;
	}
}
