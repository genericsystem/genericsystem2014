package org.genericsystem.cache;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.Vertex;

public interface GenericService<T extends GenericService<T>> extends org.genericsystem.impl.GenericService<T> {

	default Cache<T> getCurrentCache() {
		return getMeta().getCurrentCache();
	}

	@SuppressWarnings("unchecked")
	@Override
	default Dependencies<T> getInheritings() {
		return getCurrentCache().getInheritings((T) this, () -> iteratorFromAlive(Vertex::getInheritings));
	}

	default Iterator<T> iteratorFromAlive(Function<Vertex, Dependencies<Vertex>> dependencies) {
		Vertex alive = getAlive();
		return alive == null ? Collections.emptyIterator() : dependencies.apply(alive).project(this::wrap).iterator();
	}

	@SuppressWarnings("unchecked")
	@Override
	default Dependencies<T> getInstances() {
		return getCurrentCache().getInstances((T) this, () -> org.genericsystem.impl.GenericService.super.getInstances().iterator());
	}

	@SuppressWarnings("unchecked")
	@Override
	default public CompositesDependencies<T> getMetaComposites() {
		return getCurrentCache().getMetaComposites((T) this, () -> org.genericsystem.impl.GenericService.super.getMetaComposites().iterator());
	}

	@SuppressWarnings("unchecked")
	@Override
	default public CompositesDependencies<T> getSuperComposites() {
		return getCurrentCache().getSuperComposites((T) this, () -> org.genericsystem.impl.GenericService.super.getSuperComposites().iterator());
	}

}
