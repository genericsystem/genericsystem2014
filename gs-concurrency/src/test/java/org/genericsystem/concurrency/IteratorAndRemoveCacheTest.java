package org.genericsystem.concurrency;

import java.util.Iterator;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.OptimisticLockConstraintViolationException;
import org.genericsystem.cache.Generic;
import org.testng.annotations.Test;

@Test
public class IteratorAndRemoveCacheTest extends AbstractTest {

	public void test002_() {
		Engine engine = new Engine();
		Generic car = engine.addInstance("Car");
		Generic myCar = car.addInstance("myCar");
		Cache cache = engine.getCurrentCache();
		cache.flush();

		Cache cache2 = engine.newCache().start();
		myCar.remove();

		cache.start();
		myCar.remove();
		cache.flush();
		cache2.start();
		catchAndCheckCause(() -> cache2.flush(), OptimisticLockConstraintViolationException.class);
	}

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
		for (Generic g : car.getInstances()) {
			if (cpt % 2 == 0) {
				cache1.start();
				cache1.pickNewTs();
				g.remove();
				cache1.flush();
			} else {
				cache2.start();
				g.remove();
				cache2.flush();
			}
			cpt++;
		}
		assert car.getInstances().size() == 0;
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
		for (Generic g : car.getInstances()) {
			if (g.equals(myCar3))
				cache2.flush();
			if (cpt % 2 == 0) {
				cache1.start();
				cache1.pickNewTs();
				g.remove();
				cache1.flush();
			} else {
				cache2.start();
				g.remove();
			}
			cpt++;
		}
		cache2.flush();
		assert car.getInstances().size() == 0;
		cache1.start();
		assert car.getInstances().contains(myCar4);
		assert car.getInstances().size() == 1;
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
		cache1.pickNewTs();
		int cpt = 0;
		for (Generic g : car.getInstances()) {
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

		cache2.start();
		cache2.pickNewTs();

		assert car.getInstances().size() == 2 : car.getInstances().size();
		assert car.getInstances().contains(myCar4);
		assert car.getInstances().contains(myCar3);

		cache1.start();
		assert car.getInstances().contains(myCar2);
		assert car.getInstances().size() == 1;
		cache2.start().flush();
		cache1.start();
		cache1.pickNewTs();
		assert car.getInstances().contains(myCar4);
		assert car.getInstances().contains(myCar3);
		assert car.getInstances().size() == 2;
	}

	public void test005_IterateAndAdd() {
		Engine engine = new Engine();
		Cache cache1 = engine.getCurrentCache();

		Generic car = engine.addInstance("Car");
		Generic myCar1 = car.addInstance("myCar1");
		Generic myCar2 = car.addInstance("myCar2");
		Generic myCar3 = car.addInstance("myCar3");
		cache1.flush();
		Cache cache2 = engine.newCache().start();
		cache1.start();
		Generic myCar4 = car.addInstance("myCar4");

		Snapshot<Generic> myCars = car.getInstances();
		Iterator<Generic> iterator = myCars.iterator();
		int cpt = 0;
		while (iterator.hasNext()) {
			Generic g = iterator.next();
			if (g.equals(myCar2)) {
				cache2.start();
				Generic myCar4Bis = car.addInstance("myCar4");
				cache2.flush();
				cache1.start();
			}
			cpt++;
		}
		assert cache1.getTs() < cache2.getTs();
		assert cpt == 4 : cpt;
	}

	public void test006_IterateAndAdd_pickNewTs() {
		Engine engine = new Engine();
		Cache cache1 = engine.getCurrentCache();
		Generic car = engine.addInstance("Car");
		Generic myCar1 = car.addInstance("myCar1");
		Generic myCar2 = car.addInstance("myCar2");
		Generic myCar3 = car.addInstance("myCar3");
		cache1.flush();
		Cache cache2 = engine.newCache().start();
		cache1.start();
		Generic myCar4 = car.addInstance("myCar4");

		Snapshot<Generic> myCars = car.getInstances();
		Iterator<Generic> iterator = myCars.iterator();
		int cpt = 0;
		while (iterator.hasNext()) {
			Generic g = iterator.next();
			if (g.equals(myCar2)) {
				cache2.start();
				Generic myCar4Bis = car.addInstance("myCar4");
				cache2.flush();
				cache1.start();
				cache1.pickNewTs();
			}
			cpt++;
		}

		assert cache1.getTs() > cache2.getTs();
		assert cpt == 4 : cpt;
	}

	public void test007_IterateAndAdd_pickNewTs() {
		Engine engine = new Engine();
		Cache cache1 = engine.getCurrentCache();

		Generic car = engine.addInstance("Car");
		Generic myCar1 = car.addInstance("myCar1");
		Generic myCar2 = car.addInstance("myCar2");
		Generic myCar3 = car.addInstance("myCar3");
		cache1.flush();
		Cache cache2 = engine.newCache().start();
		cache1.start();
		Generic myCar4 = car.addInstance("myCar4");

		Snapshot<Generic> myCars = car.getInstances();
		Iterator<Generic> iterator = myCars.iterator();
		int cpt = 0;
		while (iterator.hasNext()) {
			Generic g = iterator.next();
			if (g.equals(myCar4)) {

				cache1.start().pickNewTs();
				cache2.start();
				Generic myCar4Bis = car.addInstance("myCar4");

				cache2.start().flush();
				cache1.start();
			}
			cpt++;
		}

		// cache1.flush();
		assert cache1.getTs() > cache2.getTs();
		assert cpt == 4 : cpt;
	}

	public void test008_IterateAndAdd_pickNewTs() {
		Engine engine = new Engine();
		Cache cache1 = engine.getCurrentCache();

		Generic car = engine.addInstance("Car");
		Generic myCar1 = car.addInstance("myCar1");
		Generic myCar2 = car.addInstance("myCar2");
		Generic myCar3 = car.addInstance("myCar3");
		Generic myCar4 = car.addInstance("myCar4");
		cache1.flush();
		Cache cache2 = engine.newCache().start();
		cache1.start();

		Snapshot<Generic> myCars = car.getInstances();
		Iterator<Generic> iterator = myCars.iterator();

		int cpt = 0;
		while (iterator.hasNext()) {
			Generic g = iterator.next();
			if (g.equals(myCar3)) {
				cache2.start();
				Generic myCar5 = car.addInstance("myCar5");
				cache2.flush();
				cache1.start();
				cache1.pickNewTs();
			}
			cpt++;
		}

		assert cpt == 4 : cpt;
	}

	public void test009_IterateAndAdd_pickNewTs() {
		Engine engine = new Engine();
		Cache cache1 = engine.getCurrentCache();

		Generic car = engine.addInstance("Car");
		Generic myCar1 = car.addInstance("myCar1");
		Generic myCar2 = car.addInstance("myCar2");
		Generic myCar3 = car.addInstance("myCar3");
		Generic myCar4 = car.addInstance("myCar4");

		cache1.flush();
		Snapshot<Generic> myCars = car.getInstances();
		Iterator<Generic> iterator = myCars.iterator();

		Generic myCar5 = car.addInstance("myCar5");
		Generic myCar6 = car.addInstance("myCar6");

		int cpt = 0;
		while (iterator.hasNext()) {
			iterator.next();
			cpt++;
		}

		assert cpt == 6 : cpt;
	}

	public void test010_IterateAndAdd_pickNewTs() {
		Engine engine = new Engine();
		Cache cache1 = engine.getCurrentCache();

		Generic car = engine.addInstance("Car");
		Generic myCar1 = car.addInstance("myCar1");
		Generic myCar2 = car.addInstance("myCar2");
		Generic myCar3 = car.addInstance("myCar3");
		Generic myCar4 = car.addInstance("myCar4");

		Snapshot<Generic> myCars = car.getInstances();
		Iterator<Generic> iterator = myCars.iterator();
		Generic myCar5 = car.addInstance("myCar5");
		Generic myCar6 = car.addInstance("myCar6");

		int cpt = 0;
		while (iterator.hasNext()) {
			if (cpt == 2)
				cache1.pickNewTs();
			iterator.next();
			cpt++;
		}

		assert cpt == 6 : cpt;
	}

}
