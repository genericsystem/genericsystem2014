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

	@SuppressWarnings("unchecked")
	@Override
	default void setSystemPropertyValue(Class<T> propertyClass, int pos, Serializable value) {
		getSystemMap().getMeta().setInstance(getMap(), new AxedPropertyClass(propertyClass, pos), getRoot()).setInstance(value, (T) this);
	}

	default T getMap() {
		return getRoot().find(SystemMap.class);
	}

	default Stream<T> getKeys() {
		return getAttributes(getMap()).stream();
	}

	default Optional<T> getKey(AxedPropertyClass property) {
		return getKeys().filter(x -> x.getValue().equals(property)).findFirst();
	}

	// TODO clean
	// @SystemGeneric
	// @Components(Root.class)
	// @org.genericsystem.kernel.annotations.constraints.PropertyConstraint
	public static class SystemMap {

	}
}
