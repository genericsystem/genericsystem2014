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

public class TransactionConcurrency<T extends GenericServiceConcurrency<T, U>, U extends EngineServiceConcurrency<T, U>> extends Transaction<T, U> implements ContextConcurrency<T, U> {

	private transient long ts;

	public TransactionConcurrency(EngineServiceConcurrency<T, U> engine) {
		this(((RootConcurrency) engine.getRoot().getVertex()).pickNewTs(), engine);
	}

	public TransactionConcurrency(long ts, EngineServiceConcurrency<T, U> engine) {
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
		return aliveAdapter(generic.unwrap().getInheritings().project(generic::wrap, org.genericsystem.impl.GenericService::unwrap, t -> true));
	}

	@Override
	public Dependencies<T> getInstances(T generic) {
		return aliveAdapter(generic.unwrap().getInstances().project(generic::wrap, org.genericsystem.impl.GenericService::unwrap, t -> true));
	}

	@Override
	public CompositesDependencies<T> getMetaComposites(T generic) {
		return aliveAdapter(generic.unwrap().getMetaComposites().projectComposites(generic::wrap, org.genericsystem.impl.GenericService::unwrap, t -> true));
	}

	@Override
	public CompositesDependencies<T> getSuperComposites(T generic) {
		return aliveAdapter(generic.unwrap().getSuperComposites().projectComposites(generic::wrap, org.genericsystem.impl.GenericService::unwrap, t -> true));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Dependencies<T> aliveAdapter(Dependencies<T> dependenciesToFilter) {
		return (Dependencies) new Dependencies<VertexConcurrency>() {

			private final AbstractDependenciesConcurrency dependencies = (AbstractDependenciesConcurrency) dependenciesToFilter;

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
				return dependencies.remove(vertex);
			}

			@Override
			public void add(VertexConcurrency vertex) {
				dependencies.add(vertex);
			}
		};
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private CompositesDependencies<T> aliveAdapter(CompositesDependencies<T> dependenciesToFilter) {
		return (CompositesDependencies) new CompositesDependencies<VertexConcurrency>() {

			private final CompositesDependencies dependencies = dependenciesToFilter;

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
