package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.exceptions.AmbiguousSelectionException;
import org.genericsystem.kernel.exceptions.CrossEnginesAssignementsException;

public interface BindingService<T extends BindingService<T>> extends DependenciesService<T>, FactoryService<T>, ExceptionAdviserService<T>, DisplayService<T> {

	default void checkSameEngine(List<T> components) {
		if (components.stream().anyMatch(component -> !component.getRoot().equals(getRoot())))
			rollbackAndThrowException(new CrossEnginesAssignementsException());
	}

	@SuppressWarnings("unchecked")
	default T adjustMeta(List<T> overrides, Serializable subValue, List<T> subComponents) {
		T result = null;
		for (T directInheriting : getInheritings())
			if (directInheriting.isMetaOf((T) this, subValue, overrides, subComponents))
				if (result == null)
					result = directInheriting;
				else
					rollbackAndThrowException(new AmbiguousSelectionException("Ambigous selection : " + result.info() + directInheriting.info()));
		return result == null ? (T) this : result.adjustMeta(overrides, subValue, subComponents);
	}

	@SuppressWarnings("unchecked")
	default T getInstance(Serializable value, T... components) {
		T nearestMeta = adjustMeta(Collections.emptyList(), value, Arrays.asList(components));
		if (nearestMeta != this)
			return nearestMeta.getInstance(value, components);
		T pluggedMeta = getAlive();
		if (pluggedMeta == null)
			return null;
		for (T instance : (Snapshot<T>) (((DependenciesService<?>) pluggedMeta).getInstances()))
			if (instance.equiv(pluggedMeta, value, Arrays.asList(components)))
				return instance;
		return null;
	}

	@SuppressWarnings("unchecked")
	default T getWeakInstance(Serializable value, T... components) {
		T nearestMeta = adjustMeta(Collections.emptyList(), value, Arrays.asList(components));
		if (nearestMeta != this)
			return nearestMeta.getInstance(value, components);
		T alive = getAlive();
		if (alive == null)
			return null;
		for (T instance : (Snapshot<T>) (((DependenciesService<?>) alive).getInstances()))
			if (instance.weakEquiv(alive, value, Arrays.asList(components)))
				return instance;
		return null;
	}

	@SuppressWarnings("unchecked")
	default T getInstance(List<T> supers, Serializable value, T... components) {
		T result = getInstance(value, components);
		if (result != null && supers.stream().allMatch(superT -> result.inheritsFrom(superT)))
			return result;
		return null;
	}

	Snapshot<T> getMetaComposites(T meta);

	Snapshot<T> getSuperComposites(T superVertex);

	T plug();

	boolean unplug();
	//
	// @SuppressWarnings("unchecked")
	// default void removeInstance(Serializable value, T... components) {
	// T t = getInstance(value, components);
	// if (t == null)
	// rollbackAndThrowException(new NotFoundException(((DisplayService<T>) this).info()));
	// ((BindingService<T>) t).unplug();
	// }

}
