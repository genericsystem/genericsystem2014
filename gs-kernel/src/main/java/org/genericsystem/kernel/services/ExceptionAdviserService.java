package org.genericsystem.kernel.services;

import org.genericsystem.kernel.exceptions.RollbackException;

public interface ExceptionAdviserService<T extends AncestorsService<T>> extends AncestorsService<T> {

	default void rollbackAndThrowException(Exception exception) throws RollbackException {
		((ExceptionAdviserService<T>) getRoot().getAlive()).rollback();
		throw new RollbackException(exception);
	}

	// TODO KK
	void rollback();
}
