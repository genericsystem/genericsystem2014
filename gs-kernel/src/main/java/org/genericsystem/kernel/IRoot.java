package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.kernel.systemproperty.constraints.Constraint.CheckingType;

public interface IRoot<T extends AbstractVertex<T, U>, U extends IRoot<T, U>> extends IVertex<T, U> {

	default void discardWithException(Throwable exception) throws RollbackException {
		throw new RollbackException(exception);
	}

	default void check(CheckingType checkingType, boolean isFlushTime, T t) throws RollbackException {
		// old check
		t.checkDependsMetaComponents();
		t.checkSupers();
		t.checkDependsSuperComposites();

		t.checkConsistency(checkingType, isFlushTime);
		t.checkConstraints(checkingType, isFlushTime);
	}

	@SuppressWarnings("unchecked")
	default T getMetaAttribute() {
		return ((T) this).getDirectInstance(getValue(), Collections.singletonList((T) this));
	}

	//
	// These signatures force Engine to re-implement methods
	//

	@Override
	boolean isRoot();

	@Override
	public U getRoot();

	@Override
	public T getAlive();

	default T addType(Serializable value) {
		return addInstance(value, coerceToTArray());
	}

	default T addType(T override, Serializable value) {
		return addInstance(override, value, coerceToTArray());
	}

	default T addType(List<T> overrides, Serializable value) {
		return addInstance(overrides, value, coerceToTArray());
	}

	default T setType(Serializable value) {
		return setInstance(value, coerceToTArray());
	}

	default T setType(T override, Serializable value) {
		return setInstance(override, value, coerceToTArray());
	}

	default T setType(List<T> overrides, Serializable value) {
		return setInstance(overrides, value, coerceToTArray());
	}
}
