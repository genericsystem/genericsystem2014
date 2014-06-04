package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.genericsystem.kernel.Snapshot;

public interface InheritanceService<T extends InheritanceService<T>> extends DependenciesService<T>, SystemPropertiesService<T>, ExceptionAdviserService<T> {

	@SuppressWarnings("unchecked")
	default boolean isSuperOf(T subMeta, List<T> overrides, Serializable subValue, List<T> subComponents) {
		return overrides.stream().anyMatch(override -> override.inheritsFrom((T) this)) || inheritsFrom(subMeta, subValue, subComponents, getMeta(), getValue(), getComponents());
	}

	default boolean isMetaOf(T subMeta, List<T> overrides, Serializable subValue, List<T> subComponents) {
		return /* !subComponents.equals(getComponents()) && */(((InheritanceService<T>) subMeta).componentsDepends(subComponents, getComponents()));
	}

	default boolean inheritsFrom(T subMeta, Serializable subValue, List<T> subComponents, T superMeta, Serializable superValue, List<T> superComponents) {
		if (!subMeta.inheritsFrom(superMeta))
			return false;
		if (!((InheritanceService<T>) subMeta).componentsDepends(subComponents, superComponents))
			return false;
		return ((SystemPropertiesService) subMeta).isPropertyConstraintEnabled() || Objects.equals(subValue, superValue);
	}

	static interface SingularsLazyCache {
		boolean get(int i);
	}

	default boolean componentsDepends(List<T> subComponents, List<T> superComponents) {
		class SingularsLazyCacheImpl implements SingularsLazyCache {
			private final Boolean[] singulars = new Boolean[subComponents.size()];

			@Override
			public boolean get(int i) {
				return singulars[i] != null ? singulars[i] : (singulars[i] = InheritanceService.this.isSingularConstraintEnabled(i));
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

	// TODO Remove this
	@Override
	List<T> getComponents();

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
