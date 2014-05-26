package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.genericsystem.kernel.Snapshot;

public interface InheritanceService<T extends InheritanceService<T>> extends DependenciesService<T>, SystemPropertiesService, ExceptionAdviserService<T> {

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

	default List<T> computeSupers(List<T> overrides) {
		class SupersComputer extends LinkedHashSet<T> {

			private static final long serialVersionUID = -1078004898524170057L;

			private final List<T> overrides;
			private final Map<T, Boolean> alreadyComputed = new HashMap<>();

			private SupersComputer(List<T> overrides) {
				this.overrides = overrides;
				visit(getMeta().getRoot());
			}

			private boolean visit(T candidate) {
				Boolean result = alreadyComputed.get(candidate);
				if (result != null)
					return result;
				boolean isMeta = getMeta().isSpecializationOf(candidate);
				boolean isSuper = ((InheritanceService<T>) candidate).isSuperOf(getMeta(), overrides, getValue(), getComponents());
				if (!isMeta && !isSuper) {
					alreadyComputed.put(candidate, false);
					return false;
				}
				boolean selectable = true;
				for (T inheriting : candidate.getInheritings())
					if (visit(inheriting))
						selectable = false;
				if (isMeta)
					for (T instance : candidate.getInstances())
						if (visit(instance))
							selectable = false;

				result = alreadyComputed.put(candidate, selectable);
				assert result == null;
				if (selectable && candidate.getLevel() == getLevel() && !candidate.equals(InheritanceService.this))
					add(candidate);
				return selectable;
			}
		}
		return new ArrayList<T>(new SupersComputer(overrides));
	}
}
