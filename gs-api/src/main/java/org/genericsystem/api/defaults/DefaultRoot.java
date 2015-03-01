package org.genericsystem.api.defaults;

import org.genericsystem.api.core.IContext;
import org.genericsystem.api.core.IRoot;

public interface DefaultRoot<T extends DefaultVertex<T>> extends IRoot<T> {

	Class<?> findAnnotedClass(T vertex);

	boolean isInitialized();

	long pickNewTs();

	IContext<T> buildTransaction();

	@Override
	DefaultContext<T> getCurrentCache();

}
