package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;

public interface MapService<T extends MapService<T>> extends CompositesInheritanceService<T>, UpdatableService<T> {

	// T setInstance(Serializable value, @SuppressWarnings("unchecked") T... components);

	@Override
	default Serializable getSystemPropertyValue(Class<?> propertyClass, int pos) {
		Optional<T> key = getKey(new AxedPropertyClass(propertyClass, pos));
		if (key.isPresent()) {
			Optional<Serializable> result = getValues(key.get()).stream().findFirst();
			if (result.isPresent())
				return result.get();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	default void setSystemPropertyValue(Class<T> propertyClass, int pos, Serializable value) {
		setKey(new AxedPropertyClass(propertyClass, pos)).setInstance(value, (T) this);
	}

	@SuppressWarnings("unchecked")
	default T getMap() {
		return getRoot().getInstance(Map.class, getRoot());
	}

	@SuppressWarnings("unchecked")
	default T setMap() {
		T map = getMap();
		return map != null ? map : getRoot().setInstance(Map.class, getRoot()).enablePropertyConstraint();
	}

	default Stream<T> getKeys() {
		T map = getMap();
		return map == null ? Stream.empty() : getAttributes(map).stream();
	}

	default Stream<T> setKeys() {
		return getAttributes(setMap()).stream();
	}

	default Optional<T> getKey(AxedPropertyClass property) {
		return getKeys().filter(x -> x.getValue().equals(property)).findFirst();
	}

	@SuppressWarnings("unchecked")
	default T setKey(AxedPropertyClass property) {
		Optional<T> key = setKeys().filter(x -> x.getValue().equals(property)).findFirst();
		if (key.isPresent())
			return key.get();
		// TODO check if all is ok for create an attribute for this key class
		T root = getRoot();
		return root.setInstance(getMap(), (Serializable) property, root);
	}

	public static class Map {

	}
}
