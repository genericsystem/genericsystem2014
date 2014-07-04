package org.genericsystem.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.VertexService;
import org.genericsystem.kernel.iterator.AbstractProjectionIterator;

public interface GenericService<T extends GenericService<T>> extends VertexService<T> {

	default List<T> wrap(Stream<Vertex> stream) {
		return stream.map(this::wrap).collect(Collectors.toList());
	}

	static List<Vertex> unwrap(Stream<? extends GenericService<?>> stream) {
		return stream.map(GenericService::unwrap).collect(Collectors.toList());
	}

	default T wrap(Vertex vertex) {
		if (vertex.isRoot())
			return getRoot();
		Vertex alive = vertex.getAlive();
		T meta = wrap(alive.getMeta());
		return meta.buildInstance().init(meta, wrap(alive.getSupersStream()), alive.getValue(), wrap(alive.getComponentsStream()));
	}

	default Vertex unwrap() {
		Vertex alive = getVertex();
		if (alive != null)
			return alive;
		alive = getMeta().unwrap();
		if (!alive.isAlive())
			throw new IllegalStateException("Not Alive" + alive.info() + alive.getMeta().getInstances());
		return alive.buildInstance().init(alive, unwrap(getSupersStream()), getValue(), unwrap(getComponentsStream()));
	}

	default Vertex getVertex() {
		Vertex pluggedMeta = getMeta().getVertex();
		if (pluggedMeta == null)
			return null;
		for (Vertex instance : pluggedMeta.getInstances())
			if (equiv(instance))
				return instance;
		return null;
	}

	@Override
	default Dependencies<T> getInstances() {
		return wrapDependencies(getVertex().getInstances());
	}

	@Override
	default Dependencies<T> getInheritings() {
		return wrapDependencies(getVertex().getInheritings());
	}

	default Dependencies<T> wrapDependencies(Dependencies<Vertex> dependencies) {
		return new Dependencies<T>() {
			@Override
			public Iterator<T> iterator() {
				return new AbstractProjectionIterator<Vertex, T>(dependencies.iterator()) {
					@Override
					public T project(Vertex t) {
						return wrap(t);
					}
				};
			}

			@Override
			public void add(T generic) {
				dependencies.add(generic.unwrap());
			}

			@Override
			public boolean remove(T generic) {
				return dependencies.remove(generic.unwrap());
			}
		};
	}

	default Dependencies<Vertex> unwrapDependencies(Dependencies<T> dependencies) {
		return new Dependencies<Vertex>() {
			@Override
			public Iterator<Vertex> iterator() {
				return new AbstractProjectionIterator<T, Vertex>(dependencies.iterator()) {
					@Override
					public Vertex project(T t) {
						return t.unwrap();
					}
				};
			}

			@Override
			public void add(Vertex vertex) {
				dependencies.add(wrap(vertex));
			}

			@Override
			public boolean remove(Vertex vertex) {
				return dependencies.remove(wrap(vertex));
			}
		};
	}

	@Override
	default CompositesDependencies<T> getMetaComposites() {
		return projectComposites(getVertex().getMetaComposites());
	}

	@Override
	default CompositesDependencies<T> getSuperComposites() {
		return projectComposites(getVertex().getSuperComposites());
	}

	default public CompositesDependencies<T> projectComposites(CompositesDependencies<Vertex> dependencies) {
		return new CompositesDependencies<T>() {

			@Override
			public boolean remove(DependenciesEntry<T> entry) {
				return dependencies.remove(new DependenciesEntry<Vertex>(entry.getKey().unwrap(), unwrapDependencies(entry.getValue())));
			}

			@Override
			public void add(DependenciesEntry<T> entry) {
				dependencies.add(new DependenciesEntry<Vertex>(entry.getKey().unwrap(), unwrapDependencies(entry.getValue())));
			}

			@Override
			public Iterator<DependenciesEntry<T>> iterator() {
				return new AbstractProjectionIterator<DependenciesEntry<Vertex>, DependenciesEntry<T>>(dependencies.iterator()) {
					@Override
					public DependenciesEntry<T> project(DependenciesEntry<Vertex> vertexEntry) {
						return new DependenciesEntry<>(wrap(vertexEntry.getKey()), wrapDependencies(vertexEntry.getValue()));
					}
				};
			}

			@Override
			public Dependencies<T> buildDependencies(Supplier<Iterator<T>> supplier) {
				return GenericService.this.buildDependencies(supplier);
			}
		};
	}

	@Override
	default T getInstance(Serializable value, @SuppressWarnings("unchecked") T... components) {
		Vertex vertex = getVertex();
		if (vertex == null)
			return null;
		vertex = vertex.getInstance(value, Arrays.asList(components).stream().map(GenericService::unwrap).collect(Collectors.toList()).toArray(new Vertex[components.length]));
		if (vertex == null)
			return null;
		return wrap(vertex);
	}

	@Override
	default Snapshot<T> getMetaComposites(T meta) {
		Vertex vertex = getVertex();
		return vertex == null ? null : vertex.getMetaComposites(meta.getVertex()).project(this::wrap);
	}

	@Override
	default Snapshot<T> getSuperComposites(T superVertex) {
		Vertex vertex = getVertex();
		return vertex == null ? null : vertex.getSuperComposites(superVertex.getVertex()).project(this::wrap);
	}
}
