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
}
