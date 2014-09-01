package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface WriteService<T extends VertexService<T, U>, U extends RootService<T, U>> extends ApiService<T, U> {

	@Override
	void remove();

	@Override
	default T updateValue(Serializable newValue) {
		return update(getSupers(), newValue, coerceToArray(getComponents().toArray()));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T updateSupers(T... supersToAdd) {
		return update(Arrays.asList(supersToAdd), getValue(), coerceToArray(getComponents().toArray()));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T updateComponents(T... newComponents) {
		return update(getSupers(), getValue(), newComponents);
	}

	@Override
	@SuppressWarnings("unchecked")
	T update(List<T> supersToAdd, Serializable newValue, T... newComponents);

	@Override
	@SuppressWarnings("unchecked")
	default T update(Serializable newValue, T... newComponents) {
		return update(Collections.emptyList(), newValue, newComponents);
	}

	@Override
	@SuppressWarnings("unchecked")
	default <subT extends T> subT setInstance(Serializable value, T... components) {
		return setInstance(Collections.emptyList(), value, components);
	}

	@Override
	@SuppressWarnings("unchecked")
	default <subT extends T> subT setInstance(T override, Serializable value, T... components) {
		return setInstance(Collections.singletonList(override), value, components);
	}

	@Override
	default T getMetaAttribute() {
		U root = getRoot();
		return root.getInstance(root.getValue(), coerceToArray(root));
	}

	@Override
	@SuppressWarnings("unchecked")
	default <subT extends T> subT addInstance(Serializable value, T... components) {
		return addInstance(Collections.emptyList(), value, components);
	}

	@Override
	@SuppressWarnings("unchecked")
	default <subT extends T> subT addInstance(T superGeneric, Serializable value, T... components) {
		return addInstance(Collections.singletonList(superGeneric), value, components);
	}

	@Override
	T[] coerceToArray(Object... array);

	@SuppressWarnings("unchecked")
	@Override
	T[] targetsToComponents(T... targets);

	@Override
	@SuppressWarnings("unchecked")
	default T addAttribute(Serializable value, T... targets) {
		return addAttribute(Collections.emptyList(), value, targets);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setAttribute(Serializable value, T... targets) {
		return setAttribute(Collections.emptyList(), value, targets);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addHolder(T attribute, Serializable value, T... targets) {
		return attribute.addInstance(attribute, value, targetsToComponents(targets));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setHolder(T attribute, Serializable value, T... targets) {
		return attribute.setInstance(attribute, value, targetsToComponents(targets));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addAttribute(T superT, Serializable value, T... targets) {
		return addAttribute(Collections.singletonList(superT), value, targets);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setAttribute(T superT, Serializable value, T... targets) {
		return setAttribute(Collections.singletonList(superT), value, targets);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addHolder(T attribute, T superT, Serializable value, T... targets) {
		return attribute.addInstance(superT, value, targetsToComponents(targets));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setHolder(T attribute, T superT, Serializable value, T... targets) {
		return attribute.setInstance(superT, value, targetsToComponents(targets));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addAttribute(List<T> overrides, Serializable value, T... targets) {
		return getRoot().addInstance(overrides, value, targetsToComponents(targets));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setAttribute(List<T> overrides, Serializable value, T... targets) {
		return getRoot().setInstance(overrides, value, targetsToComponents(targets));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addHolder(T attribute, List<T> overrides, Serializable value, T... targets) {
		return attribute.addInstance(overrides, value, targetsToComponents(targets));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setHolder(T attribute, List<T> overrides, Serializable value, T... targets) {
		return attribute.setInstance(overrides, value, targetsToComponents(targets));
	}

}
