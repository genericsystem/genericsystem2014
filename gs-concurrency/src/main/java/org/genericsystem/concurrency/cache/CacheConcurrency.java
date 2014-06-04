package org.genericsystem.concurrency.cache;

import org.genericsystem.cache.AbstractContext;
import org.genericsystem.cache.Cache;
import org.genericsystem.concurrency.generic.EngineServiceConcurrency;
import org.genericsystem.concurrency.generic.GenericServiceConcurrency;

public class CacheConcurrency<T extends GenericServiceConcurrency<T>> extends Cache<T> {

	public CacheConcurrency(EngineServiceConcurrency<T> engine) {
		this(new Transaction<T>(engine));
	}

	public CacheConcurrency(AbstractContext<T> subContext) {
		super(subContext);
	}

}
