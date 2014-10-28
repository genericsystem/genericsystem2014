package org.genericsystem.cache;

import java.io.Serializable;
import java.util.Collections;

public class Root extends Vertex implements DefaultRoot<Vertex> {

	private final Engine engine;

	Root(Engine engine, Serializable value) {
		init(false, null, Collections.emptyList(), value, Collections.emptyList());
		this.engine = engine;
	}

	@Override
	public Root getRoot() {
		return this;
	}

	@Override
	public Root getAlive() {
		return this;
	}

	@Override
	public boolean isRoot() {
		return true;
	}

	@Override
	public Engine getEngine() {
		return engine;
	}

}
