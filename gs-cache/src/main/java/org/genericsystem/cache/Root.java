package org.genericsystem.cache;

import java.io.Serializable;
import java.util.Collections;

import org.genericsystem.kernel.AbstractBuilder;
import org.genericsystem.kernel.Checker;
import org.genericsystem.kernel.Context;

public class Root extends Vertex implements DefaultRoot<Vertex> {

	private final Engine engine;

	private final Context<Vertex> context;

	Root(Engine engine, Serializable value) {
		init(null, Collections.emptyList(), value, Collections.emptyList());
		this.engine = engine;

		context = new Context<Vertex>(this);
		context.init(new Checker<>(context), new AbstractBuilder<Vertex>(this) {

			@Override
			protected Vertex newT() {
				return new Vertex();
			}

			@Override
			protected Vertex[] newTArray(int dim) {
				return new Vertex[dim];
			}
		});
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
