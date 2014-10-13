package org.genericsystem.cdi;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import org.genericsystem.cache.Cache;
import org.genericsystem.cache.Engine;

@SessionScoped
public class CacheProvider implements Serializable {

	private static final long serialVersionUID = 5201003234496546928L;

	@Inject
	private transient Engine engine;

	private transient Cache currentCache;

	@PostConstruct
	public void init() {
		currentCache = engine.newCache();
	}

	public void mountNewCache() {
		currentCache = currentCache.mountNewCache();
	}

	public void flushCurrentCache() {
		currentCache = currentCache.flushAndUnmount();
	}

	public void discardCurrentCache() {
		currentCache = currentCache.clearAndUnmount();
	}

	@Produces
	public Cache getCurrentCache() {
		return currentCache;
	}

	public void setCurrentCache(Cache<?, ?, ?, ?> cache) {
		this.currentCache = cache;
	}

}
