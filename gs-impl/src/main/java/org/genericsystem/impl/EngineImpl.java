package org.genericsystem.impl;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import org.genericsystem.api.Generic;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.services.DependenciesService;

public class EngineImpl extends GenericImpl {

	Factory<Generic> factory;

	private static class GenericFactory implements Factory<Generic> {
		@Override
		public Generic build(Generic meta, Generic[] overrides, Serializable value, Generic[] components) {
			return new GenericImpl(() -> meta, () -> Stream.of(overrides), () -> value, () -> Stream.of(components));
		}

		@Override
		public Function<Vertex, Generic> getVertexWrapper(Vertex vertex) {
			return v -> v.isRoot() ? new EngineImpl(GenericFactory.this) : new GenericImpl(v, GenericFactory.this);
		}
	}

	public EngineImpl() {
		this(new GenericFactory());
	}

	public EngineImpl(Factory<Generic> factory) {
		super(factory.buildRoot(), factory);
		this.factory = factory;
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
	public <U extends DependenciesService> U getAlive() {
		return null;
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
