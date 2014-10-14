package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.List;

import org.genericsystem.api.core.IVertex;

public interface DefaultVertex<T extends AbstractVertex<T, U>, U extends DefaultRoot<T, U>> extends DefaultAncestors<T, U>, DefaultDependencies<T, U>, DefaultDisplay<T, U>, DefaultSystemProperties<T, U>, DefaultComponentsInheritance<T, U>,
		DefaultWritable<T, U>, IVertex<T, U> {

	default T addNode(Serializable value) {
		return addNode(value, 1);
	}

	default T addNode(T override, Serializable value) {
		return addNode(override, value, 1);
	}

	default T addNode(List<T> overrides, Serializable value) {
		return addNode(overrides, value, 1);
	}

	default T addNode(Serializable value, int parentsNumber) {
		return addInstance(value, coerceToTArray(new Object[parentsNumber]));
	}

	default T addNode(T override, Serializable value, int parentsNumber) {
		return addInstance(override, value, coerceToTArray(new Object[parentsNumber]));
	}

	default T addNode(List<T> overrides, Serializable value, int parentsNumber) {
		return addInstance(overrides, value, coerceToTArray(new Object[parentsNumber]));
	}

	default T setNode(Serializable value) {
		return setNode(value, 1);
	}

	default T setNode(T override, Serializable value) {
		return setNode(override, value, 1);
	}

	default T setNode(List<T> overrides, Serializable value) {
		return setNode(overrides, value, 1);
	}

	default T setNode(Serializable value, int parentsNumber) {
		return setInstance(value, coerceToTArray(new Object[parentsNumber]));
	}

	default T setNode(T override, Serializable value, int parentsNumber) {
		return setInstance(override, value, coerceToTArray(new Object[parentsNumber]));
	}

	default T setNode(List<T> overrides, Serializable value, int parentsNumber) {
		return setInstance(overrides, value, coerceToTArray(new Object[parentsNumber]));
	}

	default T addRoot(Serializable value) {
		return addNode(value);
	}

	default T setRoot(Serializable value) {
		return setNode(value);
	}

	// TODO: Not implemented !
	default T addInhertingNode(Serializable value) {
		return null;
	}

	// TODO: Not implemented !
	default T setInhertingNode(Serializable value) {
		return null;
	}
}