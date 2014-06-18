package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface UpdatableService<T extends UpdatableService<T>> extends BindingService<T> {

	default T updateValue(Serializable newValue) {
		return update(getSupers(), newValue, getComponents());
	}

	default T updateSupers(T... supersToAdd) {
		return update(Arrays.asList(supersToAdd), getValue(), getComponents());
	}

	default T updateComponents(T... newComponents) {
		return update(getSupers(), getValue(), newComponents);
	}

	default T update(List<T> supersToAdd, Serializable newValue, T... newComponents) {
		return update(supersToAdd, newValue, Arrays.asList(newComponents));
	}

	default T update(List<T> supersToAdd, Serializable newValue, List<T> newComponents) {
		if (newComponents.size() != getComponents().size())
			rollbackAndThrowException(new IllegalArgumentException());
		return rebuildAll(() -> buildInstance().init(getLevel(), getMeta(), new Supers<T>(getSupers(), supersToAdd), newValue, newComponents).plug());
	}

	@SuppressWarnings("unchecked")
	default T rebuildAll(Supplier<T> rebuilder) {
		Map<T, T> convertMap = new HashMap<T, T>();
		LinkedHashSet<T> dependenciesToRebuild = this.computeAllDependencies();
		dependenciesToRebuild.forEach(UpdatableService::unplug);

		T build = rebuilder.get();
		dependenciesToRebuild.remove(this);
		convertMap.put((T) this, build);

		dependenciesToRebuild.forEach(x -> x.getOrBuild(convertMap));
		return build;
	}

	@SuppressWarnings("unchecked")
	default T getOrBuild(Map<T, T> convertMap) {
		if (this.isAlive())
			return (T) this;
		T newDependency = convertMap.get(this);
		if (newDependency == null)
			convertMap.put((T) this, newDependency = this.build(convertMap));
		return newDependency;
	}

	@SuppressWarnings("unchecked")
	default T build(Map<T, T> convertMap) {
		T meta = (this == getMeta()) ? (T) this : getMeta().getOrBuild(convertMap);
		return meta.buildInstance(getSupersStream().map(x -> x.getOrBuild(convertMap)).collect(Collectors.toList()), getValue(), getComponentsStream().map(x -> x.equals(this) ? null : x.getOrBuild(convertMap)).collect(Collectors.toList())).plug();
	}

	// default List<T> replaceInComponents(T source, T target) {
	// List<T> newComponents = getComponents();
	// boolean hasBeenModified = false;
	// for (int i = 0; i < newComponents.size(); i++)
	// if (source.equiv(newComponents.get(i))) {
	// newComponents.set(i, target);
	// hasBeenModified = true;
	// }
	// if (!hasBeenModified)
	// rollbackAndThrowException(new NotFoundException("Component : " + source.info() + " not found in component list : " + newComponents.toString() + " for " + this.info() + "when modifying componentList."));
	// return newComponents;
	// }

	default T setInstance(Serializable value, @SuppressWarnings("unchecked") T... components) {
		return setInstance(Collections.emptyList(), value, components);
	}

	@SuppressWarnings("unchecked")
	default T setInstance(T override, Serializable value, T... components) {
		return setInstance(Collections.singletonList(override), value, components);
	}

	@SuppressWarnings("unchecked")
	default T setInstance(List<T> overrides, Serializable value, T... components) {
		checkSameEngine(Arrays.asList(components));
		checkSameEngine(overrides);
		T nearestMeta = adjustMeta(overrides, value, Arrays.asList(components));
		if (nearestMeta != this)
			return nearestMeta.setInstance(overrides, value, components);
		T weakInstance = getWeakInstance(value, components);
		if (weakInstance != null) {
			if (weakInstance.equiv(this, value, Arrays.asList(components)))
				return weakInstance;
			return weakInstance.update(overrides, value, components);
		}
		return buildInstance(overrides, value, Arrays.asList(components)).plug();
	}

	// default T updateInstance(T instance, List<T> overrides, Serializable value, List<T> components) {
	// if (components.size() != instance.getComponents().size())
	// rollbackAndThrowException(new IllegalArgumentException());
	// boolean needToUpdateSupers = !allOverridesAreReached(overrides, instance.getSupers());
	// if (instance.equiv(instance.getMeta(), value, components) && !needToUpdateSupers)
	// return instance;
	// List<T> supers = needToUpdateSupers ? new Supers<T>(instance.getSupers(), getUnreachedSupers(instance, overrides)) : instance.getSupers();
	// return instance.update(supers, value, instance.findNewComponentsList(components));
	// }

	// default List<T> findNewComponentsList(List<T> target) {
	// List<T> newComponents = getComponents();
	// for (int i = 0; i < newComponents.size(); i++)
	// newComponents.set(i, target.get(i));
	// return newComponents;
	// }

	default List<T> getUnreachedSupers(T instance, List<T> overrides) {
		return overrides.stream().filter(override -> instance.getSupers().stream().allMatch(superVertex -> !superVertex.inheritsFrom(override))).collect(Collectors.toList());
	}

	public static class Supers<T extends UpdatableService<T>> extends ArrayList<T> {
		private static final long serialVersionUID = 6163099887384346235L;

		public Supers(List<T> adds) {
			adds.forEach(this::add);
		}

		public Supers(List<T> adds, T lastAdd) {
			this(adds);
			add(lastAdd);
		}

		public Supers(List<T> adds, List<T> otherAdds) {
			this(adds);
			otherAdds.forEach(this::add);
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
	}

}
