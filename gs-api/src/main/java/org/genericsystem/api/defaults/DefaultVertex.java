package org.genericsystem.api.defaults;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.genericsystem.api.core.ISignature;

public interface DefaultVertex<T extends DefaultVertex<T>> extends DefaultAncestors<T>, DefaultDependencies<T>, DefaultDisplay<T>, DefaultSystemProperties<T>, DefaultCompositesInheritance<T>, DefaultWritable<T>, DefaultTree<T> {

	@Override
	default DefaultContext<T> getCurrentCache() {
		return (DefaultContext<T>) getRoot().getCurrentCache();
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addInstance(List<T> overrides, Serializable value, T... components) {
		return getCurrentCache().getBuilder().addInstance(null, (T) this, overrides, value, Arrays.asList(components));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setInstance(List<T> overrides, Serializable value, T... components) {
		return getCurrentCache().getBuilder().setInstance(null, (T) this, overrides, value, Arrays.asList(components));
	}

	@SuppressWarnings("unchecked")
	@Override
	default T update(List<T> overrides, Serializable newValue, T... newComponents) {
		return getCurrentCache().getBuilder().update((T) this, overrides, newValue, Arrays.asList(newComponents));
	}

	static <T extends DefaultVertex<T>> boolean isSuperOf(T subMeta, Serializable subValue, List<T> subComponents, T superMeta, Serializable superValue, List<T> superComponents) {
		if (subMeta == null) {
			if (!superMeta.isMeta())
				return false;
		} else if (!subMeta.inheritsFrom(superMeta))
			return false;
		if (!superMeta.componentsDepends(subComponents, superComponents))
			return false;
		if (superMeta.isPropertyConstraintEnabled())
			return !subComponents.equals(superComponents);
		return Objects.equals(subValue, superValue);
	}

	default boolean inheritsFrom(T superMeta, Serializable superValue, List<T> superComponents) {
		return isSuperOf(getMeta(), getValue(), getComponents(), superMeta, superValue, superComponents);
	}

	default boolean isDependencyOf(T meta, List<T> supers, Serializable value, List<T> components) {
		return inheritsFrom(meta, value, components) || getComponents().stream().filter(component -> component != null).anyMatch(component -> component.isDependencyOf(meta, supers, value, components))
				|| (!isMeta() && getMeta().isDependencyOf(meta, supers, value, components)) || (!components.isEmpty() && componentsDepends(getComponents(), components) && supers.stream().anyMatch(override -> override.inheritsFrom(getMeta())));
	}

	@SuppressWarnings("unchecked")
	default boolean isSuperOf(T subMeta, List<T> overrides, Serializable subValue, List<T> subComponents) {
		return overrides.stream().anyMatch(override -> override.inheritsFrom((T) this)) || isSuperOf(subMeta, subValue, subComponents, getMeta(), getValue(), getComponents());
	}

	boolean equals(ISignature<?> meta, List<? extends ISignature<?>> supers, Serializable value, List<? extends ISignature<?>> components);

	@SuppressWarnings("unchecked")
	default boolean componentsDepends(List<T> subComponents, List<T> superComponents) {
		int subIndex = 0;
		loop: for (T superComponent : superComponents) {
			for (; subIndex < subComponents.size(); subIndex++) {
				T subComponent = subComponents.get(subIndex);
				if ((subComponent == null && superComponent == null) || (subComponent != null && superComponent != null && subComponent.isSpecializationOf(superComponent))
						|| (subComponent == null && superComponent != null && this.isSpecializationOf(superComponent)) || (subComponent != null && superComponent == null && subComponent.isSpecializationOf((T) this))) {
					if (isSingularConstraintEnabled(subIndex))
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
	@Override
	default T[] coerceToTArray(Object... array) {
		T[] result = getCurrentCache().getBuilder().newTArray(array.length);
		for (int i = 0; i < array.length; i++)
			result[i] = (T) array[i];
		return result;
	}

}