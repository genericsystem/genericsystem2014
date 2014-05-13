package org.genericsystem.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class Generic<T extends Generic<T>> implements GenericService<T> {

	private final T meta;
	private final T[] supers;
	private final T[] components;
	private final Serializable value;

	public Generic(T meta, T[] supers, Serializable value, T... components) {
		this.meta = meta == null ? (T) this : meta;
		this.supers = supers;
		this.value = value;
		this.components = components;
	}

	@Override
	public T build(T meta, Stream<T> overrides, Serializable value, Stream<T> components) {
		return (T) new GenericImpl((GenericImpl) meta, overrides.toArray(GenericImpl[]::new), value, components.toArray(GenericImpl[]::new));
	}

	@Override
	public T getMeta() {
		return meta;
	}

	@Override
	public Stream<T> getSupersStream() {
		return Arrays.stream(supers);
	}

	@Override
	public Stream<T> getComponentsStream() {
		return Arrays.stream(components);
	}

	@Override
	public T[] getComponents() {
		return components;
	}

	@Override
	public Serializable getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Generic))
			return false;
		Generic service = (Generic) obj;
		return equiv(service);
	}

	@Override
	public int hashCode() {
		// TODO introduce : meta and components length
		return Objects.hashCode(getValue());
	}

	@Override
	public String toString() {
		return Objects.toString(getValue());
	}

	private final static Generic[] EMPTY_ARRAY = new Generic[] {};

	@Override
	public T[] getEmptyArray() {
		return (T[]) EMPTY_ARRAY;
	}

	public static class GenericImpl extends Generic<GenericImpl> {

		public GenericImpl(GenericImpl meta, GenericImpl[] supers, Serializable value, GenericImpl[] components) {
			super(meta, supers, value, components);
		}

	}

}
