package org.genericsystem.concurrency.cache;

import java.util.Collections;

import org.genericsystem.cache.Transaction;
import org.genericsystem.concurrency.generic.EngineServiceConcurrency;
import org.genericsystem.concurrency.generic.GenericServiceConcurrency;
import org.genericsystem.concurrency.vertex.RootConcurrency;
import org.genericsystem.kernel.Dependencies.CompositesSnapshot;
import org.genericsystem.kernel.Dependencies.DependenciesEntry;
import org.genericsystem.kernel.Snapshot;

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
	public Snapshot<T> getInheritings(T generic) {
		return () -> generic.getVertex() != null ? generic.unwrap().getInheritings().project(generic::wrap).iterator() : Collections.emptyIterator();
	}

	@Override
	public Snapshot<T> getInstances(T generic) {
		return () -> generic.getVertex() != null ? generic.unwrap().getInstances().project(generic::wrap).iterator() : Collections.emptyIterator();
	}

	@Override
	public CompositesSnapshot<T> getMetaComposites(T generic) {
		return () -> generic.getVertex() != null ? generic.unwrap().getMetaComposites().stream().map(x -> new DependenciesEntry<>(generic.wrap(x.getKey()), generic.buildDependencies(() -> x.getValue().stream().map(generic::wrap).iterator()))).iterator()
				: Collections.emptyIterator();
	}

	@Override
	public CompositesSnapshot<T> getSuperComposites(T generic) {
		return () -> generic.getVertex() != null ? generic.unwrap().getSuperComposites().stream().map(x -> new DependenciesEntry<>(generic.wrap(x.getKey()), generic.buildDependencies(() -> x.getValue().stream().map(generic::wrap).iterator()))).iterator()
				: Collections.emptyIterator();
	}

}
