package org.genericsystem.impl;

import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Stream;

import org.genericsystem.api.Generic;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;

public class EngineImpl extends GenericImpl {

	Factory<Generic> factory;

	Root root = null;

	private static class GenericFactory implements Factory<Generic> {
		@Override
		public Generic build(Generic meta, Generic[] overrides, Serializable value, Generic[] components) {
			return new GenericImpl(meta, Stream.of(overrides), value, Stream.of(components));
		}
	}

	public EngineImpl() {
		this(new GenericFactory());
	}

	public EngineImpl(Factory<Generic> factory) {
		super(null, Stream.of(new Generic[] {}), Statics.ENGINE_VALUE, Stream.of(new Generic[] {}));
		this.factory = factory;
		this.root = factory.buildRoot();
	}

	@Override
	public boolean isRoot() {
		return true;
	}

	@Override
	public EngineImpl getRoot() {
		return this;
	}

	@Override
	public Generic getMeta() {
		return this;
	}

	@Override
	public Factory<Generic> getFactory() {
		return factory;
	}

	@Override
	public Vertex getAlive() {
		assert root != null;
		return root;
	}

	@Override
	public int getLevel() {
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof EngineImpl))
			return false;
		EngineImpl service = (EngineImpl) obj;
		return Objects.equals(this.getValue(), service.getValue());
	}
}
