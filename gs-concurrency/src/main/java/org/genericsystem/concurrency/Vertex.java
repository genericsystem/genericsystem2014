package org.genericsystem.concurrency;

import java.util.stream.Stream;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.DependenciesEntry;

public class Vertex extends AbstractVertex implements DefaultVertex {

	private final Dependencies<Vertex> instances = buildDependencies();
	private final Dependencies<Vertex> inheritings = buildDependencies();
	private final DependenciesMap<Vertex> superComponents = buildDependenciesMap();
	private final DependenciesMap<Vertex> metaComponents = buildDependenciesMap();

	@Override
	protected Dependencies<Vertex> getInstancesDependencies() {
		return instances;
	}

	@Override
	protected Dependencies<Vertex> getInheritingsDependencies() {
		return inheritings;
	}

	@Override
	protected DependenciesMap<Vertex> getMetaCompositesDependencies() {
		return metaComponents;
	}

	@Override
	protected DependenciesMap<Vertex> getSuperCompositesDependencies() {
		return superComponents;
	}

	@Override
	public Vertex newT() {
		return new Vertex().restore(getRoot().pickNewTs(), getRoot().getEngine().getCurrentCache().getTs(), 0L, Long.MAX_VALUE);
	}

	@Override
	public Vertex[] newTArray(int dim) {
		return new Vertex[dim];
	}

	@Override
	protected Dependencies<Vertex> buildDependencies() {
		return new AbstractDependencies<Vertex>() {

			@Override
			public LifeManager getLifeManager() {
				return lifeManager;
			}

			@Override
			public Stream<Vertex> get() {
				return get(getRoot().getEngine().getCurrentCache().getTs());
			}
		};
	}

	public static abstract class AbstractDependenciesMap<T> extends AbstractDependencies<DependenciesEntry<T>> implements DependenciesMap<T> {

	}

	@Override
	protected DependenciesMap<Vertex> buildDependenciesMap() {
		return new AbstractDependenciesMap<Vertex>() {
			@Override
			public Stream<DependenciesEntry<Vertex>> get() {
				return get(getRoot().getEngine().getCurrentCache().getTs());
			}

			@Override
			public LifeManager getLifeManager() {
				return lifeManager;
			}

		};
	}

	@Override
	public DefaultRoot getRoot() {
		return getMeta().getRoot();
	}
}
