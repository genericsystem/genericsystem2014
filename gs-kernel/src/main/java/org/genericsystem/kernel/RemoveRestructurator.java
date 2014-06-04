package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.genericsystem.kernel.services.RemovableService;

public abstract class RemoveRestructurator<T extends RemovableService<T>> extends HashMap<T, T> {
	private static final long serialVersionUID = -3498885981892406254L;

	private T vertexToRemove;

	private List<T> instancesOfVertexToRemove;

	private void setVertexToRemove(T vertexToRemove) {
		this.vertexToRemove = vertexToRemove;
		instancesOfVertexToRemove = new ArrayList<T>();
		for (T v : vertexToRemove.getInstances().stream().collect(Collectors.toList()))
			instancesOfVertexToRemove.add(v);
	}

	// FIXME
	@Deprecated
	public RemoveRestructurator(T vertexToRemove) {
		assert vertexToRemove != null;
		setVertexToRemove(vertexToRemove);
	}

	public void rebuildAll() {
		LinkedHashSet<T> oldDependenciesUnpluged = vertexToRemove.computeAllDependencies();
		oldDependenciesUnpluged.forEach(RemovableService::unplug);
		oldDependenciesUnpluged.remove(vertexToRemove);
		put(vertexToRemove, vertexToRemove.getMeta());// FIXME made to fix meta's management on buildDependency(T)
		for (T dependency : oldDependenciesUnpluged)
			getOrBuild(dependency);
	}

	private T getOrBuild(T oldVertex) {
		if (oldVertex.isAlive())
			return oldVertex;
		T newVertex = get(oldVertex);
		if (newVertex != null)
			return newVertex;
		return buildDependency(oldVertex);
	}

	private T buildDependency(T oldDependency) {
		T meta = getOrBuild(oldDependency.getMeta());
		Serializable value = oldDependency.getValue();

		List<T> supers = new ArrayList<T>();
		for (T v : oldDependency.getSupersStream().collect(Collectors.toList())) {
			if (v.equals(vertexToRemove))
				for (T instance : instancesOfVertexToRemove)
					addThinly(getOrBuild(instance), supers);
			else
				addThinly(getOrBuild(v), supers);
		}

		List<T> components = (List<T>) oldDependency.getComponentsStream().collect(Collectors.toList());
		if (components.isEmpty()) {
			T newDependency = meta.buildInstance((List<T>) supers, value, (List<T>) components).plug();
			put(oldDependency, newDependency);
			return newDependency;
		}

		if (components.remove(vertexToRemove))
			for (T component : vertexToRemove.getInheritings()) {
				components.add(component);
				meta.buildInstance(supers, value, components).plug();
				components.remove(component);
				// FIXME put and return
			}
		else
			// node construction
			return null;
		return null;
	}

	private boolean addThinly(T candidate, List<T> target) {
		for (T vertex : target)
			if (vertex.inheritsFrom(candidate))
				return false;
		Iterator<T> it = target.iterator();
		while (it.hasNext())
			if (candidate.inheritsFrom(it.next()))
				it.remove();
		return target.add(candidate);
	}

}
