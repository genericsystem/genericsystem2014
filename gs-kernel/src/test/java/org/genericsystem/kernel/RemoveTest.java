package org.genericsystem.kernel;

import org.testng.annotations.Test;

@Test
public class RemoveTest extends AbstractTest {

	public void test001_simpleHolder() {
		Vertex engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = car.addAttribute("Color");
		Vertex myBmw = car.addInstance("myBmw");
		Vertex myBmwRed = myBmw.addHolder(color, "red");

		assert myBmw.getHolders(color).contains(myBmwRed) : myBmw.getHolders(color).info();
		assert myBmw.getHolders(color).size() == 1;

		myBmwRed.remove();

		assert myBmw.getHolders(color).size() == 0;
	}

	public void test002_multipleHolders() {
		Vertex engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = car.addAttribute("Color");
		Vertex myBmw = car.addInstance("myBmw");
		Vertex myBmwRed = myBmw.addHolder(color, "red");
		Vertex myBmwBlue = myBmw.addHolder(color, "blue");

		myBmwRed.remove();
		assert myBmw.getHolders(color).contains(myBmwBlue);
		assert myBmw.getHolders(color).size() == 1;

		Vertex myBmwGreen = myBmw.addHolder(color, "green");

		myBmwBlue.remove();
		assert myBmw.getHolders(color).contains(myBmwGreen);
		assert myBmw.getHolders(color).size() == 1;
	}

	public void test003_removeAndAdd() {
		Vertex engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = car.addAttribute("Color");
		Vertex myBmw = car.addInstance("myBmw");
		Vertex myBmwRed = myBmw.addHolder(color, "red");
		Vertex myBmwBlue = myBmw.addHolder(color, "blue");

		myBmwRed.remove();
		myBmwRed = myBmw.addHolder(color, "red");

		assert myBmw.getHolders(color).contains(myBmwRed);
		assert myBmw.getHolders(color).contains(myBmwBlue);
		assert myBmw.getHolders(color).size() == 2;
	}

	public void test004_removeAndAddAndRemove() {
		Vertex engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = car.addAttribute("Color");
		Vertex myBmw = car.addInstance("myBmw");
		Vertex myBmwRed = myBmw.addHolder(color, "red");
		Vertex myBmwBlue = myBmw.addHolder(color, "blue");

		myBmwRed.remove();
		myBmwRed = myBmw.addHolder(color, "red");
		myBmwRed.remove();

		assert myBmw.getHolders(color).contains(myBmwBlue);
		assert myBmw.getHolders(color).size() == 1;
	}

	public void test005_removeConcret_withHolder() {
		Vertex engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = car.addAttribute("Color");
		Vertex myBmw = car.addInstance("myBmw");
		Vertex myBmwRed = myBmw.addHolder(color, "red");

		assert color.getInstances().contains(myBmwRed);
		assert color.getInstances().size() == 1;
		myBmw.remove();

		assert car.getInstances().size() == 0;
		assert color.getInstances().size() == 0;
	}

	public void test006_removeStructural_withHolder() {
		Vertex engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = car.addAttribute("Color");
		Vertex myBmw = car.addInstance("myBmw");
		Vertex myBmwRed = myBmw.addHolder(color, "red");
		myBmw.remove();
		car.remove();

		assert !engine.getInstances().contains(car);
		assert !engine.getInstances().contains(color);
	}

	public void test007_removeConcretAndAttribut() {
		Vertex engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = car.addAttribute("Color");
		Vertex myBmw = car.addInstance("myBmw");
		Vertex myBmwRed = myBmw.addHolder(color, "red");
		myBmw.remove();
		color.remove();

		assert engine.getInstances().contains(car);
		assert !car.getInstances().contains(myBmw);
		assert !engine.getInstances().contains(color);

	}

	public void test008_removeInstanceAndAttribute() {
		Vertex engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = car.addAttribute("Color");
		Vertex myBmw = car.addInstance("myBmw");
		Vertex myBmwRed = myBmw.addHolder(color, "red");
		myBmwRed.remove();
		color.remove();

		assert engine.getInstances().contains(car);
		assert car.getInstances().contains(myBmw);
		assert !engine.getInstances().contains(color);

	}

	public void test009_removeConcret() {
		Vertex engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = car.addAttribute("Color");
		Vertex myBmw = car.addInstance("myBmw");
		Vertex myBmwRed = myBmw.addHolder(color, "red");
		myBmwRed.remove();

		assert color.getInstances().size() == 0;

	}
}
