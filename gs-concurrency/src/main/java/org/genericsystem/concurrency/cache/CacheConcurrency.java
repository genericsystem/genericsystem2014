package org.genericsystem.concurrency.cache;

import org.genericsystem.cache.Cache;
import org.genericsystem.concurrency.generic.EngineServiceConcurrency;
import org.genericsystem.concurrency.generic.GenericServiceConcurrency;

public class CacheConcurrency<T extends GenericServiceConcurrency<T, U>, U extends EngineServiceConcurrency<T, U>> extends Cache<T, U> implements ContextConcurrency<T, U> {

	public CacheConcurrency(EngineServiceConcurrency<T, U> engine) {
		this(new TransactionConcurrency<T, U>(engine));
	}

	public CacheConcurrency(ContextConcurrency<T, U> subContext) {
		super(subContext);
	}

	@Override
	public long getTs() {
		return getSubContext().getTs();
	}

	@Override
	public ContextConcurrency<T, U> getSubContext() {
		return (ContextConcurrency<T, U>) super.getSubContext();
	}

	@Override
	public CacheConcurrency<T, U> mountNewCache() {
		return (CacheConcurrency<T, U>) super.mountNewCache();
	}

	@Override
	public EngineServiceConcurrency<T, U> getEngine() {
		return (EngineServiceConcurrency<T, U>) subContext.getEngine();
	}

}
