package org.genericsystem.kernel;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.genericsystem.api.defaults.DefaultRoot;

public class GarbageCollector<T extends AbstractVertex<T>> extends LinkedHashSet<T> {

	private static final long serialVersionUID = -2021341943811568201L;
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	private final DefaultRoot<T> root;

	public GarbageCollector(DefaultRoot<T> root) {
		this.root = root;
	}

	public void startScheduler() {
		scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				runGarbage(Statics.LIFE_TIMEOUT);
			}
		}, Statics.GARBAGE_INITIAL_DELAY, Statics.GARBAGE_PERIOD, TimeUnit.MILLISECONDS);
	}

	public void runGarbage(long timeOut) {
		long ts = root.pickNewTs();
		synchronized (root) {
			Iterator<T> iterator = GarbageCollector.this.iterator();
			while (iterator.hasNext()) {
				T generic = iterator.next();
				if (ts - generic.getLifeManager().getDeathTs() >= timeOut) {
					generic.remove();
					iterator.remove();
				}
			}
		}
	}
}
