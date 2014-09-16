package org.genericsystem.impl;

import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Vertex;

public class Generic extends AbstractGeneric<Generic, Engine, Vertex, Root> implements IGeneric<Generic, Engine> {

	@Override
	protected Generic newT() {
		return new Generic();
	}

	@Override
	public Generic[] newTArray(int dim) {
		return new Generic[dim];
	}
}
