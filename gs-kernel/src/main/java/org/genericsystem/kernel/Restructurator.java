package org.genericsystem.kernel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.genericsystem.kernel.services.RestructuratorService;

public abstract class Restructurator<T extends RestructuratorService<T>> extends HashMap<T, T> {
	private static final long serialVersionUID = -3498885981892406254L;

	public T rebuildAll(T old, LinkedHashSet<T> dependenciesToRebuild) {
		disconnect(dependenciesToRebuild);
		T build = recreateNewVertex(old, dependenciesToRebuild);
		reconstructAll(dependenciesToRebuild);
		return build;
	}

	private void disconnect(LinkedHashSet<T> linkedHashSet) {
		for (T dependency : linkedHashSet)
			dependency.unplug();
	}

	private T recreateNewVertex(T old, LinkedHashSet<T> dependenciesToRebuild) {
		T build = rebuild();
		if (build != null)
			dependenciesToRebuild.remove(old);
		put(old, build);
		return build;
	}

	protected abstract T rebuild();

	private void reconstructAll(LinkedHashSet<T> dependenciesToUpdate) {
		for (T oldDependency : dependenciesToUpdate)
			put(oldDependency, reconstructConnectedVertex(oldDependency));
	}

	private T reconstructConnectedVertex(T oldDependency) {
		return oldDependency.buildInstance().init(replaceByNewValueIfExists(oldDependency.getMeta()), new AdjustList(oldDependency.getSupersStream().collect(Collectors.toList())), oldDependency.getValue(), new AdjustList(oldDependency.getComponents()))
				.plug();
	}

	private T replaceByNewValueIfExists(T vertex) {
		if (containsKey(vertex))
			return get(vertex);
		return vertex;
	}

	class AdjustList extends ArrayList<T> {
		private static final long serialVersionUID = -82460896265173205L;

		private AdjustList(List<T> olds) {
			for (T vertexToAdjust : olds)
				adjust(vertexToAdjust);
		}

		private void adjust(T vertexToAdjust) {
			if (vertexToAdjust.isAlive())
				add(vertexToAdjust);
			else {
				if (Restructurator.this.containsKey(vertexToAdjust))
					add(Restructurator.this.get(vertexToAdjust));
				else
					adjustParents(vertexToAdjust);
			}
		}

		private void adjustParents(T vertexToAdjust) {
			adjustVertexToReconstruct(vertexToAdjust.getSupersStream().collect(Collectors.toList()), vertexToAdjust.getComponents());
		}

		@SafeVarargs
		private final void adjustVertexToReconstruct(List<T>... varargs) {
			for (List<T> parentsToAdjust : varargs)
				for (T vertexToAdjust : parentsToAdjust)
					adjust(Restructurator.this.get(vertexToAdjust));
		}
	}
}
