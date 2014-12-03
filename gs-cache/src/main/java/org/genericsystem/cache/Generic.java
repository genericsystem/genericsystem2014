package org.genericsystem.cache;

import org.genericsystem.kernel.Dependencies;

public class Generic extends AbstractGeneric<Generic> implements DefaultGeneric<Generic> {

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
}
