package org.genericsystem.impl;

import org.genericsystem.impl.Generic.GenericImpl;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;

public class Engine extends GenericImpl implements EngineService<GenericImpl> {

	private final Root root;

	public Engine() {
		super(null, new GenericImpl[] {}, Statics.ENGINE_VALUE, new GenericImpl[] {});
		root = buildVerticesRoot();
	}

	@Override
	public Vertex getAlive() {
		return root;
	}

	// @Override
	// public Engine getRoot() {
	// return (Engine) EngineService.super.getRoot();
	// // return super.getRoot();
	// }

	// @Override
	// public boolean isRoot() {
	// return EngineService.super.isRoot();
	// // return super.isRoot();
	// }
}
