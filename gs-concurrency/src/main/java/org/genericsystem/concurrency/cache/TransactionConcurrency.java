package org.genericsystem.concurrency.cache;

import java.util.Iterator;
import java.util.function.Supplier;

import org.genericsystem.cache.Transaction;
import org.genericsystem.concurrency.generic.EngineServiceConcurrency;
import org.genericsystem.concurrency.generic.GenericServiceConcurrency;
import org.genericsystem.concurrency.vertex.AbstractDependenciesConcurrency;
import org.genericsystem.concurrency.vertex.RootConcurrency;
import org.genericsystem.concurrency.vertex.VertexConcurrency;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.iterator.AbstractFilterIterator;

public class TransactionConcurrency<T extends GenericServiceConcurrency<T>> extends Transaction<T> implements ContextConcurrency<T> {

	private transient long ts;

	public TransactionConcurrency(EngineServiceConcurrency<T> engine) {
		this(((RootConcurrency) engine.getRoot().getVertex()).pickNewTs(), engine);
	}

	public TransactionConcurrency(long ts, EngineServiceConcurrency<T> engine) {
		super(engine);
		this.ts = ts;
	}

	@Override
	public long getTs() {
		return ts;
	}

	@Override
	public boolean isAlive(T generic) {
		return generic.getLifeManager().isAlive(getTs());
	}

	@Override
	public Dependencies<T> getInheritings(T generic) {
		return aliveAdapter(generic.unwrap().getInheritings().project(generic::wrap, org.genericsystem.impl.GenericService::unwrap));
	}

	@Override
	public Dependencies<T> getInstances(T generic) {
		return aliveAdapter(generic.unwrap().getInstances().project(generic::wrap, org.genericsystem.impl.GenericService::unwrap));
	}

	@Override
	public CompositesDependencies<T> getMetaComposites(T generic) {
		return aliveAdapter(generic.unwrap().getMetaComposites().projectComposites(generic::wrap, org.genericsystem.impl.GenericService::unwrap));
	}

	@Override
	public CompositesDependencies<T> getSuperComposites(T generic) {
		return aliveAdapter(generic.unwrap().getSuperComposites().projectComposites(generic::wrap, org.genericsystem.impl.GenericService::unwrap));
	}

	@SuppressWarnings("unchecked")
	private Dependencies<T> aliveAdapter(Dependencies<T> dependenciesToFilter) {
		return (Dependencies<T>) new Dependencies<VertexConcurrency>() {

			private AbstractDependenciesConcurrency dependencies = (AbstractDependenciesConcurrency) dependenciesToFilter;

			@Override
			public Iterator<VertexConcurrency> iterator() {
				return new AbstractFilterIterator<VertexConcurrency>(dependencies.iterator(ts)) {
					@Override
					public boolean isSelected() {
						return next.isAlive(ts);
					}
				};
			}

			@Override
			public boolean remove(VertexConcurrency vertex) {
				return dependencies.remove((VertexConcurrency) vertex);
			}

			@Override
			public void add(VertexConcurrency vertex) {
				dependencies.add(vertex);
			}
		};
	}

	@SuppressWarnings("unchecked")
	private CompositesDependencies<T> aliveAdapter(CompositesDependencies<T> dependenciesToFilter) {
		return (CompositesDependencies<T>) new CompositesDependencies<VertexConcurrency>() {

			private CompositesDependencies<VertexConcurrency> dependencies = (org.genericsystem.kernel.Dependencies.CompositesDependencies<VertexConcurrency>) dependenciesToFilter;

			@Override
			public Iterator<org.genericsystem.kernel.Dependencies.DependenciesEntry<VertexConcurrency>> iterator() {
				return new AbstractFilterIterator<org.genericsystem.kernel.Dependencies.DependenciesEntry<VertexConcurrency>>(dependencies.iterator()) {
					@Override
					public boolean isSelected() {
						return next.getKey().isAlive(ts);
					}
				};
			}

			@Override
			public boolean remove(org.genericsystem.kernel.Dependencies.DependenciesEntry<VertexConcurrency> vertex) {
				return dependencies.remove(vertex);
			}

			@Override
			public void add(org.genericsystem.kernel.Dependencies.DependenciesEntry<VertexConcurrency> vertex) {
				dependencies.add(vertex);
			}

			@Override
			public Dependencies<VertexConcurrency> buildDependencies(Supplier<Iterator<VertexConcurrency>> supplier) {
				return dependencies.buildDependencies(supplier);
			}
		};
	}
}
