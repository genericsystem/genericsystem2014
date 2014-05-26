package org.genericsystem.impl;

import java.util.Collections;

import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;

public class Engine extends Generic implements EngineService<Generic> {

	private final Root root = buildRoot();

	public Engine() {
		init(0, null, Collections.emptyList(), Statics.ENGINE_VALUE, Collections.emptyList()).computeSupers();
	}

	@Override
	public Engine getAlive() {
		return this;
	}

	@Override
	public Vertex getVertex() {
		return root;
	}

	@Override
	public Engine getRoot() {
		return (Engine) EngineService.super.getRoot();
		// return super.getRoot();
	}

	@Override
	public void rollback() {
		root.rollback();
	}

	// @Override
	// public boolean isRoot() {
	// return EngineService.super.isRoot();
	// // return super.isRoot();
	// }
}
