package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.genericsystem.kernel.ISystemProperties.AxedPropertyClass;
import org.genericsystem.kernel.services.IGeneric;

public interface IMap<T extends IVertex<T, U>, U extends IRoot<T, U>> extends IGeneric<T, U> {

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
		getMap().getMeta().setInstance(getMap(), new AxedPropertyClass(propertyClass, pos), (T[]) ((AbstractVertex<?, ?>) this).coerceToArray(getRoot())).setInstance(value, (T[]) ((AbstractVertex<?, ?>) this).coerceToArray(this));
	}

	default T getMap() {
		T metaAttribute = getMetaAttribute();
		for (T instance : metaAttribute.getInstances())
			if (instance.equals(metaAttribute, Collections.emptyList(), SystemMap.class, Collections.singletonList(getRoot())))
				return instance;
		return null;

	}

	// TODO remove
	default boolean equals(IGeneric<?, ?> meta, List<? extends IGeneric<?, ?>> supers, Serializable value, List<? extends IGeneric<?, ?>> components) {
		return (isRoot() || getMeta().equals(meta)) && Objects.equals(getValue(), value) && getComponents().equals(components) && getSupers().equals(supers);
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
