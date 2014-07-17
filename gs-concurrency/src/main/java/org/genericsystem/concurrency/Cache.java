package org.genericsystem.concurrency;

import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.services.RootService;

public class Cache<T extends AbstractGeneric<T, U, V, W>, U extends EngineService<T, U, V, W>, V extends AbstractVertex<V, W>, W extends RootService<V, W>> extends org.genericsystem.cache.Cache<T, U, V, W> implements Context<T, U, V, W> {

	public Cache(U engine) {
		this(new Transaction<T, U, V, W>(engine));
	}

	public Cache(org.genericsystem.cache.Context<T, U, V, W> subContext) {
		super(subContext);
	}

	@Override
	public long getTs() {
		return getSubContext().getTs();
	}

	@Override
	public Context<T, U, V, W> getSubContext() {
		return (Context<T, U, V, W>) super.getSubContext();
	}

	@Override
	public Cache<T, U, V, W> mountNewCache() {
		return (Cache<T, U, V, W>) super.mountNewCache();
	}

	@Override
	public U getEngine() {
		return subContext.getEngine();
	}

}
