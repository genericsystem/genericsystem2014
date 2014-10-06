package org.genericsystem.impl;

import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.kernel.IRoot;

public interface IEngine<T extends AbstractGeneric<T, U, ?, ?>, U extends IEngine<T, U>> extends IRoot<T, U>, IGeneric<T, U> {

	<subT extends T> subT find(Class<subT> clazz);

	default void check(CheckingType checkingType, boolean isFlushTime, T t) throws RollbackException {
		check(t);
		checkConsistency(checkingType, isFlushTime, t);
		t.checkConstraints(checkingType, isFlushTime);
	}

	default void checkConsistency(CheckingType checkingType, boolean isFlushTime, T t) {}

}
