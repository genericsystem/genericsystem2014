package org.genericsystem.cache;

import java.io.Serializable;
import java.util.Collections;

public class Root extends Vertex implements DefaultRoot<Vertex, Root> {

	private final DefaultEngine<?, ?, Vertex, Root> engine;

	Root(DefaultEngine<?, ?, Vertex, Root> engine, Serializable value) {
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
	public DefaultEngine<?, ?, Vertex, Root> getEngine() {
		return engine;
	}

}
