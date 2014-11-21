package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.genericsystem.api.core.IVertex;

public interface DefaultWritable<T extends DefaultVertex<T>> extends IVertex<T> {

	@Override
	default T updateValue(Serializable newValue) {
		return update(getSupers(), newValue, coerceToTArray(getComponents().toArray()));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T updateSupers(T... supers) {
		return update(Arrays.asList(supers), getValue(), coerceToTArray(getComponents().toArray()));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T updateComposites(T... newComposites) {
		return update(getSupers(), getValue(), newComposites);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T update(Serializable newValue, T... newComposites) {
		return update(Collections.emptyList(), newValue, newComposites);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setInstance(Serializable value, T... components) {
		return setInstance(Collections.emptyList(), value, components);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setInstance(T override, Serializable value, T... components) {
		return setInstance(Collections.singletonList(override), value, components);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addInstance(Serializable value, T... components) {
		return addInstance(Collections.emptyList(), value, components);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addInstance(T override, Serializable value, T... components) {
		return addInstance(Collections.singletonList(override), value, components);
	}

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
		return attribute.addInstance(value, addThisToTargets(targets));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setHolder(T attribute, Serializable value, T... targets) {
		return attribute.setInstance(value, addThisToTargets(targets));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addAttribute(T override, Serializable value, T... targets) {
		return addAttribute(Collections.singletonList(override), value, targets);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setAttribute(T override, Serializable value, T... targets) {
		return setAttribute(Collections.singletonList(override), value, targets);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addHolder(T attribute, T override, Serializable value, T... targets) {
		return attribute.addInstance(override, value, addThisToTargets(targets));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setHolder(T attribute, T override, Serializable value, T... targets) {
		return attribute.setInstance(override, value, addThisToTargets(targets));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addAttribute(List<T> overrides, Serializable value, T... targets) {
		return getRoot().addInstance(overrides, value, addThisToTargets(targets));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setAttribute(List<T> overrides, Serializable value, T... targets) {
		return getRoot().setInstance(overrides, value, addThisToTargets(targets));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addHolder(T attribute, List<T> overrides, Serializable value, T... targets) {
		return attribute.addInstance(overrides, value, addThisToTargets(targets));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setHolder(T attribute, List<T> overrides, Serializable value, T... targets) {
		return attribute.setInstance(overrides, value, addThisToTargets(targets));
	}

}
