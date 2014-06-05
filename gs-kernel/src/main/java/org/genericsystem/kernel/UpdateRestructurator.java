package org.genericsystem.kernel;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.stream.Collectors;

import org.genericsystem.kernel.services.UpdatableService;

@FunctionalInterface
public interface UpdateRestructurator<T extends UpdatableService<T>> {

	default T rebuildAll(T old) {
		Map<T, T> convertMap = new HashMap<T, T>();
		LinkedHashSet<T> dependenciesToRebuild = old.computeAllDependencies();
		dependenciesToRebuild.forEach(UpdatableService::unplug);
		T build = rebuild();
		dependenciesToRebuild.remove(old);
		convertMap.put(old, build);
		dependenciesToRebuild.forEach(x -> getOrBuild(x, convertMap));
		return build;
	}

	default T getOrBuild(T vertex, Map<T, T> convertMap) {
		if (vertex.isAlive())
			return vertex;
		T newDependency = convertMap.get(vertex);
		if (newDependency == null) {
			newDependency = build(vertex, convertMap);
			convertMap.put(vertex, newDependency);
		}
		return newDependency;
	}

	default T build(T oldDependency, Map<T, T> convertMap) {
		T meta = (oldDependency == oldDependency.getMeta()) ? oldDependency : getOrBuild(oldDependency.getMeta(), convertMap);
		return meta.buildInstance(oldDependency.getSupersStream().map(x -> getOrBuild(x, convertMap)).collect(Collectors.toList()), oldDependency.getValue(),
				oldDependency.getComponentsStream().map(x -> x.equals(oldDependency) ? null : getOrBuild(x, convertMap)).collect(Collectors.toList())).plug();
	}

	T rebuild();

}
