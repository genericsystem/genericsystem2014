package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.genericsystem.api.core.IVertexBase;

public interface IWritable<T extends AbstractVertex<T, U>, U extends IRoot<T, U>> extends IVertexBase<T, U> {

	// TODO clean
	// @Override
	// boolean remove();

	@Override
	default T updateValue(Serializable newValue) {
		return update(getSupers(), newValue, coerceToTArray(getComposites().toArray()));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T updateSupers(T... supersToAdd) {
		return update(Arrays.asList(supersToAdd), getValue(), coerceToTArray(getComposites().toArray()));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T updateComponents(T... newComponents) {
		return update(getSupers(), getValue(), newComponents);
	}

	@SuppressWarnings("unchecked")
	@Override
	default T update(List<T> supersToAdd, Serializable newValue, T... newComponents) {
		return ((T) this).update(supersToAdd, newValue, Arrays.asList(newComponents));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T update(Serializable newValue, T... newComponents) {
		return update(Collections.emptyList(), newValue, newComponents);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setInstance(Serializable value, T... composites) {
		return setInstance(Collections.emptyList(), value, composites);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setInstance(T override, Serializable value, T... composites) {
		return setInstance(Collections.singletonList(override), value, composites);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addInstance(Serializable value, T... composites) {
		return addInstance(Collections.emptyList(), value, composites);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T addInstance(T override, Serializable value, T... composites) {
		return addInstance(Collections.singletonList(override), value, composites);
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
		return attribute.addInstance(attribute, value, addThisToTargets(targets));
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setHolder(T attribute, Serializable value, T... targets) {
		return attribute.setInstance(attribute, value, addThisToTargets(targets));
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
