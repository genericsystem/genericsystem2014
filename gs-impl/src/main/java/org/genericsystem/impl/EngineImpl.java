package org.genericsystem.impl;

import java.io.Serializable;
import java.util.Objects;

import org.genericsystem.api.Generic;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.services.AncestorsService;

public class EngineImpl extends GenericImpl {

	Factory<Generic> factory;

	Root root = null;

	public EngineImpl() {
		this(new Factory<Generic>() {
			@Override
			public Generic build(Generic meta, Generic[] overrides, Serializable value, Generic[] components) {
				return new GenericImpl(meta, overrides, value, components);
			}
		});
	}

	public EngineImpl(Factory<Generic> factory) {
		super(null, new Generic[] {}, Statics.ENGINE_VALUE, new Generic[] {});
		this.factory = factory;
		this.root = factory.buildRoot();
	}

	public EngineImpl(Root root) {
		super(null, new Generic[] {}, Statics.ENGINE_VALUE, new Generic[] {});
		this.factory = null;
		this.root = root;
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
	public Factory<Generic> getFactory() {
		return factory;
	}

	@Override
	public Vertex getAlive() {
		return root;
	}

	public boolean equiv(AncestorsService<?> service) {
		if (this == service)
			return true;
		return Objects.equals(getValue(), service.getValue()) && equivComponents(service);
	}
}
