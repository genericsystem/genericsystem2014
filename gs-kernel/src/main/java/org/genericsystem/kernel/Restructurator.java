package org.genericsystem.kernel;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import org.genericsystem.kernel.services.RestructuratorService;

public abstract class Restructurator<T extends RestructuratorService<T>> extends HashMap<T, T> {
	private static final long serialVersionUID = -3498885981892406254L;

	public T rebuildAll(T old, LinkedHashSet<T> dependenciesToRebuild) {
		dependenciesToRebuild.forEach(RestructuratorService::unplug);
		T build = rebuild();
		dependenciesToRebuild.remove(old);
		put(old, build);
		dependenciesToRebuild.forEach(this::getOrBuild);
		return build;
	}

	private T getOrBuild(T vertex) {
		if (vertex.isAlive())
			return vertex;
		T newDependency = get(vertex);
		if (newDependency == null)
			newDependency = build(vertex);
		put(vertex, newDependency);
		return newDependency;
	}

	private T build(T oldDependency) {
		T meta = (oldDependency == oldDependency.getMeta()) ? oldDependency : getOrBuild(oldDependency.getMeta());
		return meta.buildInstance(oldDependency.getSupersStream().map(this::getOrBuild).collect(Collectors.toList()), oldDependency.getValue(),
				oldDependency.getComponentsStream().map(x -> x.equals(oldDependency) ? null : getOrBuild(x)).collect(Collectors.toList())).plug();
	}

	protected abstract T rebuild();

}
