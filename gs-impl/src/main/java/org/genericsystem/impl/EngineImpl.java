package org.genericsystem.impl;

import java.io.Serializable;
import java.util.Objects;

import org.genericsystem.api.Generic;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.services.AncestorsService;

public class EngineImpl extends GenericImpl {

	GenericFactory<Generic> factory;

	Root root = null;

	public EngineImpl() {
		this(new GenericFactory<Generic>() {
			@Override
			public Generic buildGeneric(Generic meta, Generic[] overrides, Serializable value, Generic[] components) {
				return new GenericImpl(meta, overrides, value, components);
			}
		});
	}

	public EngineImpl(GenericFactory<Generic> factory) {
		super(null, new Generic[] {}, Statics.ENGINE_VALUE, new Generic[] {});
		this.factory = factory;
		this.root = factory.buildRoot();
	}

	@Override
	public boolean isRoot() {
		return true;
	}

	@Override
	public boolean isAlive() {
		return equiv(getAlive());
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
	public int getLevel() {
		return 0;
	}

	@Override
	public GenericFactory<Generic> getFactory() {
		return factory;
	}

	@Override
	public Vertex getAlive() {
		return root;
	}

	@Override
	public boolean equiv(AncestorsService<?> service) {
		if (this == service)
			return true;
		return Objects.equals(getValue(), service.getValue()) && equivComponents(service);
	}
}
