package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.genericsystem.api.core.IVertex;
import org.genericsystem.api.core.Snapshot;

public interface DefaultDependencies<T extends AbstractVertex<T>> extends IVertex<T> {

	@Override
	default boolean isAncestorOf(T dependency) {
		return equals(dependency) || (!dependency.isMeta() && isAncestorOf(dependency.getMeta())) || dependency.getSupers().stream().anyMatch(this::isAncestorOf)
				|| dependency.getComponents().stream().filter(component -> !dependency.equals(component)).anyMatch(this::isAncestorOf);
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
		T adjustedMeta = ((T) this).adjustMeta(value, Arrays.asList(components));
		return adjustedMeta.getDirectInstance(overrides, value, Arrays.asList(components));
	}
}
