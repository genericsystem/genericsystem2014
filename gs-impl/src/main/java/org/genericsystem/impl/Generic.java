package org.genericsystem.impl;

import java.io.Serializable;
import java.util.stream.Stream;

public class Generic<T extends Generic<T>> extends AbstractGeneric<T> implements GenericService<T> {

	@SuppressWarnings("unchecked")
	@Override
	public T build(T meta, Stream<T> overrides, Serializable value, Stream<T> components) {
		return (T) new GenericImpl().init((GenericImpl) meta, overrides.toArray(GenericImpl[]::new), value, components.toArray(GenericImpl[]::new));
	}

	private final static Generic[] EMPTY_ARRAY = new Generic[] {};

	@Override
	public T[] getEmptyArray() {
		return (T[]) EMPTY_ARRAY;
	}

	public static class GenericImpl extends Generic<GenericImpl> {

	}

}
