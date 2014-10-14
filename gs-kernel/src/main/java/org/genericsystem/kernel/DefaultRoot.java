package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.genericsystem.api.core.IRoot;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.kernel.systemproperty.constraints.AliveConstraintImpl;
import org.genericsystem.kernel.systemproperty.constraints.Constraint;
import org.genericsystem.kernel.systemproperty.constraints.Constraint.CheckingType;

public interface DefaultRoot<T extends AbstractVertex<T, U>, U extends DefaultRoot<T, U>> extends DefaultVertex<T, U>, IRoot<T> {

	static final List<Class<? extends Constraint>> SYSTEM_CONSTRAINTS = new ArrayList<Class<? extends Constraint>>() {
		private static final long serialVersionUID = -950838421343460439L;

		{
			add(AliveConstraintImpl.class);
		}
	};

	default void discardWithException(Throwable exception) throws RollbackException {
		throw new RollbackException(exception);
	}

	default void check(CheckingType checkingType, boolean isFlushTime, T t) throws RollbackException {
		t.checkSystemConstraints(checkingType, isFlushTime);
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

	default T addTree(Serializable value) {
		return addTree(value, 1);
	}

	default T addTree(T override, Serializable value) {
		return addTree(override, value, 1);
	}

	default T addTree(List<T> overrides, Serializable value) {
		return addTree(overrides, value, 1);
	}

	default T addTree(Serializable value, int parentsCount) {
		return addInstance(value, coerceToTArray(new Object[parentsCount]));
	}

	default T addTree(T override, Serializable value, int parentsCount) {
		return addInstance(override, value, coerceToTArray(new Object[parentsCount]));
	}

	default T addTree(List<T> overrides, Serializable value, int parentsCount) {
		return addInstance(overrides, value, coerceToTArray(new Object[parentsCount]));
	}

	default T setTree(Serializable value) {
		return setTree(value, 1);
	}

	default T setTree(T override, Serializable value) {
		return setTree(override, value, 1);
	}

	default T setTree(List<T> overrides, Serializable value) {
		return setTree(overrides, value, 1);
	}

	@SuppressWarnings("unchecked")
	default T setTree(Serializable value, int parentsCount) {
		return setInstance(value, (T[]) new Object[parentsCount]);
	}

	@SuppressWarnings("unchecked")
	default T setTree(T override, Serializable value, int parentsCount) {
		return setInstance(override, value, (T[]) new Object[parentsCount]);
	}

	@SuppressWarnings("unchecked")
	default T setTree(List<T> overrides, Serializable value, int parentsCount) {
		return setInstance(overrides, value, (T[]) new Object[parentsCount]);
	}

}
