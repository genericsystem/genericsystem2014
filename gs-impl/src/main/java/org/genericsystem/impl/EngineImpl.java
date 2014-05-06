package org.genericsystem.impl;

import java.io.Serializable;
import java.util.Objects;

import org.genericsystem.api.Generic;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.services.DependenciesService;

public class EngineImpl extends GenericImpl {

	Factory<Generic> factory;

	public EngineImpl() {
		this(new Factory<Generic>() {
			@Override
			public Generic build(Vertex meta, Vertex[] overrides, Serializable value, Vertex[] components) {
				return new GenericImpl(new Vertex(meta, overrides, value, components));
			}
		});
	}

	public EngineImpl(Factory<Generic> factory) {
		this(factory.buildRoot());
		this.factory = factory;
	}

	public EngineImpl(Vertex vertex) {
		super(vertex);
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
