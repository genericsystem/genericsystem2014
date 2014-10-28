package org.genericsystem.concurrency;

public class Generic extends AbstractGeneric implements DefaultGeneric {

	@Override
	protected Generic newT() {
		return new Generic();
	}

	@Override
	protected Generic[] newTArray(int dim) {
		return new Generic[dim];
	}
}
