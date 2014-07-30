package org.genericsystem.kernel.services;

import org.genericsystem.kernel.exceptions.RollbackException;

public interface RootService<T extends VertexService<T, U>, U extends RootService<T, U>> extends VertexService<T, U> {

	@Override
	default int getLevel() {
		return 0;
	}

	@Override
	default boolean isRoot() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	default U getRoot() {
		return (U) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	default T getAlive() {
		return (T) this;
	}

	default void discardWithException(Throwable exception) throws RollbackException {
		throw new RollbackException(exception);
	}

}
