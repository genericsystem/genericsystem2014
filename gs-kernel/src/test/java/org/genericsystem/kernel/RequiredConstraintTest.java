package org.genericsystem.kernel;

import org.testng.annotations.Test;

@Test
public class RequiredConstraintTest extends AbstractTest {

	public void test001_enableRequired() {
		Root engine = new Root();
		Vertex power = engine.addInstance("Power");
		power.enableRequiredConstraint(Statics.NO_POSITION);

		assert power.isRequiredConstraintEnabled(Statics.NO_POSITION);
		power.disableRequiredConstraint(Statics.NO_POSITION);
		assert !power.isRequiredConstraintEnabled(Statics.NO_POSITION);
	}

}
