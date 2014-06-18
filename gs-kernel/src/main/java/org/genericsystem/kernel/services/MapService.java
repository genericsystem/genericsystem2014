package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;

public interface MapService<T extends MapService<T>> extends SystemPropertiesService<T>, CompositesInheritanceService<T>, UpdatableService<T> { // extends CompositesInheritanceService<T>, UpdatableService<T> {

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
		if (map == null) {
			map = getRoot().setInstance(Map.class, getRoot());// .enablePropertyConstraint();
//			log.info(map.info() + " / " + getMap());
			assert map == getMap();// getMap is unable to find map cause of meta attribute creation !!!
		}
		return map;
	}

	default Stream<T> getKeys() {
		T map = getMap();
		return map == null ? Stream.empty() : getAttributes(map).stream();
	}

	default Optional<T> getKey(AxedPropertyClass property) {
		return getKeys().filter(x -> x.getValue().equals(property)).findFirst();
	}

	@SuppressWarnings("unchecked")
	default T setKey(AxedPropertyClass property) {
		Optional<T> key = getKey(property);
		if (key.isPresent())
			return key.get();
		T root = getRoot();
		return root.setInstance(setMap(), (Serializable) property, root);
	}

	public static class Map {

	}
}
