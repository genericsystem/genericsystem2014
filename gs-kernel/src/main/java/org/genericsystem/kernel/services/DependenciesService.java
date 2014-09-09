package org.genericsystem.kernel.services;

import java.util.stream.Stream;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.Statics;

public interface DependenciesService<T extends VertexService<T, U>, U extends RootService<T, U>> extends ApiService<T, U> {

	@Override
	default boolean isAncestorOf(T dependency) {
		return equals(dependency) || (!dependency.isRoot() && isAncestorOf(dependency.getMeta())) || dependency.getSupersStream().anyMatch(this::isAncestorOf)
				|| dependency.getComponentsStream().filter(component -> !dependency.equals(component)).anyMatch(this::isAncestorOf);
	}

	@SuppressWarnings("unchecked")
	default Stream<T> select() {
		return Stream.of((T) this);
	}

	@Override
	default Snapshot<T> getAllInheritings() {
		return () -> (Stream.concat(select(), Statics.concat(getInheritings().stream(), inheriting -> inheriting.getAllInheritings().stream()).distinct())).iterator();
	}

	@Override
	default Snapshot<T> getAllInstances() {
		return () -> getAllInheritings().stream().map(inheriting -> inheriting.getInstances().stream()).flatMap(x -> x).iterator();// .reduce(Stream.empty(), Stream::concat);
	}
}
