package org.genericsystem.mutability;

import java.io.Serializable;
import java.util.Collections;

public class Root extends Vertex implements DefaultRoot<Vertex> {

	private final Engine engine;

	Root(Engine engine, Serializable value) {
		init(null, Collections.emptyList(), value, Collections.emptyList());
		this.engine = engine;
	}

	@Override
	public Root getRoot() {
		return this;
	}

	@Override
	public Engine getEngine() {
		return engine;
	}

}
