package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;
import org.genericsystem.api.core.IVertex;
import org.genericsystem.api.core.Snapshot;

public interface DefaultVertex<T extends AbstractVertex<T, U>, U extends DefaultRoot<T, U>> extends DefaultAncestors<T, U>, DefaultDependencies<T, U>, DefaultDisplay<T, U>, DefaultSystemProperties<T, U>, DefaultComponentsInheritance<T, U>,
		DefaultWritable<T, U>, IVertex<T, U> {

	@Override
	default T addRoot(Serializable value) {
		return addRoot(value, 1);
	}

	@Override
	default T addRoot(T override, Serializable value) {
		return addRoot(override, value, 1);
	}

	@Override
	default T addRoot(List<T> overrides, Serializable value) {
		return addRoot(overrides, value, 1);
	}

	@Override
	default T addRoot(Serializable value, int parentsNumber) {
		return addInstance(value, coerceToTArray(new Object[parentsNumber]));
	}

	@Override
	default T addRoot(T override, Serializable value, int parentsNumber) {
		return addInstance(override, value, coerceToTArray(new Object[parentsNumber]));
	}

	@Override
	default T addRoot(List<T> overrides, Serializable value, int parentsNumber) {
		return addInstance(overrides, value, coerceToTArray(new Object[parentsNumber]));
	}

	@Override
	default T setRoot(Serializable value) {
		return setRoot(value, 1);
	}

	@Override
	default T setRoot(T override, Serializable value) {
		return setRoot(override, value, 1);
	}

	@Override
	default T setRoot(List<T> overrides, Serializable value) {
		return setRoot(overrides, value, 1);
	}

	@Override
	default T setRoot(Serializable value, int parentsNumber) {
		return setInstance(value, coerceToTArray(new Object[parentsNumber]));
	}

	@Override
	default T setRoot(T override, Serializable value, int parentsNumber) {
		return setInstance(override, value, coerceToTArray(new Object[parentsNumber]));
	}

	@Override
	default T setRoot(List<T> overrides, Serializable value, int parentsNumber) {
		return setInstance(overrides, value, coerceToTArray(new Object[parentsNumber]));
	}

	@Override
	default T addSubNode(Serializable value) {
		return addHolder(getMeta(), value, coerceToTArray());
	}

	@Override
	default T setSubNode(Serializable value) {
		return addHolder(this.getMeta(), value, coerceToTArray());
	}

	// TODO: Not implemented !
	@Override
	default T addInhertingNode(Serializable value) {
		return null;
	}

	// TODO: Not implemented !
	@Override
	default T setInhertingNode(Serializable value) {
		return null;
	}

	@SuppressWarnings("unchecked")
	default Snapshot<T> getSubNodes() {
		return () -> ((T) this).getMetaComponents(this.getMeta()).stream().filter(component -> !equals(component)).iterator();
	}

	@SuppressWarnings("unchecked")
	default Snapshot<T> getAllSubNodes() {
		return () -> Stream.concat(Stream.of((T) this), getSubNodes().stream().flatMap(inheriting -> inheriting.getAllSubNodes().stream())).distinct().iterator();
	}
}