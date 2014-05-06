package org.genericsystem.impl;

import java.io.Serializable;
import java.util.Objects;
import org.genericsystem.api.Generic;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.services.DependenciesService;

public class EngineImpl extends GenericImpl {

	Factory<Generic> factory;

	public EngineImpl() {
		this(new Factory<Generic>() {
			@Override
			public Generic buildVertex(Vertex meta, Vertex[] overrides, Serializable value, Vertex[] components) {
				return null;
			}
		});
	}

	private static Root buildRoot(Factory<Generic> factory) {
		return null;
	}

	public EngineImpl(Factory<Generic> factory) {
		super(buildRoot(factory));
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
	public <U extends DependenciesService> U getPlugged() {
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
