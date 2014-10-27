package org.genericsystem.cache;

import org.testng.annotations.Test;

@Test
public class RemoveManyCachesTest extends AbstractTest {

	public void test001_simpleHolder() {
		Engine engine = new Engine();
		Cache cache = engine.getCurrentCache();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		cache.flush();
		Cache cache2 = engine.newCache().start();
		Generic myBmw = car.addInstance("myBmw");
		Generic myBmwRed = myBmw.addHolder(color, "red");

		assert myBmw.getHolders(color).contains(myBmwRed);
		assert myBmw.getHolders(color).size() == 1;

		myBmwRed.remove();

		assert myBmw.getHolders(color).size() == 0;
	}

	public void test002_simpleHolder() {
		Engine engine = new Engine();
		Cache cache = engine.getCurrentCache();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		cache.flush();
		Cache cache2 = engine.newCache().start();
		Generic myBmw2 = car.addInstance("myBmw");
		Generic myBmwRed2 = myBmw2.addHolder(color, "red");
		cache.start();
		Generic myBmw = car.addInstance("myBmw");
		Generic myBmwRed = myBmw.addHolder(color, "red");
		cache.flush();
		assert myBmw.getHolders(color).contains(myBmwRed);
		assert myBmw.getHolders(color).size() == 1;

		myBmwRed.remove();

		assert myBmw.getHolders(color).size() == 0;
	}

	public void test003_simpleHolder() {
		Engine engine = new Engine();
		Cache cache = engine.getCurrentCache();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		cache.flush();
		Cache cache2 = engine.newCache().start();
		Generic myBmw2 = car.addInstance("myBmw2");
		Generic myBmwRed2 = myBmw2.addHolder(color, "red2");
		cache.start();
		Generic myBmw = car.addInstance("myBmw");
		Generic myBmwRed = myBmw.addHolder(color, "red");
		cache.flush();
		cache2.start();
		assert myBmw2.getHolders(color).contains(myBmwRed2);
		assert myBmw2.getHolders(color).size() == 1;
		myBmwRed2.remove();
		assert myBmw2.getHolders(color).size() == 0;

		assert myBmw.getHolders(color).contains(myBmwRed);
		assert myBmw.getHolders(color).size() == 1;
		myBmwRed.remove();
		assert myBmw.getHolders(color).size() == 0;
	}

	public void test004_simpleHolder() {
		Engine engine = new Engine();
		Cache cache = engine.getCurrentCache();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		cache.flush();
		Cache cache2 = engine.newCache().start();
		Generic myBmw2 = car.addInstance("myBmw2");
		Generic myBmwRed2 = myBmw2.addHolder(color, "red2");
		cache.start();
		Generic myBmw = car.addInstance("myBmw");
		Generic myBmwRed = myBmw.addHolder(color, "red");
		cache.flush();
		cache2.start();
		assert myBmw2.getHolders(color).contains(myBmwRed2);
		assert myBmw2.getHolders(color).size() == 1;
		myBmwRed2.remove();
		assert myBmw2.getHolders(color).size() == 0;

		assert myBmw.getHolders(color).contains(myBmwRed);
		assert myBmw.getHolders(color).size() == 1;
		myBmwRed.remove();
		assert myBmw.getHolders(color).size() == 0;
		cache2.clear();
		cache.start();
		assert myBmw.getHolders(color).contains(myBmwRed);
		assert myBmw.getHolders(color).size() == 1;
		myBmwRed.remove();
		assert myBmw.getHolders(color).size() == 0;
		assert myBmw2.getHolders(color).size() == 0;

	}

	public void test003_removeAndAdd() {
		Engine engine = new Engine();

		Cache cache = engine.getCurrentCache();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");
		Generic myBmwRed = myBmw.addHolder(color, "red");
		Generic myBmwBlue = myBmw.addHolder(color, "blue");

		Cache cache2 = engine.newCache().start();
		Generic car2 = engine.addInstance("Car2");
		Generic color2 = car2.addAttribute("Color2");
		Generic myBmw2 = car2.addInstance("myBmw2");
		Generic myBmwRed2 = myBmw2.addHolder(color2, "red2");

		cache2.flush();
		cache.start();
		myBmwRed.remove();
		cache.flush();
		cache2.start();
		myBmwRed = myBmw.addHolder(color, "red");

		assert myBmw.getHolders(color).contains(myBmwRed);
		assert myBmw.getHolders(color).contains(myBmwBlue);
		assert myBmw.getHolders(color).size() == 2;

		assert myBmw2.getHolders(color2).contains(myBmwRed2);
		assert myBmw2.getHolders(color2).size() == 1;
		cache.start();
		cache.clear();
		assert myBmw.getHolders(color).contains(myBmwBlue);
		assert myBmw.getHolders(color).size() == 1;
		assert myBmw2.getHolders(color2).contains(myBmwRed2);
		assert myBmw2.getHolders(color2).size() == 1;

	}

	public void test004_removeAndAdd() {
		Engine engine = new Engine();
		Cache cache = engine.getCurrentCache();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");
		cache.flush();
		Generic myBmwRed = myBmw.addHolder(color, "red");
		Generic myBmwBlue = myBmw.addHolder(color, "blue");
		Cache cache2 = engine.newCache().start();
		Generic car2 = engine.addInstance("Car2");
		Generic color2 = car2.addAttribute("Color2");
		Generic myBmw2 = car2.addInstance("myBmw2");
		Generic myBmwRed2 = myBmw.addHolder(color, "red2");
		cache2.flush();
		cache.start();
		cache.flush();
		cache2.start();
		assert myBmwRed.isAlive();
		assert myBmwBlue.isAlive();
		assert myBmwRed2.isAlive();

		assert myBmw.getHolders(color).contains(myBmwRed2);
		assert myBmw.getHolders(color).contains(myBmwRed);
		assert myBmw.getHolders(color).contains(myBmwBlue);
		assert myBmw.getHolders(color).size() == 3;
		cache.start();
		assert myBmwRed.isAlive();
		assert myBmwBlue.isAlive();
		assert myBmwRed2.isAlive();

		assert myBmw.getHolders(color).contains(myBmwRed2);
		assert myBmw.getHolders(color).contains(myBmwRed);
		assert myBmw.getHolders(color).contains(myBmwBlue);
		assert myBmw.getHolders(color).size() == 3;
		cache2.start();
		myBmwRed.remove();
		myBmwRed = myBmw.addHolder(color, "red");

		assert myBmw.getHolders(color).contains(myBmwRed);
		assert myBmw.getHolders(color).contains(myBmwBlue);
		assert myBmw.getHolders(color).contains(myBmwRed2);
		assert myBmw.getHolders(color).size() == 3;

		cache.start();
		assert myBmwRed.isAlive();
		assert myBmwBlue.isAlive();
		assert myBmwRed2.isAlive();

		assert myBmw.getHolders(color).contains(myBmwRed2);
		assert myBmw.getHolders(color).contains(myBmwRed);
		assert myBmw.getHolders(color).contains(myBmwBlue);
		assert myBmw.getHolders(color).size() == 3;
		cache2.start();
		cache2.flush();
		cache.start();
		assert myBmwRed.isAlive();
		assert myBmwBlue.isAlive();
		assert myBmwRed2.isAlive();

		assert myBmw.getHolders(color).contains(myBmwRed2);
		assert myBmw.getHolders(color).contains(myBmwRed);
		assert myBmw.getHolders(color).contains(myBmwBlue);
		assert myBmw.getHolders(color).size() == 3;

	}

	public void test004_removeAndAddAndRemove() {
		Engine engine = new Engine();
		Cache cache = engine.getCurrentCache();
		Cache cache2 = engine.newCache().start();
		Cache cache3 = engine.newCache().start();
		cache.start();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");
		Generic myBmwRed = myBmw.addHolder(color, "red");
		cache.flush();
		myBmwRed.remove();
		cache2.start();
		myBmwRed.remove();
		cache3.start();
		Generic myBmwBlue = myBmw.addHolder(color, "blue");
		cache3.flush();
		cache2.start();
		cache2.flush();
		cache.start();
		assert myBmw.getHolders(color).contains(myBmwBlue);
		assert myBmw.getHolders(color).size() == 1;
		cache.clear();
		assert myBmw.getHolders(color).contains(myBmwBlue);
		assert myBmw.getHolders(color).size() == 1;
	}

	public void test005_removeAndAddAndRemove() {
		Engine engine = new Engine();
		Cache cache = engine.getCurrentCache();
		Cache cache2 = engine.newCache().start();
		Cache cache3 = engine.newCache().start();
		cache.start();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");
		Generic myBmwRed = myBmw.addHolder(color, "red");
		cache.flush();
		cache2.start();
		myBmwRed.remove();
		cache3.start();
		Generic myBmwBlue = myBmw.addHolder(color, "blue");
		cache3.flush();
		cache2.start();
		cache2.flush();
		cache.start();
		assert myBmw.getHolders(color).contains(myBmwBlue);
		assert myBmw.getHolders(color).size() == 1;
		myBmwBlue.remove();
		cache3.start();
		assert myBmw.getHolders(color).contains(myBmwBlue);
		assert myBmw.getHolders(color).size() == 1;
		myBmwBlue.remove();
		cache2.start();
		assert myBmw.getHolders(color).contains(myBmwBlue);
		assert myBmw.getHolders(color).size() == 1;
		myBmwBlue.remove();
		cache.start();
		cache.flush();
		cache2.start();
		cache2.clear();
		assert myBmw.getHolders(color).size() == 0;
		cache2.flush();
		cache3.start();
		assert myBmw.getHolders(color).size() == 0;
		cache3.clear();
		Generic myBmwBlue2 = myBmw.addHolder(color, "blue2");
		cache3.flush();
		assert myBmw.getHolders(color).contains(myBmwBlue2);
		assert myBmw.getHolders(color).size() == 1;
		cache2.start();
		assert myBmw.getHolders(color).contains(myBmwBlue2);
		assert myBmw.getHolders(color).size() == 1;
		cache.start();
		assert myBmw.getHolders(color).contains(myBmwBlue2);
		assert myBmw.getHolders(color).size() == 1;
	}

}
