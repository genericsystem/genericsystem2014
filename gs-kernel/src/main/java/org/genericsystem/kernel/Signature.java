package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class Signature<T> {
	protected T meta;
	protected List<T> components;
	protected Serializable value;

	@SuppressWarnings("unchecked")
	protected T init(T meta, Serializable value, List<T> components) {
		this.meta = meta == null ? (T) this : meta;
		this.value = value;
		this.components = new ArrayList<>(components);
		for (int i = 0; i < components.size(); i++)
			if (components.get(i) == null)
				this.components.set(i, (T) this);
		return (T) this;
	}

	public T getMeta() {
		return meta;
	}

	public List<T> getComponents() {
		return components;
	}

	public Serializable getValue() {
		return value;
	}

	public Stream<T> getComponentsStream() {
		return components.stream();
	}

	@Override
	public String toString() {
		return Objects.toString(getValue());
	}
}
