package org.genericsystem.kernel.services;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.exceptions.ExistException;
import org.genericsystem.kernel.exceptions.NotFoundException;

public interface DependenciesService extends AncestorsService, FactoryService, ExceptionAdviserService {

	interface Dependencies<T> extends Snapshot<T> {

		boolean remove(T vertex);

		void add(T vertex);

		default T set(T vertex) {
			T result = get(vertex);
			if (result == null) {
				add(vertex);
				return vertex;
			}
			return result;
		}

	}

	interface CompositesDependencies<T> extends Dependencies<Entry<T, Dependencies<T>>> {

		default Dependencies<T> internalGetByIndex(T index) {
			Iterator<Entry<T, Dependencies<T>>> it = iterator();
			while (it.hasNext()) {
				Entry<T, Dependencies<T>> next = it.next();
				if (index.equals(next.getKey()))
					return next.getValue();
			}
			return null;
		}

		default Snapshot<T> getByIndex(T index) {
			Snapshot<T> result = internalGetByIndex(index);
			return result != null ? result : AbstractSnapshot.<T> emptySnapshot();
		}

		default T setByIndex(T index, T vertex) {
			Dependencies<T> result = internalGetByIndex(index);
			if (result == null) {
				result = buildDependencies();
				set(new AbstractMap.SimpleEntry<T, Dependencies<T>>(index, result));
			}
			return result.set(vertex);
		}

		default public boolean removeByIndex(T index, T vertex) {
			Dependencies<T> dependencies = internalGetByIndex(index);
			if (dependencies == null)
				return false;
			return dependencies.remove(vertex);
		}

		Dependencies<T> buildDependencies();
	}

	Snapshot<Vertex> getInstances();

	Snapshot<Vertex> getInheritings();

	Snapshot<Vertex> getMetaComposites(Vertex meta);

	Snapshot<?> getMetaComposites();

	Snapshot<?> getSuperComposites(Vertex superVertex);

	Snapshot<?> getSuperComposites();

	default boolean isPlugged() throws NotFoundException {
		return this == getPlugged();
	}

	default Vertex getPlugged() {
		return getMeta().getInstances().get((Vertex) this);
	}

	default Vertex plug(boolean throwsExistException) {
		Vertex vertex = getMeta().getInstances().set((Vertex) this);
		if (this != vertex) {
			if (throwsExistException)
				rollbackAndThrowException(new ExistException(vertex));
			return vertex;
		}
		getSupersStream().forEach(superGeneric -> superGeneric.getInheritings().set((Vertex) this));
		getComponentsStream().forEach(component -> component.getMetaComposites().setByIndex(getMeta(), (Vertex) this));
		getSupersStream().forEach(superGeneric -> getComponentsStream().forEach(component -> component.getSuperComposites().setByIndex(superGeneric, (Vertex) this)));

		// assert getSupersStream().allMatch(superGeneric -> this == superGeneric.getInheritings().get((Vertex) this));
		// assert Arrays.stream(getComponents()).allMatch(component -> this == component.getMetaComposites(getMeta()).get((Vertex) this));
		// assert getSupersStream().allMatch(superGeneric -> Arrays.stream(getComponents()).allMatch(component -> component == component.getSuperComposites(superGeneric).get((Vertex) this)));

		return vertex;
	}

	default boolean unplug() {
		boolean result = getMeta().getInstances().remove((Vertex) this);
		if (!result)
			rollbackAndThrowException(new NotFoundException((Vertex) this));
		getSupersStream().forEach(superGeneric -> superGeneric.getInheritings().remove((Vertex) this));
		getComponentsStream().forEach(component -> component.getMetaComposites().removeByIndex(getMeta(), (Vertex) this));
		getSupersStream().forEach(superGeneric -> getComponentsStream().forEach(component -> component.getSuperComposites().removeByIndex(superGeneric, (Vertex) this)));
		return result;
	}
}
