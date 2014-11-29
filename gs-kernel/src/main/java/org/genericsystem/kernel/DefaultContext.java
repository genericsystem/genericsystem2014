package org.genericsystem.kernel;

import org.genericsystem.api.core.IContext;

public interface DefaultContext<T extends AbstractVertex<T>> extends IContext<T> {

	DefaultRoot<T> getRoot();


}
