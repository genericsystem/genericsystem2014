package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;

public interface MapService<T extends MapService<T>> extends SystemPropertiesService<T>, CompositesInheritanceService<T>, UpdatableService<T> {

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

	@Override
	default void setSystemPropertyValue(Class<T> propertyClass, int pos, Serializable value) {
		getMap().getMeta().setInstance(getMap(), new AxedPropertyClass(propertyClass, pos), coerceToArray(getRoot())).setInstance(value, coerceToArray(this));
	}

	default T getMap() {
		return getRoot().find(SystemMap.class);
	}

	default Stream<T> getKeys() {
		T map = getMap();
		if (map == null)
			return Stream.empty();
		return getAttributes(map).stream();
	}

	default Optional<T> getKey(AxedPropertyClass property) {
		return getKeys().filter(x -> x.getValue().equals(property)).findFirst();
	}

	public static class SystemMap {}
}
