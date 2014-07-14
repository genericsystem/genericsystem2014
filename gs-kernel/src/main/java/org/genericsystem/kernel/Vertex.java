package org.genericsystem.kernel;

import java.util.Collections;
import org.genericsystem.kernel.Dependencies.DependenciesEntry;
import org.genericsystem.kernel.exceptions.NotFoundException;
import org.genericsystem.kernel.services.VertexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vertex extends AbstractVertex<Vertex> implements VertexService<Vertex> {

	protected static Logger log = LoggerFactory.getLogger(Vertex.class);

	private final Dependencies<Vertex> instances = buildDependencies();
	private final Dependencies<Vertex> inheritings = buildDependencies();
	private final Dependencies<DependenciesEntry<Vertex>> superComposites = buildDependencies();
	private final Dependencies<DependenciesEntry<Vertex>> metaComposites = buildDependencies();

	@Override
	public Vertex newT() {
		return new Vertex();
	}

	@Override
	public Vertex[] newTArray(int dim) {
		return new Vertex[dim];
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

	private Vertex indexByMeta(Vertex meta, Vertex composite) {
		return index(metaComposites, meta, composite);
	}

	private Vertex indexBySuper(Vertex superVertex, Vertex composite) {
		return index(superComposites, superVertex, composite);
	}

	private static Vertex index(Dependencies<DependenciesEntry<Vertex>> multimap, Vertex index, Vertex composite) {
		for (DependenciesEntry<Vertex> entry : multimap)
			if (index.equals(entry.getKey()))
				return entry.getValue().set(composite);

		Dependencies<Vertex> dependencies = composite.buildDependencies();
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

	private boolean unIndexByMeta(Vertex meta, Vertex composite) {
		return unIndex(metaComposites, meta, composite);
	}

	private boolean unIndexBySuper(Vertex superVertex, Vertex composite) {
		return unIndex(superComposites, superVertex, composite);
	}

	private static Vertex index(Dependencies<Vertex> dependencies, Vertex dependency) {
		return dependencies.set(dependency);
	}

	private static boolean unIndex(Dependencies<Vertex> dependencies, Vertex dependency) {
		return dependencies.remove(dependency);
	}

	private Vertex indexInstance(Vertex instance) {
		return index(instances, instance);
	}

	private Vertex indexInheriting(Vertex inheriting) {
		return index(inheritings, inheriting);
	}

	private boolean unIndexInstance(Vertex instance) {
		return unIndex(instances, instance);
	}

	private boolean unIndexInheriting(Vertex inheriting) {
		return unIndex(inheritings, inheriting);
	}

	@Override
	public Vertex plug() {
		Vertex result = getMeta().indexInstance(this);
		getSupersStream().forEach(superGeneric -> superGeneric.indexInheriting(this));
		getComponentsStream().forEach(component -> component.indexByMeta(getMeta(), this));
		getSupersStream().forEach(superGeneric -> getComponentsStream().forEach(component -> component.indexBySuper(superGeneric, this)));
		return result;
	}

	@Override
	public boolean unplug() {
		boolean result = getMeta().unIndexInstance(this);
		if (!result)
			rollbackAndThrowException(new NotFoundException(this.info()));
		getSupersStream().forEach(superGeneric -> superGeneric.unIndexInheriting(this));
		getComponentsStream().forEach(component -> component.unIndexByMeta(getMeta(), this));
		getSupersStream().forEach(superGeneric -> getComponentsStream().forEach(component -> component.unIndexBySuper(superGeneric, this)));
		return result;
	}

}
