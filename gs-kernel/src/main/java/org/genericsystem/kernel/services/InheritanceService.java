package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public interface InheritanceService<T extends InheritanceService<T>> extends DependenciesService<T>, SystemPropertiesService, ExceptionAdviserService<T> {

	@SuppressWarnings("unchecked")
	default boolean isSuperOf(T subMeta, T[] overrides, Serializable subValue, T... subComponents) {
		return Arrays.asList(overrides).stream().anyMatch(override -> override.inheritsFrom((T) this)) || inheritsFrom(subMeta, subValue, subComponents, getMeta(), getValue(), getComponents());
	}

	default boolean inheritsFrom(T subMeta, Serializable subValue, T[] subComponents, T superMeta, Serializable superValue, T[] superComponents) {
		if (!subMeta.inheritsFrom(superMeta))
			return false;
		if (!((InheritanceService<T>) subMeta).componentsDepends(subComponents, superComponents))
			return false;
		return ((SystemPropertiesService) subMeta).isPropertyConstraintEnabled() || Objects.equals(subValue, superValue);
	}

	static interface SingularsLazyCache {
		boolean get(int i);
	}

	default boolean componentsDepends(T[] subComponents, T[] superComponents) {
		class SingularsLazyCacheImpl implements SingularsLazyCache {
			private final Boolean[] singulars = new Boolean[subComponents.length];

			@Override
			public boolean get(int i) {
				return singulars[i] != null ? singulars[i] : (singulars[i] = InheritanceService.this.isSingularConstraintEnabled(i));
			}
		}
		return componentsDepends(new SingularsLazyCacheImpl(), subComponents, superComponents);
	}

	default boolean componentsDepends(SingularsLazyCache singulars, T[] subComponents, T[] superComponents) {
		int subIndex = 0;
		loop: for (T superComponent : superComponents) {
			for (; subIndex < subComponents.length; subIndex++) {
				T subComponent = subComponents[subIndex];
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
	T[] getComponents();

	default Stream<T> computeSupersStream(T[] overrides) {
		class SupersComputer extends LinkedHashSet<T> {

			private static final long serialVersionUID = -1078004898524170057L;

			private final T[] overrides;
			private final Map<T, Boolean> alreadyComputed = new HashMap<>();

			private SupersComputer(T[] overrides) {
				this.overrides = overrides;
				visit(getMeta().getRoot());
			}

			private boolean visit(T candidate) {
				Boolean result = alreadyComputed.get(candidate);
				if (result != null)
					return result;
				if ((!isRoot() && this.equals(candidate))) {
					alreadyComputed.put(candidate, false);
					return false;
				}
				boolean isMeta = getMeta().inheritsFrom(candidate) || candidate.isRoot();
				boolean isSuper = ((InheritanceService<T>) candidate).isSuperOf(getMeta(), overrides, getValue(), getComponents());
				if (!isMeta && !isSuper) {
					alreadyComputed.put(candidate, false);
					return false;
				}
				boolean selectable = true;
				for (T inheriting : candidate.getInheritings())
					if (visit(inheriting))
						selectable = false;
				if (isMeta) {
					selectable = false;
					for (T instance : candidate.getInstances())
						visit(instance);
				}
				result = alreadyComputed.put(candidate, selectable);
				assert result == null;
				if (selectable)
					add(candidate);
				return selectable;
			}
		}
		return new SupersComputer(overrides).stream();
	}

}
