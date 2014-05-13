package org.genericsystem.cache;

import org.genericsystem.cache.Generic.GenericImpl;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;

public class Engine extends GenericImpl implements EngineService<GenericImpl> {

	private Cache currentCache = new Cache();

	@Override
	public Cache getCurrentCache() {
		return currentCache;
	}

	private final Root root;

	public Engine() {
		super(null, new GenericImpl[] {}, Statics.ENGINE_VALUE, new GenericImpl[] {});
		root = buildVerticesRoot();
	}

	@Override
	public Vertex getAlive() {
		return root;
	}

	// Why is this necessary ??? what does maven do here ?
	@Override
	public Engine getRoot() {
		return (Engine) EngineService.super.getRoot();
	}

}
