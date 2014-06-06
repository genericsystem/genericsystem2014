package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.genericsystem.kernel.exceptions.NotFoundException;

public interface UpdatableService<T extends UpdatableService<T>> extends BindingService<T> {

	default T setValue(Serializable value) {
		T meta = getMeta();
		return rebuildAll(() -> buildInstance().init(meta.getLevel() + 1, meta, getSupers(), value, getComponents()).plug());
	}

	default T addSuper(T superToAdd) {
		return rebuildAll(() -> getMeta().buildInstance(new OrderedSupers<T>(getSupersStream(), superToAdd), getValue(), getComponents()).plug());
	}

	default T replaceComponent(T source, T target) {
		return rebuildAll(() -> getMeta().buildInstance(getSupers(), getValue(), findNewComponentList(source, target)).plug());
	}

	default T replaceComponentWithValueModification(T source, T target, Serializable value) {
		T meta = getMeta();
		return rebuildAll(() -> buildInstance().init(meta.getLevel() + 1, meta, getSupers(), value, findNewComponentList(source, target)).plug());
	}

	@FunctionalInterface
	interface Rebuilder<T> {
		T rebuild();
	}

	default T rebuildAll(Rebuilder<T> rebuilder) {
		Map<T, T> convertMap = new HashMap<T, T>();
		LinkedHashSet<T> dependenciesToRebuild = this.computeAllDependencies();
		dependenciesToRebuild.forEach(UpdatableService::unplug);

		T build = rebuilder.rebuild();
		dependenciesToRebuild.remove(this);
		convertMap.put((T) this, build);

		dependenciesToRebuild.forEach(x -> x.getOrBuild(convertMap));
		return build;
	}

	default T getOrBuild(Map<T, T> convertMap) {
		if (this.isAlive())
			return (T) this;
		T newDependency = convertMap.get(this);
		if (newDependency == null) {
			newDependency = this.build(convertMap);
			convertMap.put((T) this, newDependency);
		}
		return newDependency;
	}

	default T build(Map<T, T> convertMap) {
		T meta = (this == getMeta()) ? (T) this : getMeta().getOrBuild(convertMap);
		return meta.buildInstance(getSupersStream().map(x -> x.getOrBuild(convertMap)).collect(Collectors.toList()), getValue(), getComponentsStream().map(x -> x.equals(this) ? null : x.getOrBuild(convertMap)).collect(Collectors.toList())).plug();
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

}
