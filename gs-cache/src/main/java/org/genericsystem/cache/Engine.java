package org.genericsystem.cache;

import java.util.Collections;

import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;

public class Engine extends Generic implements EngineService<Generic> {

	private final Root root = buildRoot();
	private final Cache<Generic> currentCache = buildCache();

	public Engine() {
		initFromSupers(null, Collections.emptyList(), Statics.ENGINE_VALUE, Collections.emptyList());
	}

	@Override
	public Cache<Generic> getCurrentCache() {
		return currentCache;
	}

	@Override
	public Engine getAlive() {
		return this;
	}

	@Override
	public Vertex getVertex() {
		return root;
	}

	// Why is this necessary ??? what does maven do here ?
	@Override
	public Engine getRoot() {
		return (Engine) EngineService.super.getRoot();
	}

}
