package org.genericsystem.impl;

import java.util.Objects;

import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.services.DependenciesService;
import org.genericsystem.kernel.services.FactoryService.Factory;

public class EngineImpl extends GenericImpl {

	public EngineImpl() {
		this(new Factory() {
		});
	}

	public EngineImpl(Factory factory) {
		this(new Root(factory));
	}

	public EngineImpl(Vertex vertex) {
		super(vertex);
	}

	@Override
	public boolean isRoot() {
		return true;
	}

	@SuppressWarnings("unchecked")
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
