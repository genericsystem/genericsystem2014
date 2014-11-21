package org.genericsystem.mutability;

import org.testng.annotations.Test;

@Test
public class MutabilityTest extends AbstractTest {

	public void test001() {
		Engine engine = new Engine();
		assert "Engine".equals(engine.getValue());
	}

	public void test002() {
		Engine engine = new Engine();
		Generic car = engine.addInstance("Car");
		assert engine.getInstances().contains(car);
		assert engine.getInstances().size() == 1;
	}

	public void test003() {
		Engine engine = new Engine();
		Generic car = engine.addInstance("Car");
		assert engine.getInstances().contains(car);
		assert engine.getInstances().size() == 1;
	}

}
