package org.genericsystem.kernel;

import org.genericsystem.api.core.ApiStatics;
import org.testng.annotations.Test;

@Test
public class CascadeRemoveTest extends AbstractTest {

	public void testWhithCascadeRemove() {
		Root root = new Root();
		Generic car = root.addInstance("Car");
		Generic color = root.addInstance("Color");
		Generic carColor = car.addInstance("CarColor", color);
		carColor.enableCascadeRemove(ApiStatics.TARGET_POSITION);
		Generic myCar = car.addInstance("myCar");
		Generic link = myCar.addLink(carColor, "red", color);
		assert myCar.isAlive();
		assert link.isAlive();
		assert link.getTargetComponent().isAlive();
		link.remove();
		assert myCar.isAlive();
		assert !link.isAlive();
		assert !link.getTargetComponent().isAlive();
	}
}
