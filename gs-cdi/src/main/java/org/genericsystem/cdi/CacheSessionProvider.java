package org.genericsystem.cdi;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import org.genericsystem.concurrency.Cache;

@SessionScoped
public class CacheSessionProvider implements Serializable {

	private static final long serialVersionUID = 5201003234496546928L;

	@Inject
	private transient org.genericsystem.concurrency.Engine engine;

	private transient Cache currentCache;

	@PostConstruct
	public void init() {
		currentCache = engine.newCache();
	}

	public void mountAndStartNewCache() {
		currentCache = currentCache.mountAndStartNewCache();
	}

	public void flushAndUnmount() {
		currentCache = (Cache) currentCache.flushAndUnmount();
	}

	public void clearAndUnmount() {
		currentCache = (Cache) currentCache.clearAndUnmount();
	}

	public Cache getCurrentCache() {
		return currentCache;
	}

	@PreDestroy
	public void preDestroy() {
		currentCache.stop();
		currentCache = null;
	}

}
