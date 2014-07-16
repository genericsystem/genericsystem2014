package org.genericsystem.concurrency;

public class Cache<T extends AbstractGeneric<T>> extends org.genericsystem.cache.Cache<T> implements Context<T> {

	public Cache(EngineService<T> engine) {
		this(new Transaction<T>(engine));
	}

	public Cache(Context<T> subContext) {
		super(subContext);
	}

	@Override
	public long getTs() {
		return getSubContext().getTs();
	}

	@Override
	public Context<T> getSubContext() {
		return (Context<T>) super.getSubContext();
	}

	@Override
	public Cache<T> mountNewCache() {
		return (Cache<T>) super.mountNewCache();
	}

	@Override
	public EngineService<T> getEngine() {
		return (EngineService<T>) subContext.getEngine();
	}

}
