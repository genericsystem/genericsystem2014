package org.genericsystem.mutability;

import org.genericsystem.concurrency.Vertex;
import org.genericsystem.kernel.Dependencies;

public class Generic extends AbstractGeneric<Generic, org.genericsystem.concurrency.Generic, Vertex> implements DefaultGeneric<Generic, org.genericsystem.concurrency.Generic, Vertex> {

	private final Dependencies<Generic> instances = buildDependencies();
	private final Dependencies<Generic> inheritings = buildDependencies();
	private final Dependencies<Generic> composites = buildDependencies();

	@Override
	protected Dependencies<Generic> getInstancesDependencies() {
		return instances;
	}

	@Override
	protected Dependencies<Generic> getInheritingsDependencies() {
		return inheritings;
	}

	@Override
	protected Dependencies<Generic> getCompositesDependencies() {
		return composites;
	}

	@Override
	protected Generic newT() {
		return new Generic();
	}

	@Override
	protected Generic[] newTArray(int dim) {
		return new Generic[dim];
	}

	@Override
	public DefaultEngine<Generic, org.genericsystem.concurrency.Generic, Vertex> getRoot() {
		return (DefaultEngine<Generic, org.genericsystem.concurrency.Generic, Vertex>) super.getRoot();
	}
}
