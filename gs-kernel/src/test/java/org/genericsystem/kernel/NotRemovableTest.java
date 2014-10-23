package org.genericsystem.kernel;

import org.testng.annotations.Test;

@Test
public class NotRemovableTest extends AbstractTest {

	public void test001_multipleHolders_removeTwice() {
		// Vertex engine = new Root();
		// Vertex car = engine.addInstance("Car");
		// Vertex color = car.addAttribute("Color");
		// Vertex myBmw = car.addInstance("myBmw");
		// Vertex myBmwRed = myBmw.addHolder(color, "red");
		// Vertex myBmwBlue = myBmw.addHolder(color, "blue");
		//
		// myBmwRed.remove();
		// assert myBmw.getHolders(color).contains(myBmwBlue);
		// assert myBmw.getHolders(color).size() == 1;
		//
		// myBmwRed.remove();

	}

	public void test006_removeStructural_withdependency() {
		// Vertex engine = new Root();
		// Vertex car = engine.addInstance("Car");
		// Vertex color = car.addAttribute("Color");
		// Vertex myBmw = car.addInstance("myBmw");
		// Vertex myBmwRed = myBmw.addHolder(color, "red");
		//
		// assert car.getInstances().contains(myBmw);
		// assert car.getInstances().size() == 1;
		// car.remove();
		//
		// assert engine.getInstances().size() == 0;
	}
}
