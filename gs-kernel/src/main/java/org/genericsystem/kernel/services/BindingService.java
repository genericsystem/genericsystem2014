package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.exceptions.AmbiguousSelectionException;
import org.genericsystem.kernel.exceptions.CrossEnginesAssignementsException;
import org.genericsystem.kernel.exceptions.ExistsException;
import org.genericsystem.kernel.exceptions.NotFoundException;

public interface BindingService<T extends BindingService<T>> extends DependenciesService<T>, FactoryService<T>, ExceptionAdviserService<T>, DisplayService<T> {

	@SuppressWarnings("unchecked")
	default T addInstance(Serializable value, T... components) {
		return addInstance(Collections.emptyList(), value, components);
	}

	@SuppressWarnings("unchecked")
	default T addInstance(T superGeneric, Serializable value, T... components) {
		return addInstance(Collections.singletonList(superGeneric), value, components);
	}

	@SuppressWarnings("unchecked")
	default T addInstance(List<T> overrides, Serializable value, T... components) {
		checkSameEngine(Arrays.asList(components));
		checkSameEngine(overrides);

		T nearestMeta = adjustMeta(overrides, value, Arrays.asList(components));
		if (nearestMeta != this)
			return nearestMeta.addInstance(overrides, value, components);
		T weakInstance = getWeakInstance(value, components);
		if (weakInstance != null)
			rollbackAndThrowException(new ExistsException(weakInstance.info()));
		return buildInstance(overrides, value, Arrays.asList(components)).plug();
	}

	default void checkSameEngine(List<T> components) {
		if (components.stream().anyMatch(component -> !component.getRoot().equals(getRoot())))
			rollbackAndThrowException(new CrossEnginesAssignementsException());
	}

	// TODO we have to compute super of "this" if necessary here
	@SuppressWarnings("unchecked")
	default T adjustMeta(List<T> overrides, Serializable subValue, List<T> subComponents) {
		T result = null;
		for (T directInheriting : getInheritings())
			if (directInheriting.isMetaOf(overrides, subValue, subComponents))
				if (result == null)
					result = directInheriting;
				else
					rollbackAndThrowException(new AmbiguousSelectionException("Ambigous selection : " + result.info() + directInheriting.info()));
		return result == null ? (T) this : result;
	}

	@SuppressWarnings("unchecked")
	default T getInstance(Serializable value, T... components) {
		return buildInstance(Collections.emptyList(), value, Arrays.asList(components)).getAlive();
	}

	@SuppressWarnings("unchecked")
	default T getInstance(List<T> supers, Serializable value, T... components) {
		T nearestMeta = adjustMeta(supers, value, Arrays.asList(components));
		if (nearestMeta != this)
			return nearestMeta.getInstance(supers, value, components);
		T result = getInstance(value, components);
		if (result != null && supers.stream().allMatch(superT -> result.inheritsFrom(superT)))
			return result;
		return null;
	}

	@SuppressWarnings("unchecked")
	default T getWeakInstance(Serializable value, T... components) {
		return buildInstance(Collections.emptyList(), value, Arrays.asList(components)).getWeakAlive();
	}

	@Override
	Dependencies<T> getInstances();

	@Override
	Dependencies<T> getInheritings();

	@Override
	CompositesDependencies<T> getMetaComposites();

	@Override
	CompositesDependencies<T> getSuperComposites();

	default Snapshot<T> getMetaComposites(T meta) {
		return getMetaComposites().getByIndex(meta);
	}

	default Snapshot<T> getSuperComposites(T superVertex) {
		return getSuperComposites().getByIndex(superVertex);
	}

	@SuppressWarnings("unchecked")
	default T plug() {
		T t = getMeta().getInstances().set((T) this);
		getSupersStream().forEach(superGeneric -> superGeneric.getInheritings().set((T) this));
		getComponentsStream().forEach(component -> component.getMetaComposites().setByIndex(getMeta(), (T) this));
		getSupersStream().forEach(superGeneric -> getComponentsStream().forEach(component -> component.getSuperComposites().setByIndex(superGeneric, (T) this)));

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
		getComponentsStream().forEach(component -> component.getMetaComposites().removeByIndex(getMeta(), (T) this));
		getSupersStream().forEach(superGeneric -> getComponentsStream().forEach(component -> component.getSuperComposites().removeByIndex(superGeneric, (T) this)));
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
