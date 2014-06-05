package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.genericsystem.kernel.UpdateRestructurator;
import org.genericsystem.kernel.exceptions.NotFoundException;
import org.genericsystem.kernel.exceptions.RollbackException;

public interface UpdatableService<T extends UpdatableService<T>> extends BindingService<T> {

	@SuppressWarnings("unchecked")
	default T setValue(Serializable value) {
		T meta = getMeta();
		return ((UpdateRestructurator<T>) (() -> buildInstance().init(meta.getLevel() + 1, meta, getSupers(), value, getComponents()).plug())).rebuildAll((T) this);
	}

	@SuppressWarnings("unchecked")
	default T addSuper(T superToAdd) {
		return ((UpdateRestructurator<T>) (() -> getMeta().buildInstance(new OrderedSupers<T>(getSupersStream(), superToAdd), getValue(), getComponents()).plug())).rebuildAll((T) this);
	}

	@SuppressWarnings("unchecked")
	default T replaceComponent(T source, T target) {
		return ((UpdateRestructurator<T>) (() -> getMeta().buildInstance(getSupers(), getValue(), findNewComponentList(source, target)).plug())).rebuildAll((T) this);
	}

	@SuppressWarnings("unchecked")
	default T replaceComponentWithValueModification(T source, T target, Serializable value) {
		T meta = getMeta();
		return ((UpdateRestructurator<T>) (() -> buildInstance().init(meta.getLevel() + 1, meta, getSupers(), value, findNewComponentList(source, target)).plug())).rebuildAll((T) this);
	}

	default List<T> findNewComponentList(T source, T target) {
		List<T> newComponents = getComponents();
		boolean hasBeenModified = false;
		for (int i = 0; i < newComponents.size(); i++)
			if (source.equiv(newComponents.get(i))) {
				newComponents.set(i, target);
				hasBeenModified = true;
			}
		if (!hasBeenModified)
			rollbackAndThrowException(new NotFoundException("Component : " + source.info() + " not found in component list : " + newComponents.toString() + " for " + this.info() + "when modifying componentList."));
		return newComponents;
	}

	public static class OrderedSupers<T extends UpdatableService<T>> extends ArrayList<T> {
		private static final long serialVersionUID = 6163099887384346235L;

		public OrderedSupers(Stream<T> adds) {
			for (T add : adds.collect(Collectors.toList()))
				add(add);
		}

		public OrderedSupers(Stream<T> adds, T lastAdd) {
			this(adds);
			add(lastAdd);
		}

		@Override
		public boolean add(T candidate) {
			for (T element : this)
				if (element.inheritsFrom(candidate))
					return false;
			Iterator<T> it = iterator();
			while (it.hasNext())
				if (candidate.inheritsFrom(it.next()))
					it.remove();
			return super.add(candidate);
		}

		public List<T> toList() {
			return this.stream().collect(Collectors.toList());
		}
	}

	default T setInstance(Serializable value, @SuppressWarnings("unchecked") T... components) {
		return setInstance(Collections.emptyList(), value, components);
	}

	default T setInstance(T superGeneric, Serializable value, @SuppressWarnings("unchecked") T... components) {
		return setInstance(Collections.singletonList(superGeneric), value, components);
	}

	@SuppressWarnings("unchecked")
	default T setInstance(List<T> overrides, Serializable value, T... components) {
		checkSameEngine(Arrays.asList(components));
		checkSameEngine(overrides);
		T nearestMeta = computeNearestMeta(overrides, value, Arrays.asList(components));
		if (nearestMeta != this)
			return nearestMeta.setInstance(overrides, value, components);
		T instance = getWeakInstance(value, components);
		if (instance == null)
			return buildInstance(overrides, value, Arrays.asList(components)).plug();
		return updateInstance(instance, overrides, value, components);
	}

	default T updateInstance(T instance, List<T> overrides, Serializable value, T... components) {
		if (!value.equals(instance.getValue()))
			instance = instance.setValue(value);
		if (!allOverridesAreReached(overrides, instance.getSupersStream().collect(Collectors.toList()))) {
			List<T> supers = instance.getSupers();
			Stream<T> overridesToAdd = overrides.stream().filter(override -> supers.stream().allMatch(superVertex -> !superVertex.inheritsFrom(override)));
			for (T superElement : overridesToAdd.collect(Collectors.toList()))
				instance = instance.addSuper(superElement);
			// supersToAdd.map(override -> instance.addSuper(override));
			// instance;
		}
		int nbComponents = this.getComponents().size();
		if (nbComponents != instance.getComponents().size())
			new RollbackException();
		Iterator componentsIterator = Arrays.asList(components).iterator();
		Iterator instanceComponentsIterator = Arrays.asList(instance.getComponents()).iterator();
		// while (componentsIterator.hasNext()) {
		// BindingService<T> currentComponent = (BindingService<T>) componentsIterator.next();
		// BindingService<T> currentInstanceComponent = (BindingService<T>) instanceComponentsIterator.next();
		// if (!currentComponent.equals(currentInstanceComponent)) {
		// //
		// }
		// }
		return instance;
	}

}
