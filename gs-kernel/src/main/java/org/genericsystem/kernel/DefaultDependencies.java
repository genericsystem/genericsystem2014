package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.genericsystem.api.core.IVertex;
import org.genericsystem.api.core.Snapshot;

public interface DefaultDependencies<T extends AbstractVertex<T>> extends IVertex<T> {

	@SuppressWarnings("unchecked")
	@Override
	default boolean isAlive() {
		return getCurrentCache().isAlive((T)this);
	}

	@Override
	default boolean isAncestorOf(T dependency) {
		return equals(dependency) || (!dependency.isMeta() && isAncestorOf(dependency.getMeta())) || dependency.getSupers().stream().anyMatch(this::isAncestorOf) || dependency.getComponents().stream().filter(x -> x != null).anyMatch(this::isAncestorOf);
	}
	
	@Override
	default Context<T> getCurrentCache() {
		return (Context<T>) getRoot().getCurrentCache();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getInstances() {
		return getCurrentCache().getInstances((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getInheritings() {
		return getCurrentCache().getInheritings((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getComposites() {
		return getCurrentCache().getComposites((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getAllInheritings() {
		return () -> Stream.concat(Stream.of((T) this), getInheritings().get().flatMap(inheriting -> inheriting.getAllInheritings().get())).distinct();
	}

	@Override
	default Snapshot<T> getAllInstances() {
		return () -> getAllInheritings().get().flatMap(inheriting -> inheriting.getInstances().get());
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getInstance(T superT, Serializable value, T... composites) {
		return getInstance(Collections.singletonList(superT), value, composites);
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getInstance(Serializable value, T... composites) {
		return getInstance(Collections.emptyList(), value, composites);
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getInstance(List<T> overrides, Serializable value, T... components) {
		return ((T) this).adjustMeta(value, Arrays.asList(components)).getDirectInstance(overrides, value, Arrays.asList(components));
	}
}
