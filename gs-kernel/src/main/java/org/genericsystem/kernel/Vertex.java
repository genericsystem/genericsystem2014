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
	public Dependencies<Vertex> getInstances() {
		return instances;
	}

	@Override
	public Dependencies<Vertex> getInheritings() {
		return inheritings;
	}

	@Override
	public Snapshot<Vertex> getComposites() {
		// assert false;
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
	public Vertex indexByMeta(Vertex meta, Vertex component) {
		for (DependenciesEntry<Vertex> entry : metaComposites)
			if (meta.equals(entry.getKey()))
				return entry.getValue().set(component);

		Dependencies<Vertex> dependencies = new DependenciesImpl<Vertex>();
		Vertex result = dependencies.set(component);
		metaComposites.set(new DependenciesEntry<Vertex>(meta, dependencies));
		return result;
	}

	@Override
	public Vertex indexBySuper(Vertex superVertex, Vertex component) {

		for (DependenciesEntry<Vertex> entry : superComposites)
			if (superVertex.equals(entry.getKey()))
				return entry.getValue().set(component);

		Dependencies<Vertex> dependencies = new DependenciesImpl<Vertex>();
		Vertex result = dependencies.set(component);// Add or Set
		superComposites.set(new DependenciesEntry<Vertex>(superVertex, dependencies));

		return result;
	}

	@Override
	public boolean unIndexByMeta(Vertex meta, Vertex component) {
		for (DependenciesEntry<Vertex> entry : metaComposites)
			if (meta.equals(entry.getKey()))
				return entry.getValue().remove(component);
		return false;
	}

	@Override
	public boolean unIndexBySuper(Vertex superVertex, Vertex component) {
		for (DependenciesEntry<Vertex> entry : superComposites)
			if (superVertex.equals(entry.getKey()))
				return entry.getValue().remove(component);
		return false;
	}

}
