package org.genericsystem.kernel;

import org.genericsystem.api.core.Snapshot;

public interface DefaultContext<T extends AbstractVertex<T>> {

	DefaultRoot<T> getRoot();

	boolean isAlive(T vertex);

	Snapshot<T> getInheritings(T vertex);

	Snapshot<T> getInstances(T vertex);

	Snapshot<T> getComposites(T vertex);

}
