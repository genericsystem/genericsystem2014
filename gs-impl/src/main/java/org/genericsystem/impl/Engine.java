package org.genericsystem.impl;

import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;

public class Engine extends Generic implements EngineService<Generic> {

	private final Root root;

	public Engine() {
		super(null, new Generic[] {}, Statics.ENGINE_VALUE, new Generic[] {});
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

	// Not necessary ! where is the logic ?
	// @Override
	// public boolean isRoot() {
	// return EngineService.super.isRoot();
	// }

}
