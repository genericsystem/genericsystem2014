package org.genericsystem.cache;

import java.util.Iterator;

import org.genericsystem.api.core.Snapshot;
import org.testng.annotations.Test;

@Test
public class IteratorAndRemoveCacheTest extends AbstractTest {

	public void test002_IterateAndRemove() {
		Engine engine = new Engine();
		Cache cache1 = engine.getCurrentCache();
		Cache cache2 = engine.newCache().start();
		Generic car = engine.addInstance("Car");
		Generic myCar1 = car.addInstance("myCar1");
		Generic myCar2 = car.addInstance("myCar2");
		Generic myCar3 = car.addInstance("myCar3");
		Generic myCar4 = car.addInstance("myCar4");

		cache2.flush();
		int cpt = 0;
		for (Generic g : car.getAllInstances()) {
			if (cpt % 2 == 0) {
				cache1.start();
				g.remove();
				cache1.flush();
			} else {
				cache2.start();
				g.remove();
				cache2.flush();
			}
			cpt++;
		}
		assert car.getAllInstances().size() == 0;
	}

	public void test003_IterateAndRemove() {
		Engine engine = new Engine();
		Cache cache1 = engine.getCurrentCache();
		Cache cache2 = engine.newCache().start();
		Generic car = engine.addInstance("Car");
		Generic myCar1 = car.addInstance("myCar1");
		Generic myCar2 = car.addInstance("myCar2");
		cache2.flush();
		Generic myCar3 = car.addInstance("myCar3");
		Generic myCar4 = car.addInstance("myCar4");

		int cpt = 0;
		for (Generic g : car.getAllInstances()) {
			if (g.equals(myCar3))
				cache2.flush();
			if (cpt % 2 == 0) {
				cache1.start();
				g.remove();
				cache1.flush();
			} else {
				cache2.start();
				g.remove();
			}
			cpt++;
		}
		cache2.flush();
		assert car.getAllInstances().size() == 0;
		cache1.start();
		assert car.getAllInstances().size() == 0;
	}

	public void test004_IterateAndRemove() {
		Engine engine = new Engine();
		Cache cache1 = engine.getCurrentCache();
		Cache cache2 = engine.newCache().start();
		Generic car = engine.addInstance("Car");
		Generic myCar1 = car.addInstance("myCar1");
		Generic myCar2 = car.addInstance("myCar2");
		cache2.flush();
		Generic myCar3 = car.addInstance("myCar3");
		Generic myCar4 = car.addInstance("myCar4");

		cache1.start();
		int cpt = 0;
		for (Generic g : car.getAllInstances()) {
			if (cpt % 2 == 0) {
				cache1.start();
				g.remove();
				cache1.flush();
			} else {
				cache2.start();
				g.remove();
			}
			cpt++;
		}
		assert car.getAllInstances().contains(myCar4);
		assert car.getAllInstances().contains(myCar3);
		assert car.getAllInstances().size() == 2;
		cache1.start();
		assert car.getAllInstances().contains(myCar2);
		assert car.getAllInstances().size() == 1;
		cache2.start().flush();
		cache1.start();
		assert car.getAllInstances().contains(myCar4);
		assert car.getAllInstances().contains(myCar3);
		assert car.getAllInstances().size() == 2;
	}

	
}
