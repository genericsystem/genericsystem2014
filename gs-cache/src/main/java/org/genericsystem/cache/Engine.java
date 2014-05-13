package org.genericsystem.cache;

import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;

public class Engine extends Generic implements EngineService<Generic> {

	private final Root root = buildRoot();
	private final Cache currentCache = buildCache();

	public Engine() {
		init(null, getEmptyArray(), Statics.ENGINE_VALUE, getEmptyArray());
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
