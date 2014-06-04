package org.genericsystem.concurrency.cache;

import org.genericsystem.concurrency.generic.EngineServiceConcurrency;
import org.genericsystem.concurrency.generic.GenericServiceConcurrency;
import org.genericsystem.concurrency.vertex.RootConcurrency;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;

public class TransactionConcurrency<T extends GenericServiceConcurrency<T>> extends org.genericsystem.cache.Transaction<T> {

	private transient long ts;

	public TransactionConcurrency(EngineServiceConcurrency<T> engine) {
		this(((RootConcurrency) engine.getRoot().getVertex()).pickNewTs(), engine);
	}

	public TransactionConcurrency(long ts, EngineServiceConcurrency<T> engine) {
		super(engine);
		this.ts = ts;
	}

	public long getTs() {
		return ts;
	}

	@Override
	public boolean isAlive(T generic) {
		return generic.getLifeManager().isAlive(getTs());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Dependencies<T> getInheritings(T generic) {
		return (Dependencies<T>) generic.unwrap().getLifeManager().getEngineInheritings();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Dependencies<T> getInstances(T generic) {
		return (Dependencies<T>) generic.unwrap().getLifeManager().getEngineInstances();
	}

	@SuppressWarnings("unchecked")
	@Override
	public CompositesDependencies<T> getMetaComposites(T generic) {
		return (CompositesDependencies<T>) generic.unwrap().getLifeManager().getEngineMetaComposites();
	}

	@SuppressWarnings("unchecked")
	@Override
	public CompositesDependencies<T> getSuperComposites(T generic) {
		return (CompositesDependencies<T>) generic.unwrap().getLifeManager().getEngineSuperComposites();
	}
}
