package org.genericsystem.cache;

import org.genericsystem.cache.Generic.GenericImpl;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;

public class Engine extends GenericImpl implements EngineService<GenericImpl> {

	private final Root root = buildVerticesRoot();
	private Cache currentCache = new Cache();

	public Engine() {
		init(null, new GenericImpl[] {}, Statics.ENGINE_VALUE, new GenericImpl[] {});
	}

	@Override
	public Cache getCurrentCache() {
		return currentCache;
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
