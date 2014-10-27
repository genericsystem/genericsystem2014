package org.genericsystem.cdi;

import org.genericsystem.concurrency.Transaction;

public class Cache extends org.genericsystem.concurrency.Cache<Generic, Engine, Vertex, Root> {

	protected Cache(Engine engine) {
		this(new Transaction<Generic, Engine, Vertex, Root>(engine));
	}

	protected Cache(org.genericsystem.cache.AbstractContext<Generic, Engine, Vertex, Root> subContext) {
		super(subContext);
	}

	// @Override
	// public Cache mountAndStartNewCache() {
	// return (Cache) super.mountAndStartNewCache();
	// }

	// @Override
	// public Cache flushAndUnmount() {
	// flush();
	// return getSubContext() instanceof Cache ? ((Cache) getSubContext()).start() : this;
	// }
	//
	// @Override
	// public Cache clearAndUnmount() {
	// clear();
	// return getSubContext() instanceof Cache ? ((Cache) getSubContext()).start() : this;
	// }

	@Override
	public Cache start() {
		return getEngine().start(this);
	}
}
