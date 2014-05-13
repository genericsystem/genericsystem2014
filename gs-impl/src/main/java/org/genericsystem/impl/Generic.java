package org.genericsystem.impl;

import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Stream;

import org.genericsystem.kernel.AbstractVertex;

public class Generic<T extends Generic<T>> extends AbstractVertex<T> implements GenericService<T> {

	public Generic(T meta, T[] supers, Serializable value, T... components) {
		super(meta, supers, value, components);
	}

	@Override
	public T build(T meta, Stream<T> overrides, Serializable value, Stream<T> components) {
		return (T) new GenericImpl((GenericImpl) meta, overrides.toArray(GenericImpl[]::new), value, components.toArray(GenericImpl[]::new));
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
