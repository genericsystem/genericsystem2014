package org.genericsystem.cache;

import java.util.Arrays;
import org.genericsystem.kernel.Vertex;
import org.testng.annotations.Test;

@Test
public class CacheTest extends AbstractTest {

	public void testTypeInheritings() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		Generic car = engine.addInstance(Arrays.asList(vehicle), "Car");
		assert vehicle.getInheritings().stream().anyMatch(g -> g.equals(car));
	}

	public void testCacheInheritings() {
		Engine engine = new Engine();
		Generic vehicle = engine.addInstance("Vehicle");
		// assert vehicle.getAlive() != null;
		Vertex car = engine.getAlive().addInstance(Arrays.asList(vehicle.getAlive()), "Car");
		assert vehicle.getInheritings().filter(car::equiv).size() == 1;
	}
}
