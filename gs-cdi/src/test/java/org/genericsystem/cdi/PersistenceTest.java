package org.genericsystem.cdi;

import org.genericsystem.concurrency.Generic;
import org.genericsystem.kernel.annotations.SystemGeneric;
import org.testng.annotations.Test;

@Test
public class PersistenceTest extends AbstractTest {

	public void testCount() {
		Generic count = engine.find(Count.class);
		if (count.getInstances().isEmpty()) {
			count.addInstance(0);
			engine.getCurrentCache().flush();
		}
		Generic previousValue = count.getInstances().first();
		Generic actualValue = previousValue.updateValue((int) previousValue.getValue() + 1);
		log.info("previousValue " + previousValue.info() + " actualValue " + actualValue.info());
		engine.getCurrentCache().flush();
	}

	@SystemGeneric
	public static class Count extends Generic {
	}

}
