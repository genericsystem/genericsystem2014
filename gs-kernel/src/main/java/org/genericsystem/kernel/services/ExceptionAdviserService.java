package org.genericsystem.kernel.services;

import org.genericsystem.kernel.exceptions.RollbackException;

public interface ExceptionAdviserService<T extends ExceptionAdviserService<T>> extends AncestorsService<T> {

	void rollbackAndThrowException(Exception exception) throws RollbackException;
}
