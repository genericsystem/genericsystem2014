package org.genericsystem.concurrency.generic;

import java.io.Serializable;
import java.util.Collections;
import org.genericsystem.cache.Cache;
import org.genericsystem.concurrency.vertex.RootConcurrency;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;

public class EngineConcurrency extends GenericConcurrency implements EngineServiceConcurrency<GenericConcurrency> {

	private final RootConcurrency root;

	public EngineConcurrency() {
		this(Statics.ENGINE_VALUE, Statics.ENGINE_VALUE);
	}

	public EngineConcurrency(Serializable rootValue, Serializable engineValue) {
		root = buildRoot(rootValue);
		init(0, null, Collections.emptyList(), engineValue, Collections.emptyList());
	}

	@Override
	public RootConcurrency buildRoot() {
		return buildRoot(Statics.ENGINE_VALUE);
	}

	@Override
	public RootConcurrency buildRoot(Serializable value) {
		return new RootConcurrency(value);
	}

	@Override
	public Cache<GenericConcurrency> start(Cache<GenericConcurrency> cache) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stop(Cache<GenericConcurrency> cache) {
		// TODO Auto-generated method stub

	}

	@Override
	public EngineConcurrency getAlive() {
		return this;
	}

	@Override
	public Vertex getVertex() {
		return root;
	}

	@Override
	public EngineConcurrency getRoot() {
		return this;
	}

	@Override
	public void rollback() {
		root.rollback();
	}
}