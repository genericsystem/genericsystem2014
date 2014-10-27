package org.genericsystem.kernel;

import java.util.Iterator;

import org.genericsystem.api.core.Snapshot;
import org.testng.annotations.Test;

@Test
public class IteratorAndRemoveTest extends AbstractTest {

	public void test002_IterateAndRemove() {
		Vertex engine = new Root();
		Vertex car = engine.addInstance("Car");
		car.addInstance("myCar1");
		car.addInstance("myCar2");
		Vertex myCar3 = car.addInstance("myCar3");
		car.addInstance("myCar4");

		Snapshot<Vertex> myCars = car.getInstances();

		Iterator<Vertex> iterator = myCars.iterator();
		int cpt = 0;
		myCar3.remove();
		while (iterator.hasNext()) {
			iterator.next();
			cpt++;
		}
		assert cpt == 3;
	}

	public void test004_IterateAndRemoveInLoop_beforeFindIt() {
		Vertex engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex myCar1 = car.addInstance("myCar1");
		car.addInstance("myCar2");
		car.addInstance("myCar3");
		car.addInstance("myCar4");

		for (Vertex v : car.getInstances())
			if (v.equals(myCar1))
				v.remove();
		assert car.getInstances().size() == 3;
	}

	public void test005_IterateAndRemoveInLoop_beforeFindIt() {
		Vertex engine = new Root();
		Vertex car = engine.addInstance("Car");
		car.addInstance("myCar1");
		car.addInstance("myCar2");
		Vertex myCar3 = car.addInstance("myCar3");
		car.addInstance("myCar4");

		Snapshot<Vertex> myCars = car.getInstances();

		Iterator<Vertex> iterator = myCars.iterator();
		int cpt = 0;
		while (iterator.hasNext()) {
			if (cpt == 0)
				myCar3.remove();
			if (iterator.next().equals(myCar3))
				assert false : "Remove Object";
			cpt++;
		}
		assert cpt == 3 : cpt;
		assert car.getInstances().size() == 3;
	}

	public void test006_IterateAndRemoveInLoop_attributes() {
		Vertex engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = car.addAttribute("Color");
		Vertex power = car.addAttribute("Power");
		Vertex doors = car.addAttribute("Doors");

		for (Vertex v : car.getComposites())
			v.remove();
		assert car.getComposites().size() == 0;
	}

	public void test007_IterateAndRemoveInLoop_attributes() {
		Vertex engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = car.addAttribute("Color");
		Vertex power = car.addAttribute("Power");
		Vertex doors = car.addAttribute("Doors");

		for (Vertex v : car.getComposites()) {
			color.remove();
			power.remove();
			doors.remove();
		}
		assert car.getComposites().size() == 0;
	}
}
