package org.genericsystem.cdi;

import org.genericsystem.concurrency.AbstractGeneric;
import org.genericsystem.concurrency.IGeneric;

public class Generic extends AbstractGeneric<Generic, Engine, Vertex, Root> implements IGeneric<Generic, Engine, Vertex, Root> {

	@Override
	protected Generic newT() {
		return new Generic();
	}

	@Override
	protected Generic[] newTArray(int dim) {
		return new Generic[dim];
	}
}
