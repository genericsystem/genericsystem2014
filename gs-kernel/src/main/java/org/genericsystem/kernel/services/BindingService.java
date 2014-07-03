package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.exceptions.AmbiguousSelectionException;
import org.genericsystem.kernel.exceptions.CrossEnginesAssignementsException;
import org.genericsystem.kernel.exceptions.NotFoundException;

public interface BindingService<T extends BindingService<T>> extends DependenciesService<T>, FactoryService<T>, ExceptionAdviserService<T>, DisplayService<T> {

	default void checkSameEngine(List<T> components) {
		if (components.stream().anyMatch(component -> !component.getRoot().equals(getRoot())))
			rollbackAndThrowException(new CrossEnginesAssignementsException());
	}

	@SuppressWarnings("unchecked")
	default T adjustMeta(List<T> overrides, Serializable subValue, List<T> subComponents) {
		T result = null;
		for (T directInheriting : getInheritings())
			if (directInheriting.isMetaOf((T) this, overrides, subComponents))
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

	@Override
	Dependencies<T> getInstances();

	@Override
	Dependencies<T> getInheritings();

	Snapshot<T> getCompositesByMeta(T meta);

	Snapshot<T> getCompositesBySuper(T superVertex);

	void setCompositeByMeta(T meta, T composite);

	void setCompositeBySuper(T superGeneric, T composite);

	void removeCompositeByMeta(T meta, T composite);

	void removeCompositeBySuper(T superGeneric, T composite);

	@SuppressWarnings("unchecked")
	default T plug() {
		T t = getMeta().getInstances().set((T) this);
		getSupersStream().forEach(superGeneric -> superGeneric.getInheritings().set((T) this));
		getComponentsStream().forEach(component -> component.setCompositeByMeta(getMeta(), (T) this));
		getSupersStream().forEach(superGeneric -> getComponentsStream().forEach(component -> component.setCompositeBySuper(superGeneric, (T) this)));

		// assert getSupersStream().allMatch(superGeneric -> this == superGeneric.getInheritings().get((T) this));
		// assert Arrays.stream(getComponents()).allMatch(component -> this == component.getMetaComposites(getMeta()).get((T) this));
		// assert getSupersStream().allMatch(superGeneric -> Arrays.stream(getComponents()).allMatch(component -> component == component.getSuperComposites(superGeneric).get((T) this)));

		return t;
	}

	@SuppressWarnings("unchecked")
	default boolean unplug() {
		boolean result = getMeta().getInstances().remove((T) this);
		if (!result)
			rollbackAndThrowException(new NotFoundException(((DisplayService<T>) this).info()));
		getSupersStream().forEach(superGeneric -> superGeneric.getInheritings().remove((T) this));
		getComponentsStream().forEach(component -> component.removeCompositeByMeta(getMeta(), (T) this));
		getSupersStream().forEach(superGeneric -> getComponentsStream().forEach(component -> component.removeCompositeBySuper(superGeneric, (T) this)));
		return result;
	}
	//
	// @SuppressWarnings("unchecked")
	// default void removeInstance(Serializable value, T... components) {
	// T t = getInstance(value, components);
	// if (t == null)
	// rollbackAndThrowException(new NotFoundException(((DisplayService<T>) this).info()));
	// ((BindingService<T>) t).unplug();
	// }

}
