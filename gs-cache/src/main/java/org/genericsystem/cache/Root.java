package org.genericsystem.cache;

import java.io.Serializable;
import java.util.Collections;

import org.genericsystem.cache.AbstractBuilder.VertexBuilder;
import org.genericsystem.kernel.Context;

public class Root extends Vertex implements DefaultRoot<Vertex> {

	private final Engine engine;

	private final Context<Vertex> context;

	Root(Engine engine, Serializable value) {
		init(null, Collections.emptyList(), value, Collections.emptyList());
		this.engine = engine;
		context = new Context<Vertex>(this);
		context.init(new VertexBuilder(context));
	}

	@Override
	public Context<Vertex> getCurrentCache() {
		return context;
	}

	@Override
	public Engine getEngine() {
		return engine;
	}

}
