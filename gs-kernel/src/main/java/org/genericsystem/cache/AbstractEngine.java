package org.genericsystem.cache;

import java.io.Serializable;

import org.genericsystem.cache.Cache.ContextEventListener;
import org.genericsystem.kernel.Generic;
import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;

public abstract class AbstractEngine extends Root {

	protected final GarbageCollector garbageCollector = new GarbageCollector(this);

	public AbstractEngine(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public AbstractEngine(Serializable engineValue, Class<?>... userClasses) {
		this(engineValue, null, userClasses);
	}

	public AbstractEngine(Serializable engineValue, String persistentDirectoryPath, Class<?>... userClasses) {
		super(engineValue, persistentDirectoryPath, userClasses);
		// garbageCollector.startScheduler();
	}

	public Cache newCache() {
		return new Cache(this);
	}

	@Override
	protected void flushContext() {
		getCurrentCache().flush();
	}

	@Override
	protected void shiftContext() {
		getCurrentCache().pickNewTs();
	}

	public Cache newCache(ContextEventListener<Generic> listener) {
		return new Cache(new Transaction(this), listener);
	}

	protected abstract Cache start(Cache cache);

	protected abstract void stop(Cache cache);

	@Override
	abstract public Cache getCurrentCache();

	GarbageCollector getGarbageCollector() {
		return garbageCollector;
	}

}
