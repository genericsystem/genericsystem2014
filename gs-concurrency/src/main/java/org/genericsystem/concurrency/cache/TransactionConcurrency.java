package org.genericsystem.concurrency.cache;

import java.util.Iterator;
import java.util.function.Supplier;

import org.genericsystem.cache.Transaction;
import org.genericsystem.concurrency.generic.EngineServiceConcurrency;
import org.genericsystem.concurrency.generic.GenericServiceConcurrency;
import org.genericsystem.concurrency.vertex.RootConcurrency;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;

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
		return new Dependencies<T>() {

			@Override
			public Iterator<T> iterator() {
				return generic.unwrap().getInheritings().project(generic::wrap).iterator();
			}

			@Override
			public boolean remove(T vertex) {
				assert false;
				return false;
			}

			@Override
			public void add(T vertex) {
				assert false;
			}
		};
	}

	@Override
	public Dependencies<T> getInstances(T generic) {
		return new Dependencies<T>() {

			@Override
			public Iterator<T> iterator() {
				return generic.unwrap().getInstances().project(generic::wrap).iterator();
			}

			@Override
			public boolean remove(T vertex) {
				assert false;
				return false;
			}

			@Override
			public void add(T vertex) {
				assert false;
			}
		};
	}

	@Override
	public CompositesDependencies<T> getMetaComposites(T generic) {
		return new CompositesDependencies<T>() {

			@Override
			public boolean remove(DependenciesEntry<T> vertex) {
				assert false;
				return false;
			}

			@Override
			public void add(DependenciesEntry<T> vertex) {
				assert false;
			}

			@Override
			public Iterator<DependenciesEntry<T>> iterator() {
				return generic.unwrap().getMetaComposites().stream().map(x -> buildEntry(generic.wrap(x.getKey()), generic.buildDependencies(() -> x.getValue().stream().map(generic::wrap).iterator()))).iterator();
			}

			@Override
			public Dependencies<T> buildDependencies(Supplier<Iterator<T>> supplier) {
				assert false;
				return null;
			}
		};
	}

	@Override
	public CompositesDependencies<T> getSuperComposites(T generic) {
		return new CompositesDependencies<T>() {

			@Override
			public boolean remove(DependenciesEntry<T> vertex) {
				assert false;
				return false;
			}

			@Override
			public void add(DependenciesEntry<T> vertex) {
				assert false;
			}

			@Override
			public Iterator<DependenciesEntry<T>> iterator() {
				return generic.unwrap().getSuperComposites().stream().map(x -> buildEntry(generic.wrap(x.getKey()), generic.buildDependencies(() -> x.getValue().stream().map(generic::wrap).iterator()))).iterator();
			}

			@Override
			public Dependencies<T> buildDependencies(Supplier<Iterator<T>> supplier) {
				assert false;
				return null;
			}
		};
	}

}
