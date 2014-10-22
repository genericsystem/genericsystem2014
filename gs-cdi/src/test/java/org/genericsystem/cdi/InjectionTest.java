package org.genericsystem.cdi;

import org.genericsystem.cache.Generic;
import org.testng.annotations.Test;

@Test
public class InjectionTest extends AbstractTest {

	public void testInstanceIsConcreteWithValue() {
		Generic car = engine.addInstance("Car");
		// @SuppressWarnings("unused")
		Generic color = engine.addInstance("Color", car);
		Generic audi = car.addInstance("audi");
		// Generic red = carColor.addInstance("audiRed", audi);
	}

}
