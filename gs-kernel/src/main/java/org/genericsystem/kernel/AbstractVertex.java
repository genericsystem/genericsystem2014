package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class AbstractVertex<T> {
	protected T meta;
	protected T[] supers;
	protected T[] components;
	protected Serializable value;

	@SuppressWarnings("unchecked")
	public AbstractVertex(T meta, T[] supers, Serializable value, T... components) {
		// if (isRoot())
		// ((Root) this).valueCache = new ValueCache();
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

	public Stream<T> getSupersStream() {
		return Arrays.stream(supers);
	}

	public Stream<T> getComponentsStream() {
		return Arrays.stream(components);
	}

	@Override
	public String toString() {
		return Objects.toString(getValue());
	}
}
