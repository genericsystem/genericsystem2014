package org.genericsystem.mutability;

import org.testng.annotations.Test;

@Test
public class MutabilityTest extends AbstractTest {

	public void test001() {
		Engine engine = new Engine();
		System.out.println(engine.getValue());

	}

}
