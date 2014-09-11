package org.genericsystem.kernel;

import org.genericsystem.kernel.exceptions.RollbackException;

public interface IRoot<T extends AbstractVertex<T, U>, U extends IRoot<T, U>> extends IVertex<T, U> {

	default void discardWithException(Throwable exception) throws RollbackException {
		throw new RollbackException(exception);
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

}
