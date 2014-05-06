package org.genericsystem.kernel.services;

import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.exceptions.RollbackException;

public interface ExceptionAdviserService extends AncestorsService<Vertex> {

	default void rollbackAndThrowException(Exception exception) throws RollbackException {
		this.<Root> getRoot().rollback();
		throw new RollbackException(exception);
	}
}
