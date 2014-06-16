package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;

public interface MapService<T extends MapService<T>> extends CompositesInheritanceService<T> {

	default T getMapType() {
		// TODO
		return null;
	}

	T setInstance(Serializable value, T... components);

	default Stream<T> getKeys() {
		// return (Stream<T>) ((CompositesInheritanceService) getMapType()).getInheritings();
		return ((CompositesInheritanceService) this).getAttributes(getMapType()).stream();
	}

	default Optional<T> getKeyType(AxedPropertyClass property) {
		return getKeys().filter(property::equals).findFirst();
	}

	default T setKeyType(AxedPropertyClass property) {

		Optional<T> keyType = getKeyType(property);
		if (keyType.isPresent())
			return keyType.get();
		// TODO check if all is ok for create an attribute for this key class
		// TODO bind the new key
		return null;
	}

	@Override
	default Serializable getSystemPropertyValue(Class<?> propertyClass, int pos) {
		Optional<T> keyType = getKeyType(new AxedPropertyClass(propertyClass, pos));
		if (keyType.isPresent()) {
			Optional<Serializable> result = getValues(keyType.get()).stream().findFirst();
			if (result.isPresent())
				return result.get();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	default void setSystemPropertyValue(Class<T> propertyClass, int pos, Serializable value) {
		T ketType = setKeyType(new AxedPropertyClass(propertyClass, pos));
		ketType.setInstance(value, (T) this);
	}
}
