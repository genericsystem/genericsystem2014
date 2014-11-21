package org.genericsystem.cache;

import java.io.Serializable;
import java.util.Collections;

import org.genericsystem.kernel.Context;

public class Root extends Vertex implements DefaultRoot<Vertex> {

	private final Engine engine;

	private final Context<Vertex> context = new Context<Vertex>(this);

	Root(Engine engine, Serializable value) {
		init(null, Collections.emptyList(), value, Collections.emptyList());
		this.engine = engine;
	}

	@Override
	public Context<Vertex> getCurrentCache() {
		return context;
	}

	@Override
	public Engine getEngine() {
		return engine;
	}

	@Override
	protected Vertex getMeta(int dim) {
		return super.getMeta(dim);
	}

}
