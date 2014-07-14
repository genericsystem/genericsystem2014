package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.Statics;

public interface DependenciesService<T extends VertexService<T>> extends ApiService<T> {

	// TODO KK
	@Override
	default boolean isAncestorOf(final T dependency) {
		return equiv(dependency) || (!dependency.equals(dependency.getMeta()) && isAncestorOf(dependency.getMeta())) || dependency.getSupersStream().anyMatch(component -> this.isAncestorOf(component))
				|| dependency.getComponentsStream().filter(component -> !dependency.equals(component)).anyMatch(component -> this.isAncestorOf(component))
				|| inheritsFrom(dependency.getMeta(), dependency.getValue(), dependency.getComponents(), getMeta(), getValue(), getComponents());
	}

	@Override
	@SuppressWarnings("unchecked")
	default boolean isSuperOf(T subMeta, List<T> overrides, Serializable subValue, List<T> subComponents) {
		return overrides.stream().anyMatch(override -> override.inheritsFrom((T) this)) || inheritsFrom(subMeta, subValue, subComponents, getMeta(), getValue(), getComponents());
	}

	@Override
	default boolean inheritsFrom(T subMeta, Serializable subValue, List<T> subComponents, T superMeta, Serializable superValue, List<T> superComponents) {
		if (!subMeta.inheritsFrom(superMeta))
			return false;
		if (!subMeta.componentsDepends(subComponents, superComponents))
			return false;
		return subMeta.getValuesBiPredicate().test(subValue, superValue);
	}

	static interface SingularsLazyCache {
		boolean get(int i);
	}

	@Override
	default boolean componentsDepends(List<T> subComponents, List<T> superComponents) {
		class SingularsLazyCacheImpl implements SingularsLazyCache {
			private final Boolean[] singulars = new Boolean[subComponents.size()];

			@Override
			public boolean get(int i) {
				return singulars[i] != null ? singulars[i] : (singulars[i] = DependenciesService.this.isSingularConstraintEnabled(i));
			}
		}
		return componentsDepends(new SingularsLazyCacheImpl(), subComponents, superComponents);
	}

	static <T extends VertexService<T>> boolean componentsDepends(SingularsLazyCache singulars, List<T> subComponents, List<T> superComponents) {
		int subIndex = 0;
		loop: for (T superComponent : superComponents) {
			for (; subIndex < subComponents.size(); subIndex++) {
				T subComponent = subComponents.get(subIndex);
				if (subComponent.isSpecializationOf(superComponent)) {
					if (singulars.get(subIndex))
						return true;
					subIndex++;
					continue loop;
				}
			}
			return false;
		}
		return true;
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
		return () -> getAllInheritings().stream().map(inheriting -> ((DependenciesService<T>) inheriting).getInstances().stream()).flatMap(x -> x).iterator();// .reduce(Stream.empty(), Stream::concat);
	}
}
