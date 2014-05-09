package org.genericsystem.kernel.services;

import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.exceptions.RollbackException;

public interface ExceptionAdviserService<T extends AncestorsService<T>> extends AncestorsService<T> {

	default void rollbackAndThrowException(Exception exception) throws RollbackException {
		((Root) getRoot()).rollback();
		throw new RollbackException(exception);
	}
}
