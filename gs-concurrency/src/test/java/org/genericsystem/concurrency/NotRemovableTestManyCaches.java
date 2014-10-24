package org.genericsystem.concurrency;

import org.genericsystem.api.exception.AliveConstraintViolationException;
import org.genericsystem.api.exception.ReferentialIntegrityConstraintViolationException;
import org.testng.annotations.Test;

@Test
public class NotRemovableTestManyCaches extends AbstractTest {

	public void test001_aliveEx() {
		Engine engine = new Engine();
		Cache cache = engine.getCurrentCache();
		Cache cache2 = engine.newCache().start();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");
		Generic myBmwRed = myBmw.addHolder(color, "red");
		cache.start();
		catchAndCheckCause(() -> myBmwRed.remove(), AliveConstraintViolationException.class);
	}

	public void test003_aliveEx() {
		Engine engine = new Engine();
		Cache cache = engine.getCurrentCache();
		Cache cache2 = engine.newCache().start();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");
		Generic myBmwRed = myBmw.addHolder(color, "red");
		cache.start();
		Generic car2 = engine.addInstance("Car2");
		Generic myBmw2 = car2.addInstance("myBmw2");
		catchAndCheckCause(() -> myBmw2.addHolder(color, "red2"), AliveConstraintViolationException.class);
	}

	public void test001_referenceEx() {
		Engine engine = new Engine();
		Cache cache = engine.getCurrentCache();
		Generic car = engine.addInstance("Car");
		cache.flush();
		Cache cache2 = engine.newCache().start();
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");
		catchAndCheckCause(() -> car.remove(), ReferentialIntegrityConstraintViolationException.class);
	}

	public void test002_referenceEx() {
		Engine engine = new Engine();
		Cache cache = engine.getCurrentCache();
		Cache cache2 = engine.newCache().start();
		Cache cache3 = engine.newCache().start();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");
		cache3.flush();
		cache2.start();
		cache2.pickNewTs();
		Generic myBmwRed = myBmw.addHolder(color, "red");
		cache2.flush();
		cache.start();
		cache.pickNewTs();
		catchAndCheckCause(() -> car.remove(), ReferentialIntegrityConstraintViolationException.class);
	}
}
