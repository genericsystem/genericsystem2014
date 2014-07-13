package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface UpdatableService<T extends UpdatableService<T>> extends BindingService<T> {

	void remove();

	default T updateValue(Serializable newValue) {
		return update(getSupers(), newValue, getComponents());
	}

	@SuppressWarnings("unchecked")
	default T updateSupers(T... supersToAdd) {
		return update(Arrays.asList(supersToAdd), getValue(), getComponents());
	}

	@SuppressWarnings("unchecked")
	default T updateComponents(T... newComponents) {
		return update(getSupers(), getValue(), newComponents);
	}

	@SuppressWarnings("unchecked")
	default T update(List<T> supersToAdd, Serializable newValue, T... newComponents) {
		return update(supersToAdd, newValue, Arrays.asList(newComponents));
	}

	@SuppressWarnings("unchecked")
	default T update(Serializable newValue, T... newComponents) {
		return update(Collections.emptyList(), newValue, Arrays.asList(newComponents));
	}

	T update(List<T> supersToAdd, Serializable newValue, List<T> newComponents);

	default T setInstance(Serializable value, @SuppressWarnings("unchecked") T... components) {
		return setInstance(Collections.emptyList(), value, components);
	}

	@SuppressWarnings("unchecked")
	default T setInstance(T override, Serializable value, T... components) {
		return setInstance(Collections.singletonList(override), value, components);
	}

	@SuppressWarnings("unchecked")
	T setInstance(List<T> overrides, Serializable value, T... components);

	default T getMetaAttribute() {
		T root = getRoot();
		return root.getInstance(root.getValue(), coerceToArray(root));
	}

	@SuppressWarnings("unchecked")
	default T addInstance(Serializable value, T... components) {
		return addInstance(Collections.emptyList(), value, components);
	}

	@SuppressWarnings("unchecked")
	default T addInstance(T superGeneric, Serializable value, T... components) {
		return addInstance(Collections.singletonList(superGeneric), value, components);
	}

	@SuppressWarnings("unchecked")
	T addInstance(List<T> overrides, Serializable value, T... components);

	T[] coerceToArray(Object... array);
}
