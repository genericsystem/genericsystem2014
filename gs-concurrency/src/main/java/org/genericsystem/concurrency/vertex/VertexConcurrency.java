package org.genericsystem.concurrency.vertex;

import java.util.Iterator;
import java.util.function.Supplier;

import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.Dependencies.DependenciesEntry;
import org.genericsystem.kernel.ExtendedSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VertexConcurrency extends ExtendedSignature<VertexConcurrency> implements VertexServiceConcurrency<VertexConcurrency, RootConcurrency> {

	protected static Logger log = LoggerFactory.getLogger(VertexConcurrency.class);

	private final Dependencies<VertexConcurrency> instances = buildDependencies(null);
	private final Dependencies<VertexConcurrency> inheritings = buildDependencies(null);
	private final CompositesDependencies<VertexConcurrency> superComposites = buildCompositeDependencies(null);
	private final CompositesDependencies<VertexConcurrency> metaComposites = buildCompositeDependencies(null);

	private LifeManager lifeManager;

	void restore(Long designTs, long birthTs, long lastReadTs, long deathTs) {
		lifeManager = new LifeManager(designTs == null ? ((RootConcurrency) getRoot()).pickNewTs() : designTs, birthTs, lastReadTs, deathTs);
	}

	@Override
	public VertexConcurrency buildInstance() {
		return new VertexConcurrency();
	}

	@Override
	public Dependencies<VertexConcurrency> getInstances() {
		return instances;
	}

	@Override
	public Dependencies<VertexConcurrency> getInheritings() {
		return inheritings;
	}

	@Override
	public CompositesDependencies<VertexConcurrency> getMetaComposites() {
		return metaComposites;
	}

	@Override
	public CompositesDependencies<VertexConcurrency> getSuperComposites() {
		return superComposites;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U extends VertexConcurrency> Dependencies<U> buildDependencies(Supplier<Iterator<VertexConcurrency>> subDependenciesSupplier) {
		return (Dependencies<U>) new AbstractDependenciesConcurrency() {

			@Override
			public LifeManager getLifeManager() {
				return lifeManager;
			}
		};
	}

	@Override
	public CompositesDependencies<VertexConcurrency> buildCompositeDependencies(Supplier<Iterator<DependenciesEntry<VertexConcurrency>>> subDependenciesSupplier) {
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

			@Override
			public Dependencies<E> buildDependencies(Supplier<Iterator<E>> supplier) {
				return (Dependencies<E>) VertexConcurrency.super.buildDependencies((Supplier) supplier);
			}
		}
		return new CompositesDependenciesImpl<VertexConcurrency>();
	}

	public LifeManager getLifeManager() {
		return lifeManager;
	}

	public boolean isAlive(long ts) {
		return lifeManager.isAlive(ts);
	}
}
