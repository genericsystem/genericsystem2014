package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.List;

import org.genericsystem.api.defaults.DefaultVertex;

public class GenericBuilder<T extends DefaultVertex<T>> {
	private final Class<?> clazz;
	private final T meta;
	private final List<T> overrides;
	private final Serializable value;
	private final List<T> components;

	public GenericBuilder(Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
		this.clazz = clazz;
		this.meta = meta;
		this.overrides = overrides;
		this.value = value;
		this.components = components;
	}
}
