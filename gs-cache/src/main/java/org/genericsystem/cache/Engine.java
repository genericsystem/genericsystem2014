package org.genericsystem.cache;

import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;

public class Engine extends Generic implements EngineService<Generic> {

	private final Root root = buildVerticesRoot();
	private Cache currentCache = new Cache();

	public Engine() {
		init(null, new Generic[] {}, Statics.ENGINE_VALUE, new Generic[] {});
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
