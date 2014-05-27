package org.genericsystem.kernel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.genericsystem.kernel.services.RestructuratorService;

public abstract class RemoveRestructurator<T extends RestructuratorService<T>> extends HashMap<T, T> {
	private static final long serialVersionUID = -3498885981892406254L;

	private Vertex vertexRemoved;

	public Vertex getVertexRemoved() {
		return vertexRemoved;
	}

	private void setVertexRemoved(Vertex vertexRemoved) {
		this.vertexRemoved = vertexRemoved;
	}

	public RemoveRestructurator(Vertex vertexRemoved) {
		setVertexRemoved(vertexRemoved);
	}

	public void rebuildAll() {
		assert getVertexRemoved() != null;
		LinkedHashSet<Vertex> dependenciesToRebuild = getVertexRemoved().computeAllDependencies();
		dependenciesToRebuild.forEach(RestructuratorService::unplug);
		dependenciesToRebuild.remove(getVertexRemoved());
		for (Vertex dependency : dependenciesToRebuild)
			getOrBuild((T) dependency);
	}

	private T getOrBuild(T oldDependency) {
		if (oldDependency.isAlive())
			return oldDependency;
		if (oldDependency.equals(getVertexRemoved()))
			return (T) getVertexRemoved().getMeta();
		T newDependency = get(oldDependency);
		if (newDependency == null) {
			T meta = (oldDependency == oldDependency.getMeta()) ? (T) oldDependency : getOrBuild((T) oldDependency.getMeta());
			List<Vertex> supers = new ArrayList<Vertex>();
			oldDependency.getSupersStream().forEach(x -> addSupers((Vertex) x, supers));
			List<T> components = oldDependency.getComponentsStream().map(x -> x.equals(oldDependency) ? null : getOrBuild(x)).collect(Collectors.toList());
			newDependency = meta.buildInstance((List<T>) supers, oldDependency.getValue(), components).plug();
		}
		put(oldDependency, newDependency);
		return newDependency;
	}

	private boolean addSupers(Vertex candidate, List<Vertex> target) {
		if (target.contains(candidate))
			return false;
		if (get(candidate) != null)
			return addSupers((Vertex) get(candidate), target);
		if (candidate.equals(getVertexRemoved())) {
			if (getVertexRemoved().getSupersStream().count() == 0)
				return target.add(getVertexRemoved().getMeta());
			getVertexRemoved().getSupersStream().forEach(x -> addSupers(x, target));
		}
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
