package org.genericsystem.cache;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;

import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.Dependencies.DependenciesEntry;
import org.genericsystem.kernel.Vertex;

public interface GenericService<T extends GenericService<T>> extends org.genericsystem.impl.GenericService<T> {

	default Cache<T> getCurrentCache() {
		return getMeta().getCurrentCache();
	}

	default Iterator<T> iteratorFromAlive(Function<Vertex, Dependencies<Vertex>> dependencies) {
		Vertex vertex = getVertex();
		return vertex == null ? Collections.emptyIterator() : dependencies.apply(vertex).project(this::wrap).iterator();
	}

	default Iterator<DependenciesEntry<T>> iteratorFromAliveComposite(Function<Vertex, CompositesDependencies<Vertex>> dependencies) {
		Vertex vertex = getVertex();
		return vertex == null ? Collections.emptyIterator() : dependencies.apply(vertex).projectComposites(this::wrap, org.genericsystem.impl.GenericService::unwrap).iterator();
	}

	@SuppressWarnings("unchecked")
	@Override
	default Dependencies<T> getInheritings() {
		return getCurrentCache().getInheritings((T) this, () -> iteratorFromAlive(Vertex::getInheritings));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Dependencies<T> getInstances() {
		return getCurrentCache().getInstances((T) this, () -> iteratorFromAlive(Vertex::getInstances));
	}

	@SuppressWarnings("unchecked")
	@Override
	default public CompositesDependencies<T> getMetaComposites() {
		return getCurrentCache().getMetaComposites((T) this, () -> (Iterator<DependenciesEntry<T>>) iteratorFromAliveComposite(Vertex::getMetaComposites));
	}

	@SuppressWarnings("unchecked")
	@Override
	default public CompositesDependencies<T> getSuperComposites() {
		return getCurrentCache().getSuperComposites((T) this, () -> (Iterator<DependenciesEntry<T>>) iteratorFromAliveComposite(Vertex::getSuperComposites));
	}

}
