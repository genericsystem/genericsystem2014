package org.genericsystem.kernel;

import org.genericsystem.kernel.exceptions.RollbackException;
import org.genericsystem.kernel.services.IVertexBase;

public interface IRoot<T extends IVertex<T, U>, U extends IRoot<T, U>> extends IVertexBase<T, U> {

	default void discardWithException(Throwable exception) throws RollbackException {
		throw new RollbackException(exception);
	}

}
