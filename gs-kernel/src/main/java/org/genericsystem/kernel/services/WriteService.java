package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.genericsystem.kernel.RootService;

public interface WriteService<T extends VertexService<T>> extends ApiService<T> {

	@Override
	void remove();

	@Override
	default T updateValue(Serializable newValue) {
		return update(getSupers(), newValue, getComponents());
	}

	@Override
	@SuppressWarnings("unchecked")
	default T updateSupers(T... supersToAdd) {
		return update(Arrays.asList(supersToAdd), getValue(), getComponents());
	}

	@Override
	@SuppressWarnings("unchecked")
	default T updateComponents(T... newComponents) {
		return update(getSupers(), getValue(), newComponents);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T update(List<T> supersToAdd, Serializable newValue, T... newComponents) {
		return update(supersToAdd, newValue, Arrays.asList(newComponents));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T update(Serializable newValue, T... newComponents) {
		return update(Collections.emptyList(), newValue, Arrays.asList(newComponents));
	}

	@Override
	T update(List<T> supersToAdd, Serializable newValue, List<T> newComponents);

	@Override
	default T setInstance(Serializable value, @SuppressWarnings("unchecked") T... components) {
		return setInstance(Collections.emptyList(), value, components);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setInstance(T override, Serializable value, T... components) {
		return setInstance(Collections.singletonList(override), value, components);
	}

	@Override
	@SuppressWarnings("unchecked")
	T setInstance(List<T> overrides, Serializable value, T... components);

	@Override
	default T getMetaAttribute() {
		RootService<T> root = getRoot();
		return root.getInstance(root.getValue(), coerceToArray(root));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addInstance(Serializable value, T... components) {
		return addInstance(Collections.emptyList(), value, components);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addInstance(T superGeneric, Serializable value, T... components) {
		return addInstance(Collections.singletonList(superGeneric), value, components);
	}

	@Override
	@SuppressWarnings("unchecked")
	T addInstance(List<T> overrides, Serializable value, T... components);

	@Override
	T[] coerceToArray(Object... array);
}
