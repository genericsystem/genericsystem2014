package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.SupersComputer;
import org.genericsystem.kernel.exceptions.AmbiguousSelectionException;
import org.genericsystem.kernel.exceptions.CrossEnginesAssignementsException;
import org.genericsystem.kernel.exceptions.ExistsException;
import org.genericsystem.kernel.exceptions.NotFoundException;

public interface BindingService<T extends BindingService<T>> extends DependenciesService<T>, FactoryService<T>, ExceptionAdviserService<T>, DisplayService<T> {

	// TODO KK
	default T setMetaAttribute() {
		return setMetaAttribute(Collections.emptyList());
	}

	default T setMetaAttribute(T component) {
		return setMetaAttribute(Collections.singletonList(component));
	}

	default T setMetaAttribute(int nbComponents) {
		if (nbComponents == 0)
			return (T) this;
		List<T> metaAttributes = new ArrayList<T>();
		for (int i = 0; i < nbComponents - 1; ++i)
			metaAttributes.add((T) this);
		return setMetaAttribute(metaAttributes);

	}

	default T setMetaAttribute(List<T> components) {
		checkSameEngine(components);
		T metaOfmeta = adjustMeta(Collections.emptyList(), getValue(), components);
		List<T> allComponents = new ArrayList<T>(components);
		allComponents.add(0, (T) this);
		T instance = buildInstance().init(0, metaOfmeta, Collections.emptyList(), getRoot().getValue(), allComponents).getAlive();// getInstance(getRoot().getValue(), allComponents);
		if (instance != null)
			return instance;
		List<T> supersList = new ArrayList<>(new SupersComputer<>(0, getMeta(), Collections.emptyList(), getRoot().getValue(), allComponents));
		T meta = adjustMeta(Collections.emptyList(), getValue(), allComponents);
		return meta.buildInstance().init(0, meta, supersList, getRoot().getValue(), allComponents).plug();
	}

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
		getRoot().setMetaAttribute(components.length);
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
	default T adjustMeta(List<T> overrides, Serializable subValue) {
		return adjustMeta(overrides, subValue, Collections.emptyList());
	}

	@SuppressWarnings("unchecked")
	default T adjustMeta(List<T> overrides, Serializable subValue, T subComponent) {
		return adjustMeta(overrides, subValue, Collections.singletonList(subComponent));
	}

	@SuppressWarnings("unchecked")
	default T adjustMeta(List<T> overrides, Serializable subValue, List<T> subComponents) {
		T result = null;
		for (T directInheriting : getInheritings())
			if (directInheriting.isMetaOf(overrides, subValue, subComponents))// KK
				if (result == null)
					result = directInheriting;
				else
					rollbackAndThrowException(new AmbiguousSelectionException("Ambigous selection : " + result.info() + directInheriting.info()));
		return result == null ? (T) this : result.adjustMeta(overrides, subValue, subComponents);
	}

	@SuppressWarnings("unchecked")
	default T getInstance(Serializable value) {
		return getInstance(value, Collections.emptyList());
	}

	@SuppressWarnings("unchecked")
	default T getInstance(Serializable value, T component) {
		return getInstance(value, Collections.singletonList(component));
	}

	@SuppressWarnings("unchecked")
	default T getInstance(Serializable value, List<T> components) {
		return buildInstance(Collections.emptyList(), value, components).getAlive();
	}

	@SuppressWarnings("unchecked")
	default T getInstance(T superVertex, Serializable value) {
		return getInstance(Collections.singletonList(superVertex), value, Collections.emptyList());
	}

	@SuppressWarnings("unchecked")
	default T getInstance(List<T> supers, Serializable value) {
		return getInstance(supers, value, Collections.emptyList());
	}

	@SuppressWarnings("unchecked")
	default T getInstance(List<T> supers, Serializable value, T component) {
		return getInstance(supers, value, Collections.singletonList(component));
	}

	@SuppressWarnings("unchecked")
	default T getInstance(T supers, Serializable value, List<T> components) {
		return getInstance(Collections.singletonList(supers), value, components);
	}

	@SuppressWarnings("unchecked")
	default T getInstance(T superVertex, Serializable value, T component) {
		return getInstance(Collections.singletonList(superVertex), value, Collections.singletonList(component));
	}

	@SuppressWarnings("unchecked")
	default T getInstance(List<T> supers, Serializable value, List<T> components) {
		T nearestMeta = adjustMeta(supers, value, components);
		if (nearestMeta != this)
			return nearestMeta.getInstance(supers, value, components);
		T result = getInstance(value, components);
		if (result != null && supers.stream().allMatch(superT -> result.inheritsFrom(superT)))
			return result;
		return null;
	}

	@SuppressWarnings("unchecked")
	default T getInstance(Serializable value, T... components) {
		return new AncestorsService<T>() {

			@Override
			public T getMeta() {
				return (T) BindingService.this;
			}

			@Override
			public List<T> getComponents() {
				return Arrays.asList(components);
			}

			@Override
			public Stream<T> getComponentsStream() {
				return Arrays.stream(components);
			}

			@Override
			public Serializable getValue() {
				return value;
			}

			@Override
			public int getLevel() {
				return getMeta().getLevel() + 1;
			}

			@Override
			public List<T> getSupers() {
				return Collections.emptyList();
			}

			@Override
			public Stream<T> getSupersStream() {
				return getSupers().stream();
			}

			@Override
			public org.genericsystem.kernel.services.SystemPropertiesService.WeakPredicate getWeakPredicate() {
				throw new UnsupportedOperationException();
			}
		}.getAlive();
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
