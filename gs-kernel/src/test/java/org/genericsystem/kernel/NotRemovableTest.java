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
		Generic engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");
		Generic myBmwRed = myBmw.addHolder(color, "red");

		myBmwRed.remove();
		catchAndCheckCause(() -> myBmwRed.remove(), AliveConstraintViolationException.class);

	}

	public void test002_referenceEx() {
		Generic engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");

		catchAndCheckCause(() -> car.remove(), ReferentialIntegrityConstraintViolationException.class);
	}

	public void test003_referenceEx() {
		Generic engine = new Root();
		Generic car = engine.addInstance("Car");
		Generic color = car.addAttribute("Color");
		Generic myBmw = car.addInstance("myBmw");
		Generic myBmwRed = myBmw.addHolder(color, "red");

		catchAndCheckCause(() -> color.remove(), ReferentialIntegrityConstraintViolationException.class);
	}

	public void test004_notRemoveAnnotedClass() {
		Root engine = new Root(Power.class);
		Generic power = engine.find(Power.class);
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
