package org.genericsystem.impl;

import java.io.Serializable;
import java.util.Collections;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;

public class Engine extends Generic implements EngineService<Generic> {

	private final Root root;

	public Engine() {
		this(Statics.ENGINE_VALUE, Statics.ENGINE_VALUE);
	}

	public Engine(Serializable rootValue, Serializable engineValue) {
		root = buildRoot(rootValue);
		init(0, null, Collections.emptyList(), engineValue, Collections.emptyList());
	}

	@Override
	public Root buildRoot(Serializable value) {
		return new Root(value);
	}

	@Override
	public Vertex getVertex() {
		return root;
	}
}
