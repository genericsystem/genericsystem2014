package org.genericsystem.concurrency.vertex;

import java.util.Iterator;
import java.util.function.Supplier;

import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.Dependencies.DependenciesEntry;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.VertexService;

public class VertexConcurrency extends Vertex implements VertexService<Vertex> {

	private LifeManager lifeManager;

	void restore(Long designTs, long birthTs, long lastReadTs, long deathTs) {
		lifeManager = new LifeManager(designTs == null ? ((RootConcurrency) getRoot()).pickNewTs() : designTs, birthTs, lastReadTs, deathTs);
	}

	@Override
	public VertexConcurrency buildInstance() {
		return new VertexConcurrency();
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
				return VertexConcurrency.super.buildDependencies((Supplier) supplier);
			}
		}
		return new CompositesDependenciesImpl<Vertex>();
	}

	public LifeManager getLifeManager() {
		return lifeManager;
	}

	public boolean isAlive(long ts) {
		return lifeManager.isAlive(ts);
	}
}
