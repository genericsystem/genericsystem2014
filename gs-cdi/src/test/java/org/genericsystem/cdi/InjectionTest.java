package org.genericsystem.cdi;

import org.genericsystem.concurrency.Generic;
import org.testng.annotations.Test;

@Test
public class InjectionTest extends AbstractTest {

	// @Inject
	// Cache cache;

	public void test() {
		assert engine != null;
		// assert cache != null;
	}

	public void test2() {
		engine.newCache().start();
		Generic car = engine.addInstance("Car");
		Generic color = engine.addInstance("Color");
		Generic carColor = car.setAttribute("outsideColor", color);
		Generic audi = car.addInstance("audi");
		Generic red = color.addInstance("red");
		Generic audiRed = audi.setHolder(carColor, "audiRed", red);
		assert audi.getHolders(carColor).contains(audiRed) : audi.getHolders(carColor);
	}

}
