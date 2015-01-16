package org.genericsystem.api.defaults;

import java.io.Serializable;
import java.util.List;

public interface DefaultBuilder<T extends DefaultVertex<T>> {

	DefaultContext<T> getContext();

	T[] newTArray(int i);

	T addInstance(Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components);

	T update(T update, List<T> overrides, Serializable newValue, List<T> newComponents);

	T setInstance(Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components);

	void forceRemove(T generic);

	void remove(T generic);

	void conserveRemove(T generic);
}
