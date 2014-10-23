package org.genericsystem.concurrency;

import org.testng.annotations.Test;

@Test
public class RemoveManyCachesWithTsTest extends AbstractTest {

	public void test001_cache2_noEdit() {
		Engine engine = new Engine();
		Cache cache = engine.getCurrentCache();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");
		Generic myBmwRed = myBmw.addHolder(color, "red");
		cache.flush();

		Cache cache2 = engine.newCache().start();
		cache2.flush();
		assert cache.getTs() < cache2.getTs();
		cache.start();
		myBmwRed.remove();
		cache.flush();
		assert cache.getTs() < cache2.getTs();
		cache2.start();

		assert cache.getTs() < cache2.getTs();
		assert myBmw.getHolders(color).size() == 0;
	}

	public void test002_cache2_edit() {
		Engine engine = new Engine();
		Cache cache = engine.getCurrentCache();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");
		Generic myBmwRed = myBmw.addHolder(color, "red");
		cache.flush();

		Cache cache2 = engine.newCache().start();
		Generic car2 = engine.addInstance("Car2");
		cache2.flush();
		assert cache.getTs() < cache2.getTs();
		cache.start();
		myBmwRed.remove();
		cache.flush();
		assert cache.getTs() > cache2.getTs();
		cache2.start();
		assert myBmw.getHolders(color).contains(myBmwRed);
		assert myBmw.getHolders(color).size() == 1;
		cache2.pickNewTs();
		assert cache.getTs() < cache2.getTs();
		assert myBmw.getHolders(color).size() == 0;
	}

	public void test003_() {
		Engine engine = new Engine();
		Cache cache = engine.getCurrentCache();
		Cache cache2 = engine.newCache().start();
		cache.start();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");
		Generic myBmwRed = myBmw.addHolder(color, "red");
		cache.flush();
		cache2.start();
		myBmwRed.remove();
		cache2.flush();
		assert cache.getTs() < cache2.getTs();
	}
}
