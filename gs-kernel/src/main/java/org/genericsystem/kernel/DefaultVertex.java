package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.List;

import org.genericsystem.api.core.IVertex;

public interface DefaultVertex<T extends AbstractVertex<T, U>, U extends DefaultRoot<T, U>> extends DefaultAncestors<T, U>, DefaultDependencies<T, U>, DefaultDisplay<T, U>, DefaultSystemProperties<T, U>, DefaultComponentsInheritance<T, U>,
		DefaultWritable<T, U>, IVertex<T, U> {

	default T addRoot(Serializable value) {
		return addRoot(value, 1);
	}

	default T addRoot(T override, Serializable value) {
		return addRoot(override, value, 1);
	}

	default T addRoot(List<T> overrides, Serializable value) {
		return addRoot(overrides, value, 1);
	}

	default T addRoot(Serializable value, int parentsNumber) {
		return addInstance(value, coerceToTArray(new Object[parentsNumber]));
	}

	default T addRoot(T override, Serializable value, int parentsNumber) {
		return addInstance(override, value, coerceToTArray(new Object[parentsNumber]));
	}

	default T addRoot(List<T> overrides, Serializable value, int parentsNumber) {
		return addInstance(overrides, value, coerceToTArray(new Object[parentsNumber]));
	}

	default T setRoot(Serializable value) {
		return setRoot(value, 1);
	}

	default T setRoot(T override, Serializable value) {
		return setRoot(override, value, 1);
	}

	default T setRoot(List<T> overrides, Serializable value) {
		return setRoot(overrides, value, 1);
	}

	default T setRoot(Serializable value, int parentsNumber) {
		return setInstance(value, coerceToTArray(new Object[parentsNumber]));
	}

	default T setRoot(T override, Serializable value, int parentsNumber) {
		return setInstance(override, value, coerceToTArray(new Object[parentsNumber]));
	}

	default T setRoot(List<T> overrides, Serializable value, int parentsNumber) {
		return setInstance(overrides, value, coerceToTArray(new Object[parentsNumber]));
	}

	default T addSubNode(Serializable value) {

		return addHolder(getMeta(), value, coerceToTArray());
	}

	default T setSubNode(Serializable value) {
		return addHolder(this.getMeta(), value, coerceToTArray());
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