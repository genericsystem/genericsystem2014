package org.genericsystem.api.defaults;

import java.io.Serializable;
import java.util.List;
import org.genericsystem.api.core.IContext;
import org.genericsystem.api.core.IRoot;

public interface DefaultRoot<T extends DefaultVertex<T>> extends IRoot<T> {

	Class<?> findAnnotedClass(T vertex);

	boolean isInitialized();

	long pickNewTs();

	IContext<T> buildTransaction();

	@Override
	DefaultContext<T> getCurrentCache();

	long getTs(T generic);

	T getMeta(T generic);

	DefaultLifeManager getLifeManager(T generic);

	List<T> getComponents(T generic);

	Serializable getValue(T generic);

	List<T> getSupers(T generic);

}
