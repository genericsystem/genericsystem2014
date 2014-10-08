package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.genericsystem.api.core.IVertexBase;
import org.genericsystem.api.core.Snapshot;

public interface IDependencies<T extends AbstractVertex<T, U>, U extends IRoot<T, U>> extends IVertexBase<T, U> {

	@Override
	default boolean isAncestorOf(T dependency) {
		return equals(dependency) || (!dependency.isRoot() && isAncestorOf(dependency.getMeta())) || dependency.getSupers().stream().anyMatch(this::isAncestorOf)
				|| dependency.getComposites().stream().filter(composite -> !dependency.equals(composite)).anyMatch(this::isAncestorOf);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getAllInheritings() {
		return () -> Stream.of((T) this).flatMap(inheriting -> inheriting.getInheritings().stream()).iterator();
		// return () -> Statics.concat(Stream.of((T) this), inheriting -> inheriting.getAllInheritings().stream()).distinct().iterator();
	}

	@Override
	default Snapshot<T> getAllInstances() {
		return () -> getAllInheritings().stream().flatMap(inheriting -> inheriting.getInstances().stream()).iterator();
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addInstance(List<T> overrides, Serializable value, T... composites) {
		return ((T) this).bindInstance(null, true, overrides, value, Arrays.asList(composites));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setInstance(List<T> overrides, Serializable value, T... composites) {
		return ((T) this).bindInstance(null, false, overrides, value, Arrays.asList(composites));
	}

	@Override
	default T getInstance(T superT, Serializable value, @SuppressWarnings("unchecked") T... components) {
		return getInstance(Collections.singletonList(superT), value, components);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T getInstance(Serializable value, T... components) {
		return getInstance(Collections.emptyList(), value, components);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T getInstance(List<T> overrides, Serializable value, T... composites) {
		T adjustedMeta = ((T) this).adjustMeta(value, Arrays.asList(composites));
		return adjustedMeta.getDirectInstance(overrides, value, Arrays.asList(composites));
	}

	@SuppressWarnings("unchecked")
	default Optional<T> getInstanceInAll(List<T> overrides, Serializable value, T... composites) {
		Stream<T> adjustedMetas = Stream.of((T) this).flatMap(meta -> meta.getInheritings().stream().filter(inheriting -> ((T) IDependencies.this).isAdjusted(inheriting, value, Arrays.asList(composites))));
		return adjustedMetas.map(adjustedMeta -> adjustedMeta.getDirectInstance(overrides, value, Arrays.asList(composites))).findFirst();
	}
}
