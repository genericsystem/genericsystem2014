package org.genericsystem.concurrency;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.genericsystem.kernel.Statics;

public class GarbageCollector<T extends AbstractVertex<T, U>, U extends RootService<T, U>> extends LinkedHashSet<T> {

	private static final long serialVersionUID = -2021341943811568201L;
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	private final RootService<T, U> root;

	public GarbageCollector(RootService<T, U> root) {
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
				T vertex = iterator.next();
				if (ts - vertex.getLifeManager().getDeathTs() >= timeOut) {
					vertex.unplug();
					iterator.remove();
				}
			}
		}
	}
}
