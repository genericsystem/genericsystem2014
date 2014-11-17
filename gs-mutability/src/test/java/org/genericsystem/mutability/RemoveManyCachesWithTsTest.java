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
		assert cache.getTs() < cache2.getTs();
		cache2.start();
		myBmwRed.remove();
		cache2.flush();
		assert cache.getTs() < cache2.getTs();
	}

	public void test004_() {
		Engine engine = new Engine();
		Cache cache = engine.getCurrentCache();
		Cache cache2 = engine.newCache().start();
		cache.start();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");
		Generic myBmwRed = myBmw.addHolder(color, "red");
		myBmwRed.remove();
		cache.flush();
		cache2.start();
		Generic myBmwRed2 = myBmw.addHolder(color, "red");
		assert cache.getTs() < cache2.getTs();
		cache2.flush();
		assert cache.getTs() < cache2.getTs();
		assert myBmwRed2 == myBmwRed;
		assert myBmwRed2.equals(myBmwRed);
		assert myBmw.getHolders(color).contains(myBmwRed2);
		assert myBmw.getHolders(color).size() == 1;
	}

	public void test005() {
		Engine engine = new Engine();
		Cache cache = engine.getCurrentCache();
		Cache cache2 = engine.newCache().start();
		cache.start();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");
		cache.flush();
		assert cache.getTs() < cache2.getTs();
		Generic myBmwRed = myBmw.addHolder(color, "red");
		cache2.start();
		Generic myBmwRed2 = myBmw.addHolder(color, "red");
		cache.start();
		myBmwRed.remove();
		cache.flush();
		assert cache.getTs() < cache2.getTs();
		cache2.start();
		cache2.flush();
		assert cache.getTs() < cache2.getTs();
		assert myBmwRed2 == myBmwRed;
		assert myBmwRed2.equals(myBmwRed);
		assert myBmw.getHolders(color).contains(myBmwRed2);
		assert myBmw.getHolders(color).size() == 1;
	}

	public void test006() {
		Engine engine = new Engine();
		Cache cache = engine.getCurrentCache();
		Cache cache2 = engine.newCache().start();
		cache.start();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");
		cache.flush();
		assert cache.getTs() < cache2.getTs();
		Generic myBmwRed = myBmw.addHolder(color, "red");
		cache2.start();
		Generic myBmwRed2 = myBmw.addHolder(color, "red");
		cache.start();
		cache.flush();
		assert cache.getTs() > cache2.getTs();
		myBmwRed.remove();
		cache2.start();
		cache2.clear();
		cache2.flush();
		assert cache.getTs() > cache2.getTs();
		assert myBmwRed2 == myBmwRed;
		assert myBmwRed2.equals(myBmwRed);
		assert myBmw.getHolders(color).size() == 0;
	}
}
