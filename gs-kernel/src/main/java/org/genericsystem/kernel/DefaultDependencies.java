package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.genericsystem.api.core.IVertex;
import org.genericsystem.api.core.Snapshot;

public interface DefaultDependencies<T extends AbstractVertex<T, U>, U extends DefaultRoot<T, U>> extends IVertex<T, U> {

	@Override
	default boolean isAncestorOf(T dependency) {
		return equals(dependency) || (!dependency.isRoot() && isAncestorOf(dependency.getMeta())) || dependency.getSupers().stream().anyMatch(this::isAncestorOf)
				|| dependency.getComponents().stream().filter(component -> !dependency.equals(component)).anyMatch(this::isAncestorOf);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getAllInheritings() {
		return () -> Stream.concat(Stream.of((T) this), getInheritings().stream().flatMap(inheriting -> inheriting.getAllInheritings().stream())).distinct().iterator();
	}

	@Override
	default Snapshot<T> getAllInstances() {
		return () -> getAllInheritings().stream().flatMap(inheriting -> inheriting.getInstances().stream()).iterator();
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addInstance(List<T> overrides, Serializable value, T... components) {
		return ((T) this).bindInstance(null, true, overrides, value, Arrays.asList(components));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setInstance(List<T> overrides, Serializable value, T... components) {
		return ((T) this).bindInstance(null, false, overrides, value, Arrays.asList(components));
	}

	@Override
	default T getInstance(T superT, Serializable value, @SuppressWarnings("unchecked") T... composites) {
		return getInstance(Collections.singletonList(superT), value, composites);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T getInstance(Serializable value, T... composites) {
		return getInstance(Collections.emptyList(), value, composites);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T getInstance(List<T> overrides, Serializable value, T... components) {
		T adjustedMeta = ((T) this).adjustMeta(value, Arrays.asList(components));
		return adjustedMeta.getDirectInstance(overrides, value, Arrays.asList(components));
	}

	@SuppressWarnings("unchecked")
	default Optional<T> getInstanceInAll(List<T> overrides, Serializable value, T... components) {
		Stream<T> adjustedMetas = Stream.of((T) this).flatMap(meta -> meta.getInheritings().stream().filter(inheriting -> ((T) DefaultDependencies.this).isAdjusted(inheriting, value, Arrays.asList(components))));
		return adjustedMetas.map(adjustedMeta -> adjustedMeta.getDirectInstance(overrides, value, Arrays.asList(components))).findFirst();
	}
}
