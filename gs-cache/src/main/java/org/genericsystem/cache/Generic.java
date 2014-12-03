package org.genericsystem.cache;

import org.genericsystem.kernel.TimestampDependencies;

public class Generic extends AbstractGeneric<Generic> implements DefaultGeneric<Generic> {

	private final TimestampDependencies<Generic> instances = buildDependencies();
	private final TimestampDependencies<Generic> inheritings = buildDependencies();
	private final TimestampDependencies<Generic> composites = buildDependencies();

	@Override
	protected TimestampDependencies<Generic> getInstancesDependencies() {
		return instances;
	}

	@Override
	protected TimestampDependencies<Generic> getInheritingsDependencies() {
		return inheritings;
	}

	@Override
	protected TimestampDependencies<Generic> getCompositesDependencies() {
		return composites;
	}
}
