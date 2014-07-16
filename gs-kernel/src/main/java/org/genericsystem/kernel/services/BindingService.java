package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.exceptions.AmbiguousSelectionException;
import org.genericsystem.kernel.exceptions.CrossEnginesAssignementsException;

public interface BindingService<T extends VertexService<T>> extends ApiService<T> {

	@Override
	default void checkSameEngine(List<T> components) {
		if (components.stream().anyMatch(component -> !component.getRoot().equals(getRoot())))
			rollbackAndThrowException(new CrossEnginesAssignementsException());
	}

	@Override
	@SuppressWarnings("unchecked")
	default T adjustMeta(List<T> overrides, Serializable subValue, List<T> subComponents) {
		T result = null;
		for (T directInheriting : getInheritings()) {
			if (directInheriting.equiv(this, subValue, subComponents))
				return (T) this;
			if (!directInheriting.equiv(this, subValue, subComponents) && isSpecializationOf(getMeta()) && componentsDepends(subComponents, directInheriting.getComponents()))
				if (result == null)
					result = directInheriting;
				else
					rollbackAndThrowException(new AmbiguousSelectionException("Ambigous selection : " + result.info() + directInheriting.info()));
		}
		return result == null ? (T) this : result.adjustMeta(overrides, subValue, subComponents);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T getInstance(Serializable value, T... components) {
		T meta = getAlive();
		if (meta == null)
			return null;
		meta = adjustMeta(Collections.emptyList(), value, Arrays.asList(components));
		if (meta != this)
			return meta.getInstance(value, components);
		for (T instance : (Snapshot<T>) (((DependenciesService<?>) meta).getInstances()))
			if (instance.equiv(meta, value, Arrays.asList(components)))
				return instance;
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	default T getWeakInstance(Serializable value, T... components) {
		T meta = getAlive();
		if (meta == null)
			return null;
		meta = adjustMeta(Collections.emptyList(), value, Arrays.asList(components));
		if (meta != this)
			return meta.getInstance(value, components);
		for (T instance : meta.getInstances())
			if (instance.weakEquiv(meta, value, Arrays.asList(components)))
				return instance;
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	default T getInstance(List<T> supers, Serializable value, T... components) {
		T result = getInstance(value, components);
		return result != null && supers.stream().allMatch(superT -> result.inheritsFrom(superT)) ? result : null;
	}
}
