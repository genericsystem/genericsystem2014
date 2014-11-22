package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.List;
import org.genericsystem.api.core.IRoot;
import org.genericsystem.api.exception.RollbackException;

public interface DefaultRoot<T extends AbstractVertex<T>> extends IRoot<T> {

	Context<T> getCurrentCache();

	@Override
	default void discardWithException(Throwable exception) throws RollbackException {
		throw new RollbackException(exception);
	}

	default void check(boolean isOnAdd, boolean isFlushTime, T t) throws RollbackException {
		t.checkSystemConstraints(isOnAdd, isFlushTime);
		t.checkConsistency();
		t.checkConstraints(isOnAdd, isFlushTime);
	}

	@Override
	default T getMetaAttribute() {
		return ((T) this).getMeta(Statics.ATTRIBUTE_SIZE);
	}

	@Override
	default T getMetaRelation() {
		return ((T) this).getMeta(Statics.RELATION_SIZE);
	}

	@Override
	default T addType(Serializable value) {
		return addInstance(value, coerceToTArray());
	}

	@Override
	default T addType(T override, Serializable value) {
		return addInstance(override, value, coerceToTArray());
	}

	@Override
	default T addType(List<T> overrides, Serializable value) {
		return addInstance(overrides, value, coerceToTArray());
	}

	@Override
	default T setType(Serializable value) {
		return setInstance(value, coerceToTArray());
	}

	@Override
	default T setType(T override, Serializable value) {
		return setInstance(override, value, coerceToTArray());
	}

	@Override
	default T setType(List<T> overrides, Serializable value) {
		return setInstance(overrides, value, coerceToTArray());
	}

	@Override
	default T addTree(Serializable value) {
		return addTree(value, 1);
	}

	@Override
	default T addTree(Serializable value, int parentsCount) {
		return addInstance(value, coerceToTArray(new Object[parentsCount]));
	}

	@Override
	default T setTree(Serializable value) {
		return setTree(value, 1);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setTree(Serializable value, int parentsCount) {
		return setInstance(value, (T[]) new Object[parentsCount]);
	}

}
