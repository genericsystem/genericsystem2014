package org.genericsystem.kernel;

import org.genericsystem.api.core.IContext;
import org.genericsystem.api.core.Snapshot;

public interface DefaultContext<T extends AbstractVertex<T>> extends IContext<T> {

	DefaultRoot<T> getRoot();

	boolean isAlive(T vertex);

	Snapshot<T> getInheritings(T vertex);

	Snapshot<T> getInstances(T vertex);

	Snapshot<T> getComposites(T vertex);

	T plug(T vertex);

	boolean unplug(T vertex);

}
