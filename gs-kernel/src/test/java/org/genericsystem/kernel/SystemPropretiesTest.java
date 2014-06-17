package org.genericsystem.kernel;

import org.testng.annotations.Test;

@Test
public class SystemPropretiesTest extends AbstractTest {

	public void test001_enablePropertyConstraint() {
		Root root = new Root();
		Vertex vehicle = root.addInstance("Vehicle");
		Vertex power = root.addInstance("Power", vehicle);
		power.enablePropertyConstraint();
		power.enablePropertyConstraint();
		assert power.isPropertyConstraintEnabled();
		power.disablePropertyConstraint();
		power.disablePropertyConstraint();
		assert !power.isPropertyConstraintEnabled();
	}

}
