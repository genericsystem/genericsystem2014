package org.genericsystem.api.defaults;

import org.genericsystem.api.core.IRoot;

public interface DefaultRoot<T extends DefaultVertex<T>> extends IRoot<T> {

	@Override
	DefaultContext<T> getCurrentCache();

}
