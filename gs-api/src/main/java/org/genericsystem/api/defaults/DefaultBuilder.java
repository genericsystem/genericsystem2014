package org.genericsystem.api.defaults;

import java.io.Serializable;
import java.util.List;

public interface DefaultBuilder<T extends DefaultVertex<T>> {

	T[] newTArray(int i);

	public T addInstance(Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components);

	public T update(T update, List<T> overrides, Serializable newValue, List<T> newComponents);

	T setInstance(Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components);

}
