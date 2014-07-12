package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.genericsystem.kernel.exceptions.ExistsException;

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

	default T update(Serializable newValue, T... newComponents) {
		return update(Collections.emptyList(), newValue, Arrays.asList(newComponents));
	}

	default T update(List<T> supersToAdd, Serializable newValue, List<T> newComponents) {
		if (newComponents.size() != getComponents().size())
			rollbackAndThrowException(new IllegalArgumentException());
		return rebuildAll(() -> buildInstance().init(getMeta(), new Supers<T>(getSupers(), supersToAdd), newValue, newComponents).plug());
	}

	@SuppressWarnings("unchecked")
	default T rebuildAll(Supplier<T> rebuilder) {
		class ConvertMap extends HashMap<T, T> {
			private static final long serialVersionUID = 5003546962293036021L;

			T convert(T dependency) {
				if (dependency.isAlive())
					return dependency;
				T newDependency = get(dependency);
				if (newDependency == null) {
					T meta = (dependency.equals(dependency.getMeta())) ? dependency : convert(dependency.getMeta());
					newDependency = meta.buildInstance(dependency.getSupersStream().map(x -> convert(x)).collect(Collectors.toList()), dependency.getValue(),
							dependency.getComponentsStream().map(x -> x.equals(this) ? null : convert(x)).collect(Collectors.toList())).plug();
					put(dependency, newDependency);
				}
				return newDependency;
			}
		}
		ConvertMap convertMap = new ConvertMap();
		LinkedHashSet<T> dependenciesToRebuild = computeAllDependencies();
		dependenciesToRebuild.forEach(UpdatableService::unplug);

		T build = rebuilder.get();
		dependenciesToRebuild.remove(this);
		convertMap.put((T) this, build);
		dependenciesToRebuild.forEach(x -> convertMap.convert(x));

		return build;
	}

	@SuppressWarnings("unchecked")
	default T rebuildAll(Supplier<T> rebuilder, List<T> overrides, Serializable value, List<T> components) {
		class ConvertMap extends HashMap<T, T> {
			private static final long serialVersionUID = 5003546962293036021L;

			T convert(T dependency) {
				if (dependency.isAlive())
					return dependency;
				T newDependency = get(dependency);
				if (newDependency == null) {
					T meta = (dependency.equals(dependency.getMeta())) ? dependency : convert(dependency.getMeta());
					newDependency = meta.buildInstance(dependency.getSupersStream().map(x -> convert(x)).collect(Collectors.toList()), dependency.getValue(),
							dependency.getComponentsStream().map(x -> x.equals(this) ? null : convert(x)).collect(Collectors.toList())).plug();
					put(dependency, newDependency);
				}
				return newDependency;
			}
		}
		ConvertMap convertMap = new ConvertMap();
		LinkedHashSet<T> dependenciesToRebuild = computeAllDependencies(overrides, value, components);
		dependenciesToRebuild.forEach(UpdatableService::unplug);

		T build = rebuilder.get();
		convertMap.put((T) this, build);

		dependenciesToRebuild.forEach(x -> convertMap.convert(x));

		return build;
	}

	// @SuppressWarnings("unchecked")
	// default T getOrBuild(Map<T, T> convertMap) {
	//
	// if (this.isAlive())
	// return (T) this;
	// T newDependency = convertMap.get(this);
	// if (newDependency == null) {
	// T meta = (this == getMeta()) ? (T) this : getMeta().getOrBuild(convertMap);
	// newDependency = meta.buildInstance(getSupersStream().map(x -> x.getOrBuild(convertMap)).collect(Collectors.toList()), getValue(), getComponentsStream().map(x -> x.equals(this) ? null : x.getOrBuild(convertMap)).collect(Collectors.toList()))
	// .plug();
	// convertMap.put((T) this, newDependency);
	// }
	// return newDependency;
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
		T instance = buildInstance(overrides, value, Arrays.asList(components));
		return instance.rebuildAll(() -> instance.plug());
	}

	default T getMetaAttribute() {
		T root = getRoot();
		return root.getInstance(root.getValue(), root);
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
		T nearestMeta = adjustMeta(overrides, value, Arrays.asList(components));
		if (nearestMeta != this)
			return nearestMeta.addInstance(overrides, value, components);
		T weakInstance = getWeakInstance(value, components);
		if (weakInstance != null)
			rollbackAndThrowException(new ExistsException("Attempts to add an already existing instance : " + weakInstance.info()));
		T instance = buildInstance(overrides, value, Arrays.asList(components));
		return instance.rebuildAll(() -> instance.plug());
	}

	// default List<T> getUnreachedSupers(T instance, List<T> overrides) {
	// return overrides.stream().filter(override -> instance.getSupers().stream().allMatch(superVertex -> !superVertex.inheritsFrom(override))).collect(Collectors.toList());
	// }

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
