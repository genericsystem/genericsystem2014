package org.genericsystem.kernel;

import java.util.Collections;
import org.genericsystem.kernel.Dependencies.DependenciesEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vertex extends ExtendedSignature<Vertex> implements VertexService<Vertex> {

	protected static Logger log = LoggerFactory.getLogger(Vertex.class);

	private final Dependencies<Vertex> instances = new DependenciesImpl<Vertex>();
	private final Dependencies<Vertex> inheritings = new DependenciesImpl<Vertex>();
	private final Dependencies<DependenciesEntry<Vertex>> superComposites = new DependenciesImpl<DependenciesEntry<Vertex>>();
	private final Dependencies<DependenciesEntry<Vertex>> metaComposites = new DependenciesImpl<DependenciesEntry<Vertex>>();

	@Override
	public Vertex buildInstance() {
		return new Vertex();
	}

	@Override
	public Snapshot<Vertex> getInstances() {
		return instances;
	}

	@Override
	public Snapshot<Vertex> getInheritings() {
		return inheritings;
	}

	@Override
	public Snapshot<Vertex> getComposites() {
		return () -> metaComposites.stream().map(entry -> entry.getValue().stream()).flatMap(x -> x).iterator();
	}

	@Override
	public Snapshot<Vertex> getMetaComposites(Vertex meta) {
		return () -> {
			for (DependenciesEntry<Vertex> entry : metaComposites)
				if (meta.equals(entry.getKey()))
					return entry.getValue().iterator();
			return Collections.emptyIterator();
		};
	};

	@Override
	public Snapshot<Vertex> getSuperComposites(Vertex superVertex) {
		return () -> {
			for (DependenciesEntry<Vertex> entry : superComposites)
				if (superVertex.equals(entry.getKey()))
					return entry.getValue().iterator();
			return Collections.emptyIterator();
		};
	}

	@Override
	public Vertex indexByMeta(Vertex meta, Vertex composite) {
		return index(metaComposites, meta, composite);
	}

	@Override
	public Vertex indexBySuper(Vertex superVertex, Vertex composite) {
		return index(superComposites, superVertex, composite);
	}

	private static Vertex index(Dependencies<DependenciesEntry<Vertex>> multimap, Vertex index, Vertex composite) {
		for (DependenciesEntry<Vertex> entry : multimap)
			if (index.equals(entry.getKey()))
				return entry.getValue().set(composite);

		Dependencies<Vertex> dependencies = new DependenciesImpl<Vertex>();
		Vertex result = dependencies.set(composite);
		multimap.set(new DependenciesEntry<Vertex>(index, dependencies));
		return result;
	}

	private static boolean unIndex(Dependencies<DependenciesEntry<Vertex>> multimap, Vertex index, Vertex composite) {
		for (DependenciesEntry<Vertex> entry : multimap)
			if (index.equals(entry.getKey()))
				return entry.getValue().remove(composite);
		return false;
	}

	@Override
	public boolean unIndexByMeta(Vertex meta, Vertex composite) {
		return unIndex(metaComposites, meta, composite);
	}

	@Override
	public boolean unIndexBySuper(Vertex superVertex, Vertex composite) {
		return unIndex(superComposites, superVertex, composite);
	}

	public Vertex index(Dependencies<Vertex> dependencies, Vertex dependency) {
		return dependencies.set(dependency);
	}

	public boolean unIndex(Dependencies<Vertex> dependencies, Vertex dependency) {
		return dependencies.remove(dependency);
	}

	@Override
	public Vertex indexInstance(Vertex instance) {
		return index(instances, instance);
	}

	@Override
	public Vertex indexInheriting(Vertex inheriting) {
		return index(inheritings, inheriting);
	}

	@Override
	public boolean unIndexInstance(Vertex instance) {
		return unIndex(instances, instance);
	}

	@Override
	public boolean unIndexInheriting(Vertex inheriting) {
		return unIndex(inheritings, inheriting);
	}

}
