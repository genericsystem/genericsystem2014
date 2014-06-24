package org.genericsystem.cache;

import java.util.Iterator;
import java.util.function.Supplier;
import org.genericsystem.impl.GenericSignature;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;

public class Generic extends GenericSignature<Generic> implements GenericService<Generic> {

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
		return getRoot().getInstance(SystemMap.class, getRoot());
	}

	@Override
	public Dependencies<Generic> getInheritings() {
		return getCurrentCache().getInheritings(this);
	}

	@Override
	public Dependencies<Generic> getInstances() {
		return getCurrentCache().getInstances(this);
	}

	@Override
	public CompositesDependencies<Generic> getMetaComposites() {
		return getCurrentCache().getMetaComposites(this);
	}

	@Override
	public CompositesDependencies<Generic> getSuperComposites() {
		return getCurrentCache().getSuperComposites(this);
	}
}
