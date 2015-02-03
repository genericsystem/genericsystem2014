package org.genericsystem.kernel;

import org.genericsystem.api.exception.AliveConstraintViolationException;
import org.genericsystem.api.exception.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.genericsystem.kernel.annotations.constraints.PropertyConstraint;
import org.testng.annotations.Test;

@Test
public class NotRemovableTest extends AbstractTest {

	public void test001_aliveEx() {
		Vertex engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = car.addAttribute("Color");
		Vertex myBmw = car.addInstance("myBmw");
		Vertex myBmwRed = myBmw.addHolder(color, "red");

		myBmwRed.remove();
		catchAndCheckCause(() -> myBmwRed.remove(), AliveConstraintViolationException.class);

	}

	public void test002_referenceEx() {
		Vertex engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = car.addAttribute("Color");
		Vertex myBmw = car.addInstance("myBmw");

		catchAndCheckCause(() -> car.remove(), ReferentialIntegrityConstraintViolationException.class);
	}

	public void test003_referenceEx() {
		Vertex engine = new Root();
		Vertex car = engine.addInstance("Car");
		Vertex color = car.addAttribute("Color");
		Vertex myBmw = car.addInstance("myBmw");
		Vertex myBmwRed = myBmw.addHolder(color, "red");

		catchAndCheckCause(() -> color.remove(), ReferentialIntegrityConstraintViolationException.class);
	}

	public void test004_notRemoveAnnotedClass() {
		Root engine = new Root(Power.class);
		Vertex power = engine.find(Power.class);
		assert power.isPropertyConstraintEnabled();
		catchAndCheckCause(() -> power.disablePropertyConstraint(), IllegalAccessException.class);
	}

	@SystemGeneric
	public static class Vehicle {

	}

	@SystemGeneric
	@Components(Vehicle.class)
	@PropertyConstraint
	public static class Power {

	}
}
