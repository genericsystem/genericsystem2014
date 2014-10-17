package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.genericsystem.api.core.IRoot;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.kernel.systemproperty.constraints.AliveConstraint;
import org.genericsystem.kernel.systemproperty.constraints.Constraint;
import org.genericsystem.kernel.systemproperty.constraints.Constraint.CheckingType;

public interface DefaultRoot<T extends AbstractVertex<T, U>, U extends DefaultRoot<T, U>> extends DefaultVertex<T, U>, IRoot<T> {

	static final List<Class<? extends Constraint>> SYSTEM_CONSTRAINTS = new ArrayList<Class<? extends Constraint>>() {
		private static final long serialVersionUID = -950838421343460439L;

		{
			add(AliveConstraint.class);
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
