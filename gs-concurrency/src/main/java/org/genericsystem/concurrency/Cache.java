package org.genericsystem.concurrency;

import org.genericsystem.cache.AbstractContext;

public class Cache<T extends AbstractGeneric<T, U, V, W>, U extends EngineService<T, U, V, W>, V extends AbstractVertex<V, W>, W extends RootService<V, W>> extends org.genericsystem.cache.Cache<T, U, V, W> {

	protected Cache(U engine) {
		this(new Transaction<T, U, V, W>(engine));
	}

	protected Cache(org.genericsystem.cache.AbstractContext<T, U, V, W> subContext) {
		super(subContext);
	}

	public long getTs() {
		AbstractContext<T, U, V, W> context = getSubContext();
		return context instanceof Cache ? ((Cache<T, U, V, W>) context).getTs() : ((Transaction<T, U, V, W>) context).getTs();
	}

	@Override
	public Cache<T, U, V, W> mountNewCache() {
		return (Cache<T, U, V, W>) super.mountNewCache();
	}

	@Override
	public Cache<T, U, V, W> flushAndUnmount() {
		flush();
		return getSubContext() instanceof Cache ? ((Cache<T, U, V, W>) getSubContext()).start() : this;
	}

	@Override
	public Cache<T, U, V, W> discardAndUnmount() {
		clear();
		return getSubContext() instanceof Cache ? ((Cache<T, U, V, W>) getSubContext()).start() : this;
	}

	@Override
	public Cache<T, U, V, W> start() {
		return getEngine().start(this);
	}
}
