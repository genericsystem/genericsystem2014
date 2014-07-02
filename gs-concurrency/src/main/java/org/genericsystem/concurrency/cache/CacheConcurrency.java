package org.genericsystem.concurrency.cache;

import org.genericsystem.cache.Cache;
import org.genericsystem.concurrency.generic.EngineServiceConcurrency;
import org.genericsystem.concurrency.generic.GenericServiceConcurrency;

public class CacheConcurrency<T extends GenericServiceConcurrency<T>> extends Cache<T> implements ContextConcurrency<T> {

	public CacheConcurrency(EngineServiceConcurrency<T> engine) {
		this(new TransactionConcurrency<T>(engine));
	}

	public CacheConcurrency(ContextConcurrency<T> subContext) {
		super(subContext);
	}

	@Override
	public long getTs() {
		return getSubContext().getTs();
	}

	@Override
	public ContextConcurrency<T> getSubContext() {
		return (ContextConcurrency<T>) super.getSubContext();
	}

	@Override
	public CacheConcurrency<T> mountNewCache() {
		return (CacheConcurrency<T>) super.mountNewCache();
	}

	@Override
	public EngineServiceConcurrency<T> getEngine() {
		return (EngineServiceConcurrency<T>) subContext.getEngine();
	}

}
