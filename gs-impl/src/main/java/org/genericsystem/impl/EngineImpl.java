package org.genericsystem.impl;

import java.util.Objects;
import org.genericsystem.api.Generic;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.services.AncestorsService;

public class EngineImpl extends GenericImpl {

	private final Root root;

	public EngineImpl() {
		this(new Root());
	}

	EngineImpl(Root root) {
		super();
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
