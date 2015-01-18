package org.genericsystem.api.defaults;

import java.io.Serializable;

import org.genericsystem.api.core.IContext;
import org.genericsystem.api.core.IRoot;

public interface DefaultRoot<T extends DefaultVertex<T>> extends IRoot<T> {

	Class<?> findAnnotedClass(T vertex);

	@Override
	default T addTree(Serializable value) {
		return addTree(value, 1);
	}

	@Override
	default T addTree(Serializable value, int parentsCount) {
		return addInstance(value, coerceToTArray(new Object[parentsCount]));
	}

	@Override
	default T setTree(Serializable value) {
		return setTree(value, 1);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setTree(Serializable value, int parentsCount) {
		return setInstance(value, (T[]) new Object[parentsCount]);
	}

	long pickNewTs();

	IContext<T> buildTransaction();

}
