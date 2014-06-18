package org.genericsystem.cache;

import java.util.Iterator;
import java.util.function.Supplier;

public class Generic extends org.genericsystem.impl.GenericSignature<Generic> implements GenericService<Generic> {

	@Override
	public Generic buildInstance() {
		return new Generic();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U extends Generic> CacheDependencies<U> buildDependencies(Supplier<Iterator<Generic>> subDependenciesSupplier) {
		return (CacheDependencies<U>) new CacheDependencies<Generic>(subDependenciesSupplier);
	}

	@Override
	public boolean isAlive() {
		return getCurrentCache().isAlive(this);
	}

	@Override
	public Generic getMap() {
		return getRoot().getInstance(Map.class, getRoot());
	}
}
