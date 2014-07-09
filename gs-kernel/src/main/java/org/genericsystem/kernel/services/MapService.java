package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;

import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.annotations.Components;
import org.genericsystem.kernel.annotations.SystemGeneric;

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
		T root = getRoot();
		getMetaAttribute().setInstance(getMap(), new AxedPropertyClass(propertyClass, pos), root).setInstance(value, (T) this);
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

	@SystemGeneric
	@Components(Root.class)
	@org.genericsystem.kernel.annotations.constraints.PropertyConstraint
	public static class SystemMap {

	}
}
