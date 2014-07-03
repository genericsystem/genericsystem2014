package org.genericsystem.concurrency.vertex;

import java.util.Iterator;
import java.util.function.Supplier;

import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.Dependencies.DependenciesEntry;
import org.genericsystem.kernel.Vertex;

public class VertexConcurrency extends Vertex implements VertexServiceConcurrency<Vertex> {

	// TODO KK DEBUT KK cf RootConcurrency
	private LifeManager lifeManager;

	void restore(Long designTs, long birthTs, long lastReadTs, long deathTs) {
		lifeManager = buildLifeManager(designTs, birthTs, lastReadTs, deathTs);
	}

	@Override
	public LifeManager getLifeManager() {
		return lifeManager;
	}

	public boolean isAlive(long ts) {
		return lifeManager.isAlive(ts);
	}

	// TODO KK FIN KK

	@Override
	public VertexConcurrency buildInstance() {
		VertexConcurrency vertexConcurrency = new VertexConcurrency();
		vertexConcurrency.lifeManager = buildLifeManager();
		return vertexConcurrency;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U extends Vertex> Dependencies<U> buildDependencies(Supplier<Iterator<Vertex>> subDependenciesSupplier) {
		return (Dependencies<U>) new AbstractDependenciesConcurrency() {

			@Override
			public LifeManager getLifeManager() {
				return lifeManager;
			}
		};
	}

	@Override
	public CompositesDependencies<Vertex> buildCompositeDependencies(Supplier<Iterator<DependenciesEntry<Vertex>>> subDependenciesSupplier) {
		class CompositesDependenciesImpl<E> implements CompositesDependencies<E> {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			private final Dependencies<DependenciesEntry<E>> delegate = (Dependencies<DependenciesEntry<E>>) buildDependencies((Supplier) subDependenciesSupplier);

			@Override
			public boolean remove(DependenciesEntry<E> vertex) {
				return delegate.remove(vertex);
			}

			@Override
			public void add(DependenciesEntry<E> vertex) {
				delegate.add(vertex);
			}

			@Override
			public Iterator<DependenciesEntry<E>> iterator() {
				return delegate.iterator();
			}

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Dependencies<E> buildDependencies(Supplier<Iterator<E>> supplier) {
				return (Dependencies<E>) VertexConcurrency.super.buildDependencies((Supplier) supplier);
			}
		}
		return new CompositesDependenciesImpl<Vertex>();
	}
}
