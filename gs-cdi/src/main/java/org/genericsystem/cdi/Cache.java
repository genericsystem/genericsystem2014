package org.genericsystem.cdi;

import org.genericsystem.concurrency.Engine;
import org.genericsystem.concurrency.Generic;
import org.genericsystem.concurrency.Root;
import org.genericsystem.concurrency.Transaction;
import org.genericsystem.concurrency.Vertex;

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
		return (Cache) getEngine().start(this);
	}
}
