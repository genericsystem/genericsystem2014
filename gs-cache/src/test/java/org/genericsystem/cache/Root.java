package org.genericsystem.cache;

import java.io.Serializable;
import java.util.Collections;

public class Root extends Vertex implements IRoot<Vertex, Root> {

	private final IEngine<?, ?, Vertex, Root> engine;

	Root(IEngine<?, ?, Vertex, Root> engine, Serializable value) {
		init(false, null, Collections.emptyList(), value, Collections.emptyList());
		this.engine = engine;
	}

	@Override
	public Root getRoot() {
		return IRoot.super.getRoot();
	}

	@Override
	public Root getAlive() {
		return (Root) IRoot.super.getAlive();
	}

	@Override
	public boolean isRoot() {
		return IRoot.super.isRoot();
	}

	@Override
	public IEngine<?, ?, Vertex, Root> getEngine() {
		return engine;
	}

}
