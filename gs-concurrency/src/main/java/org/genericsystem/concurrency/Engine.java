package org.genericsystem.concurrency;

import java.io.Serializable;

import org.genericsystem.cache.Generic;
import org.genericsystem.concurrency.Cache.ContextEventListener;

public class Engine extends org.genericsystem.cache.Engine {

	protected final ThreadLocal<Cache> cacheLocal = new ThreadLocal<>();

	public Engine(Class<?>... userClasses) {
		super(userClasses);
	}

	public Engine(Serializable engineValue, Class<?>... userClasses) {
		super(engineValue, userClasses);
	}

	public Engine(Serializable engineValue, String persistentDirectoryPath, Class<?>... userClasses) {
		super(engineValue, persistentDirectoryPath, userClasses);
	}

	@Override
	public Cache newCache() {
		return new Cache(new Transaction((Engine) getRoot()));
	}

	public Cache newCache(ContextEventListener<Generic> listener) {
		return new Cache(new Transaction((Engine) getRoot()), listener);
	}

	@Override
	public Cache getCurrentCache() {
		return (Cache) super.getCurrentCache();
	}
}
