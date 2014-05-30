package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.genericsystem.kernel.services.RestructuratorService;

public abstract class RemoveRestructurator<T extends RestructuratorService<T>> extends HashMap<T, T> {
	private static final long serialVersionUID = -3498885981892406254L;

	private Vertex vertexToRemove;

	private List<Vertex> instancesOfVertexToRemove;

	private void setVertexToRemove(Vertex vertexToRemove) {
		this.vertexToRemove = vertexToRemove;
		instancesOfVertexToRemove = new ArrayList<Vertex>();
		for (Vertex v : vertexToRemove.getInstances().stream().collect(Collectors.toList()))
			instancesOfVertexToRemove.add(v);
	}

	public RemoveRestructurator(Vertex vertexToRemove) {
		assert vertexToRemove != null;
		setVertexToRemove(vertexToRemove);
	}

	public void rebuildAll() {
		LinkedHashSet<Vertex> oldDependenciesUnpluged = vertexToRemove.computeAllDependencies();
		oldDependenciesUnpluged.forEach(RestructuratorService::unplug);
		oldDependenciesUnpluged.remove(vertexToRemove);
		put((T) vertexToRemove, (T) vertexToRemove.getMeta());// FIXME made to fix meta's management on buildDependency(T)
		for (Vertex dependency : oldDependenciesUnpluged)
			getOrBuild((T) dependency);
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
		T meta = getOrBuild((T) oldDependency.getMeta());
		Serializable value = oldDependency.getValue();

		List<Vertex> supers = new ArrayList<>();
		for (T v : oldDependency.getSupersStream().collect(Collectors.toList())) {
			if (v.equals(vertexToRemove))
				for (Vertex instance : instancesOfVertexToRemove)
					addThinly((Vertex) getOrBuild((T) instance), supers);
			else
				addThinly((Vertex) getOrBuild((T) v), supers);
		}

		List<Vertex> components = (List<Vertex>) oldDependency.getComponentsStream().collect(Collectors.toList());
		if (components.isEmpty()) {
			T newDependency = meta.buildInstance((List<T>) supers, value, (List<T>) components).plug();
			put(oldDependency, newDependency);
			return newDependency;
		}

		components.remove(vertexToRemove);
		for (Vertex component : vertexToRemove.getInheritings()) {
			components.add(component);
			T newDependency = meta.buildInstance((List<T>) supers, value, (List<T>) components).plug();
			components.remove(component);
			// FIXME put and return
		}
		return null;
	}

	private boolean addThinly(Vertex candidate, List<Vertex> target) {
		for (Vertex vertex : target)
			if (vertex.inheritsFrom(candidate))
				return false;
		Iterator<Vertex> it = target.iterator();
		while (it.hasNext())
			if (candidate.inheritsFrom(it.next()))
				it.remove();
		return target.add(candidate);
	}

}
