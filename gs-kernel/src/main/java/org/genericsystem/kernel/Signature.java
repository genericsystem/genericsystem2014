package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class Signature<T> {
	protected T meta;
	protected T[] components;
	protected Serializable value;

	@SuppressWarnings("unchecked")
	protected T init(T meta, Serializable value, T... components) {
		this.meta = meta == null ? (T) this : meta;
		this.value = value;
		this.components = components.clone();
		for (int i = 0; i < components.length; i++)
			if (components[i] == null)
				this.components[i] = (T) this;
		return (T) this;
	}

	public T getMeta() {
		return meta;
	}

	public T[] getComponents() {
		return components;
	}

	public Serializable getValue() {
		return value;
	}

	public Stream<T> getComponentsStream() {
		return Arrays.stream(components);
	}

	@Override
	public String toString() {
		return Objects.toString(getValue());
	}
}
