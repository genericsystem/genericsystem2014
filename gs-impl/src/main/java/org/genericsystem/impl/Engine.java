package org.genericsystem.impl;

import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;

public class Engine extends Generic implements EngineService<Generic> {

	private final Root root;

	public Engine() {
		root = buildRoot();
		initFromSupers(null, getEmptyArray(), Statics.ENGINE_VALUE, getEmptyArray());
	}

	@Override
	public Vertex getAlive() {
		return root;
	}

	@Override
	public Engine getRoot() {
		return (Engine) EngineService.super.getRoot();
		// return super.getRoot();
	}

	// @Override
	// public boolean isRoot() {
	// return EngineService.super.isRoot();
	// // return super.isRoot();
	// }
}
