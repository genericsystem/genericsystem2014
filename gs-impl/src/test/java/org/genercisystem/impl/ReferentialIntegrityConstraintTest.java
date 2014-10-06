package org.genercisystem.impl;

import org.genericsystem.api.exception.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.impl.Engine;
import org.genericsystem.impl.Generic;
import org.genericsystem.kernel.Statics;
import org.testng.annotations.Test;

@Test
public class ReferentialIntegrityConstraintTest extends AbstractTest {

	public void test001_enableReferentialIntegrity_remove() {
		Engine engine = new Engine();
		Generic vehicle = engine.addType("Vehicle");
		Generic color = engine.addType("Color");
		vehicle.addAttribute("VehicleColor", color);
		engine.getMetaAttribute().enableReferentialIntegrity(Statics.BASE_POSITION);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				vehicle.remove();
			}
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	}
}
