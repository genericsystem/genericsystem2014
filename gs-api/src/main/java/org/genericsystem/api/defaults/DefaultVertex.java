package org.genericsystem.api.defaults;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.api.core.ISignature;
import org.genericsystem.api.exception.AmbiguousSelectionException;

public interface DefaultVertex<T extends DefaultVertex<T>> extends DefaultAncestors<T>, DefaultDependencies<T>, DefaultDisplay<T>, DefaultSystemProperties<T>, DefaultCompositesInheritance<T>, DefaultWritable<T>, Comparable<T> {

	@Override
	default DefaultContext<T> getCurrentCache() {
		return getRoot().getCurrentCache();
	}

	@Override
	default boolean isAlive() {
		return getCurrentCache().isAlive((T) this);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addInstance(List<T> overrides, Serializable value, T... components) {
		return getCurrentCache().addInstance((T) this, overrides, value, Arrays.asList(components));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setInstance(List<T> overrides, Serializable value, T... components) {
		return getCurrentCache().setInstance((T) this, overrides, value, Arrays.asList(components));
	}

	@SuppressWarnings("unchecked")
	@Override
	default T update(List<T> overrides, Serializable newValue, T... newComponents) {
		return getCurrentCache().update((T) this, overrides, newValue, Arrays.asList(newComponents));
	}

	static <T extends DefaultVertex<T>> boolean isSuperOf(T subMeta, Serializable subValue, List<T> subComponents, T superMeta, Serializable superValue, List<T> superComponents) {
		if (subMeta == null) {
			if (!superMeta.isMeta())
				return false;
		} else if (!subMeta.inheritsFrom(superMeta))
			return false;
		boolean[] singular = new boolean[1];
		if (!superMeta.componentsDepends(subComponents, superComponents, singular))
			return false;
		if (singular[0])
			return true;

		if (superMeta.isPropertyConstraintEnabled())
			if (!subComponents.equals(superComponents))
				return true;
		return Objects.equals(subValue, superValue);
	}

	default boolean inheritsFrom(T superMeta, Serializable superValue, List<T> superComponents) {
		return inheritsFrom(superMeta, Collections.<T> emptyList(), superValue, superComponents);
	}

	default boolean inheritsFrom(T superMeta, List<T> overrides, Serializable superValue, List<T> superComponents) {
		return isSuperOf(getMeta(), getValue(), getComponents(), superMeta, superValue, superComponents) && ApiStatics.areOverridesReached(getSupers(), overrides);
	}

	default boolean isDependencyOf(T meta, List<T> supers, Serializable value, List<T> components) {
		return inheritsFrom(meta, supers, value, components) || getComponents().stream().anyMatch(component -> component.isDependencyOf(meta, supers, value, components)) || (!isMeta() && getMeta().isDependencyOf(meta, supers, value, components))
				|| (!components.equals(getComponents()) && componentsDepends(getComponents(), components) && supers.stream().anyMatch(override -> override.inheritsFrom(getMeta())));
	}

	@SuppressWarnings("unchecked")
	default boolean isSuperOf(T subMeta, List<T> overrides, Serializable subValue, List<T> subComponents) {
		return overrides.stream().anyMatch(override -> override.inheritsFrom((T) this)) || (ApiStatics.areOverridesReached(getSupers(), overrides) && isSuperOf(subMeta, subValue, subComponents, getMeta(), getValue(), getComponents()));
	}

	default boolean componentsDepends(List<T> subComponents, List<T> superComponents) {
		return componentsDepends(subComponents, superComponents, new boolean[1]);
	}

	@SuppressWarnings("unchecked")
	default boolean componentsDepends(List<T> subComponents, List<T> superComponents, boolean[] singular) {
		singular[0] = false;
		int subIndex = 0;
		loop: for (T superComponent : superComponents) {
			for (; subIndex < subComponents.size(); subIndex++) {
				T subComponent = subComponents.get(subIndex);
				if (subComponent.isSpecializationOf(superComponent) || isSpecializationOf(superComponent) || subComponent.isSpecializationOf((T) this)) {
					if (isSingularConstraintEnabled(subIndex) && subComponent != superComponent)
						singular[0] = true;
					subIndex++;
					continue loop;
				}
			}
			return singular[0];
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	default T[] coerceToTArray(Object... array) {
		T[] result = getCurrentCache().newTArray(array.length);
		for (int i = 0; i < array.length; i++)
			result[i] = (T) array[i];
		return result;
	}

	@SuppressWarnings("unchecked")
	default T adjustMeta(Serializable value, T... components) {
		return adjustMeta(value, Arrays.asList(components));
	}

	@SuppressWarnings("unchecked")
	default T adjustMeta(Serializable value, List<T> components) {
		T result = null;
		if (!components.equals(getComponents()))
			for (T directInheriting : getInheritings()) {
				if (componentsDepends(components, directInheriting.getComponents())) {
					if (result == null) {
						result = directInheriting;
					} else
						getCurrentCache().discardWithException(new AmbiguousSelectionException("Ambigous selection : " + result.info() + directInheriting.info()));
				}
			}
		return result == null ? (T) this : result.adjustMeta(value, components);
	}

	@SuppressWarnings("unchecked")
	default T getDirectInstance(List<T> overrides, Serializable value, List<T> components) {
		if (isMeta() && equalsRegardlessSupers(this, value, components))
			return (T) this;
		for (T instance : getInstances())
			if (instance.equalsRegardlessSupers(this, value, components))
				if (ApiStatics.areOverridesReached(instance.getSupers(), overrides))
					if (!instance.getSupers().stream().anyMatch(superG -> superG.getComponents().equals(components) && equals(superG.getMeta())) || ApiStatics.areOverridesReached(overrides, instance.getSupers()))
						return instance;
		return null;
	}

	@SuppressWarnings("unchecked")
	default T getDirectEquivInstance(Serializable value, List<T> components) {
		if (isMeta() && equalsRegardlessSupers(this, value, components))
			return (T) this;
		for (T instance : getInstances())
			if (instance.equiv(this, value, components))
				return instance;
		return null;
	}

	default boolean equalsAndOverrides(T meta, List<T> overrides, Serializable value, List<T> components) {
		return equalsRegardlessSupers(meta, value, components) && ApiStatics.areOverridesReached(getSupers(), overrides);
	}

	default boolean equals(ISignature<?> meta, List<? extends ISignature<?>> supers, Serializable value, List<? extends ISignature<?>> components) {
		return equalsRegardlessSupers(meta, value, components) && getSupers().equals(supers);
	}

	default boolean equalsRegardlessSupers(ISignature<?> meta, Serializable value, List<? extends ISignature<?>> components) {
		if (!getMeta().equals(meta == null ? this : meta))
			return false;
		if (!Objects.equals(getValue(), value))
			return false;
		List<T> componentsList = getComponents();
		if (componentsList.size() != components.size())
			return false;
		return componentsList.equals(components);
	}

	default boolean genericEquals(ISignature<?> service) {
		if (service == null)
			return false;
		if (this == service)
			return true;
		if (!getMeta().genericEquals(service == service.getMeta() ? this : service.getMeta()))
			return false;
		if (!Objects.equals(getValue(), service.getValue()))
			return false;
		List<T> componentsList = getComponents();
		if (componentsList.size() != service.getComponents().size())
			return false;
		for (int i = 0; i < componentsList.size(); i++)
			if (!genericEquals(componentsList.get(i), service.getComponents().get(i)))
				return false;
		List<T> supersList = getSupers();
		if (supersList.size() != service.getSupers().size())
			return false;
		for (int i = 0; i < supersList.size(); i++)
			if (!supersList.get(i).genericEquals(service.getSupers().get(i)))
				return false;
		return true;
	}

	public static <T extends DefaultVertex<T>> boolean genericEquals(T component, ISignature<?> compare) {
		return (component == compare) || (component != null && component.genericEquals(compare));
	}

	public static <T extends DefaultVertex<T>> boolean equiv(T component, ISignature<?> compare) {
		return (component == compare) || (component != null && component.equiv(compare));
	}

	default boolean equiv(ISignature<? extends ISignature<?>> service) {
		if (service == null)
			return false;
		if (this == service)
			return true;
		return equiv(service.getMeta(), service.getValue(), service.getComponents());
	}

	default boolean equiv(ISignature<?> meta, Serializable value, List<? extends ISignature<?>> components) {
		if (!getMeta().equals(meta == null ? this : meta))
			return false;
		List<T> componentsList = getComponents();
		if (componentsList.size() != components.size())
			return false;
		for (int i = 0; i < componentsList.size(); i++)
			if (!getMeta().isReferentialIntegrityEnabled(i) && getMeta().isSingularConstraintEnabled(i))
				return equiv(componentsList.get(i), components.get(i));
		for (int i = 0; i < componentsList.size(); i++)
			if (!equiv(componentsList.get(i), components.get(i)))
				return false;
		if (!getMeta().isPropertyConstraintEnabled())
			return Objects.equals(getValue(), value);
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	default void traverse(Visitor<T> visitor) {
		visitor.traverse((T) this);
	}

	@Override
	default boolean isSystem() {
		return getLifeManager().isSystem();
	}

	@Override
	default int compareTo(T vertex) {
		long birthTs = getLifeManager().getBirthTs();
		long compareBirthTs = vertex.getLifeManager().getBirthTs();
		return birthTs == compareBirthTs ? Long.compare(getTs(), vertex.getTs()) : Long.compare(birthTs, compareBirthTs);
	}

	default long getTs() {
		return getRoot().getTs((T) this);
	}

	default DefaultLifeManager getLifeManager() {
		return getRoot().getLifeManager((T) this);
	}

	@Override
	default T getMeta() {
		return getRoot().getMeta((T) this);
	}

	@Override
	default List<T> getComponents() {
		return getRoot().getComponents((T) this);
	}

	@Override
	default Serializable getValue() {
		return getRoot().getValue((T) this);
	}

	@Override
	default List<T> getSupers() {
		return getRoot().getSupers((T) this);
	}
}
