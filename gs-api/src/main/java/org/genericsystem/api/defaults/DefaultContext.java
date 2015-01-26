package org.genericsystem.api.defaults;

import java.io.Serializable;
import java.util.List;

import org.genericsystem.api.core.IContext;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.RollbackException;

public interface DefaultContext<T extends DefaultVertex<T>> extends IContext<T> {

	DefaultRoot<T> getRoot();

	boolean isAlive(T vertex);

	T getInstance(T meta, List<T> overrides, Serializable value, T... components);

	Snapshot<T> getInheritings(T vertex);

	Snapshot<T> getInstances(T vertex);

	Snapshot<T> getComposites(T vertex);

	default void discardWithException(Throwable exception) throws RollbackException {
		throw new RollbackException(exception);
	}

	DefaultBuilder<T> getBuilder();

	DefaultChecker<T> getChecker();

}
