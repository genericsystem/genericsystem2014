package org.genericsystem.kernel;

import org.genericsystem.kernel.exceptions.RollbackException;

public interface Cache {

	default void rollbackAndThrowException(Exception exception) throws RollbackException {
		rollback();
		throw new RollbackException(exception);
	}

	void rollback();
}
