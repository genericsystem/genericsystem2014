package org.genercisystem.impl;

import java.util.Collections;

import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Vertex;
import org.testng.annotations.Test;

@Test
public class GenericsCacheTest extends AbstractTest {

	public void test001_getGenericFromCache() {
		Engine engine = new Engine();
		Root root = (Root) engine.getVertex();
		Generic vehicle = engine.addInstance("Vehicle");
		Vertex vehicleVertex = root.buildInstance(false, Collections.emptyList(), "Vehicle", Collections.emptyList());
		assert engine.getGenericFromCache(vehicleVertex) == vehicle;
	}
}
