package org.genericsystem.kernel;

import java.io.Serializable;

public class AbstractVertex<T> {
	private final T meta;
	private final T[] supers;
	private final T[] components;
	private final Serializable value;

	@SuppressWarnings("unchecked")
	public AbstractVertex(T meta, T[] supers, Serializable value, T... components) {
		this.meta = meta == null ? (T) this : meta;
		this.supers = supers;
		this.value = value;
		this.components = components.clone();
		for (int i = 0; i < components.length; i++)
			if (components[i] == null)
				this.components[i] = (T) this;
	}

	public T getMeta() {
		return meta;
	}

	public T[] getSupers() {
		return supers;
	}

	public T[] getComponents() {
		return components;
	}

	public Serializable getValue() {
		return value;
	}
}
