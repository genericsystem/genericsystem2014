package org.genericsystem.impl;

import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;

public class Engine<T extends Generic<T>> extends Generic<T> implements EngineService<T> {

	private final Root root;

	public Engine() {
		super(null, (T[]) new Generic[] {}, Statics.ENGINE_VALUE, (T[]) new Generic[] {});
		root = buildVerticesRoot();
	}

	@Override
	public Vertex getAlive() {
		return root;
	}

	// Why is this necessary ??? what does maven do here ?
	@Override
	public T getRoot() {
		return EngineService.super.getRoot();
	}

	// Not necessary ! where is the logic ?
	// @Override
	// public boolean isRoot() {
	// return EngineService.super.isRoot();
	// }

}
