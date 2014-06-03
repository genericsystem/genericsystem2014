package org.genericsystem.concurrency;

import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;

public class Transaction<T extends GenericServiceConcurrency<T>> extends org.genericsystem.cache.Transaction<T> {

	private transient long ts;

	public Transaction(T engine) {
		this(((RootConcurrency) engine.getRoot()).pickNewTs(), engine);
	}

	public Transaction(long ts, T engine) {
		super(engine);
		this.ts = ts;
	}

	public long getTs() {
		return ts;
	}

	@Override
	public boolean isAlive(T generic) {
		return ((GenericConcurrency) generic).isAlive(getTs());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Dependencies<T> getInheritings(T generic) {
		return (Dependencies<T>) ((GenericConcurrency) generic).unwrap().getLifeManager().engineInheritings;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Dependencies<T> getInstances(T generic) {
		return (Dependencies<T>) ((GenericConcurrency) generic).unwrap().getLifeManager().engineInstances;
	}

	@SuppressWarnings("unchecked")
	@Override
	public CompositesDependencies<T> getMetaComposites(T generic) {
		return (CompositesDependencies<T>) ((GenericConcurrency) generic).unwrap().getLifeManager().engineMetaComposites;
	}

	@SuppressWarnings("unchecked")
	@Override
	public CompositesDependencies<T> getSuperComposites(T generic) {
		return (CompositesDependencies<T>) ((GenericConcurrency) generic).unwrap().getLifeManager().engineSuperComposites;
	}
}
