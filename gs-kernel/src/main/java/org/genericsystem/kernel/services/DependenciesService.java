package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.genericsystem.kernel.Dependencies.DependenciesEntry;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.Statics;

public interface DependenciesService<T extends DependenciesService<T>> extends AncestorsService<T>, SystemPropertiesService<T>, ExceptionAdviserService<T> {

	Snapshot<T> getInstances();

	Snapshot<T> getInheritings();

	Snapshot<DependenciesEntry<T>> getMetaComposites();

	Snapshot<DependenciesEntry<T>> getSuperComposites();

	default boolean isAncestorOf(final T dependency) {
		return equiv(dependency) || (!dependency.equals(dependency.getMeta()) && isAncestorOf(dependency.getMeta())) || dependency.getSupersStream().anyMatch(component -> this.isAncestorOf(component))
				|| dependency.getComponentsStream().filter(component -> !dependency.equals(component)).anyMatch(component -> this.isAncestorOf(component))
				|| inheritsFrom(dependency.getMeta(), dependency.getValue(), dependency.getComponents(), getMeta(), getValue(), getComponents());
	}

	default LinkedHashSet<T> computeAllDependencies() {
		class DirectDependencies extends LinkedHashSet<T> {
			private static final long serialVersionUID = -5970021419012502402L;
			private final Set<T> alreadyVisited = new HashSet<>();

			public DirectDependencies() {
				visit(getMeta());
			}

			public void visit(T node) {
				if (!alreadyVisited.contains(node))
					if (!isAncestorOf(node)) {
						alreadyVisited.add(node);
						node.getComposites().forEach(this::visit);
						node.getInheritings().forEach(this::visit);
						node.getInstances().forEach(this::visit);
					} else
						addDependency(node);
			}

			public void addDependency(T node) {
				if (!alreadyVisited.contains(node)) {
					alreadyVisited.add(node);
					node.getComposites().forEach(this::addDependency);
					node.getInheritings().forEach(this::addDependency);
					node.getInstances().forEach(this::addDependency);
					super.add(node);
				}
			}
		}
		return new DirectDependencies();
	}

	default Snapshot<T> getComposites() {
		return () -> Statics.concat(getMetaComposites().stream(), entry -> entry.getValue().stream()).iterator();
	}

	@SuppressWarnings("unchecked")
	default boolean isSuperOf(T subMeta, List<T> overrides, Serializable subValue, List<T> subComponents) {
		return overrides.stream().anyMatch(override -> override.inheritsFrom((T) this)) || inheritsFrom(subMeta, subValue, subComponents, getMeta(), getValue(), getComponents());
	}

	default boolean isMetaOf(T subMeta, List<T> overrides, Serializable subValue, List<T> subComponents) {
		return /* !subComponents.equals(getComponents()) && */(subMeta.componentsDepends(subComponents, getComponents()));
	}

	default boolean inheritsFrom(T subMeta, Serializable subValue, List<T> subComponents, T superMeta, Serializable superValue, List<T> superComponents) {
		if (!subMeta.inheritsFrom(superMeta))
			return false;
		if (!subMeta.componentsDepends(subComponents, superComponents))
			return false;
		return subMeta.isPropertyConstraintEnabled() || Objects.equals(subValue, superValue);
	}

	static interface SingularsLazyCache {
		boolean get(int i);
	}

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

	default boolean componentsDepends(SingularsLazyCache singulars, List<T> subComponents, List<T> superComponents) {
		int subIndex = 0;
		loop: for (T superComponent : superComponents) {
			for (; subIndex < subComponents.size(); subIndex++) {
				T subComponent = subComponents.get(subIndex);
				if (subComponent.inheritsFrom(superComponent) || subComponent.isInstanceOf(superComponent)) {
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

	default boolean isSpecializationOf(T supra) {
		return getLevel() == supra.getLevel() ? inheritsFrom(supra) : (getLevel() > supra.getLevel() && getMeta().isSpecializationOf(supra));
	}

	default Stream<T> getSupras() {
		return Stream.concat(Stream.of(getMeta()), getSupersStream());
	}

	default Snapshot<T> getSpecializations() {
		return () -> Stream.concat(getInheritings().stream(), getInstances().stream()).iterator();
	}

}
