package org.genericsystem.api.defaults;

import org.genericsystem.api.core.IContext;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.RollbackException;

public interface DefaultContext<T extends DefaultVertex<T>> extends IContext<T> {

	DefaultRoot<T> getRoot();

	boolean isAlive(T vertex);

	Snapshot<T> getInheritings(T vertex);

	Snapshot<T> getInstances(T vertex);

	Snapshot<T> getComposites(T vertex);

	default void discardWithException(Throwable exception) throws RollbackException {
		throw new RollbackException(exception);
	}

	DefaultBuilder<T> getBuilder();

	DefaultChecker<T> getChecker();

	void forceRemove(T generic);

	void remove(T generic);
}
