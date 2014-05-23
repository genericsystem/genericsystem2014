package org.genericsystem.kernel.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.genericsystem.kernel.Vertex;

public abstract class Restructurator<T> extends HashMap<Vertex, Vertex> {
	private static final long serialVersionUID = -3498885981892406254L;

	T rebuildAll(Vertex old) {
		return rebuildAll(old, old.computeAllDependencies());
	}

	T rebuildAll(Vertex old, LinkedHashSet<Vertex> dependenciesToRebuild) {
		disconnect(dependenciesToRebuild);
		Vertex build = recreateNewVertex(old, dependenciesToRebuild);
		reconstructAll(dependenciesToRebuild);
		return (T) build;
	}

	private void disconnect(LinkedHashSet<Vertex> linkedHashSet) {
		for (Vertex dependency : linkedHashSet)
			dependency.unplug();
	}

	private Vertex recreateNewVertex(Vertex old, LinkedHashSet<Vertex> dependenciesToRebuild) {
		Vertex build = (Vertex) rebuild();
		if (build != null)
			dependenciesToRebuild.remove(old);
		put(old, build);
		return build;
	}

	abstract T rebuild();

	private void reconstructAll(LinkedHashSet<Vertex> dependenciesToUpdate) {
		for (Vertex oldDependency : dependenciesToUpdate)
			put(oldDependency, reconstructConnectedVertex(oldDependency));
	}

	private Vertex reconstructConnectedVertex(Vertex oldDependency) {
		return oldDependency.buildInstance().init(replaceByNewValueIfExists(oldDependency.getMeta()), new AdjustList(oldDependency.getSupersStream().collect(Collectors.toList())), oldDependency.getValue(), new AdjustList(oldDependency.getComponents()))
				.plug();
	}

	private Vertex replaceByNewValueIfExists(Vertex vertex) {
		if (containsKey(vertex))
			return get(vertex);
		return vertex;
	}

	private class AdjustList extends ArrayList<Vertex> {
		private static final long serialVersionUID = -82460896265173205L;

		private AdjustList(List<Vertex> olds) {
			for (Vertex vertexToAdjust : olds)
				adjust(vertexToAdjust);
		}

		private void adjust(Vertex vertexToAdjust) {
			if (vertexToAdjust.isAlive())
				add(vertexToAdjust);
			else {
				if (Restructurator.this.containsKey(vertexToAdjust))
					add(Restructurator.this.get(vertexToAdjust));
				else
					adjustParents(vertexToAdjust);
			}
		}

		private void adjustParents(Vertex vertexToAdjust) {
			adjustVertexToReconstruct(vertexToAdjust.getSupersStream().collect(Collectors.toList()), vertexToAdjust.getComponents());
		}

		@SafeVarargs
		private final void adjustVertexToReconstruct(List<Vertex>... varargs) {
			for (List<Vertex> parentsToAdjust : varargs)
				for (Vertex vertexToAdjust : parentsToAdjust)
					adjust(Restructurator.this.get(vertexToAdjust));
		}
	}

}