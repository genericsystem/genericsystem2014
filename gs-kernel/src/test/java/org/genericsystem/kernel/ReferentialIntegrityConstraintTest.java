package org.genericsystem.kernel;

import org.genericsystem.api.exception.ReferentialIntegrityConstraintViolationException;
import org.testng.annotations.Test;

@Test
public class ReferentialIntegrityConstraintTest extends AbstractTest {

	public void test001_enableReferentialIntegrity_remove() {
		Root engine = new Root();
		Vertex vehicle = engine.addType("Vehicle");
		Vertex color = engine.addType("Color");
		vehicle.addAttribute("VehicleColor", color);
		engine.getMetaAttribute().enableReferentialIntegrity(Statics.BASE_POSITION);
		catchAndCheckCause(() -> vehicle.remove(), ReferentialIntegrityConstraintViolationException.class);
	}
}
