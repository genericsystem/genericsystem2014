package org.genericsystem.concurrency;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.genericsystem.kernel.Statics;

public class GarbageCollector extends LinkedHashSet<Vertex> {

	private static final long serialVersionUID = -2021341943811568201L;
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	private final DefaultRoot root;

	public GarbageCollector(DefaultRoot root) {
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
			Iterator<Vertex> iterator = GarbageCollector.this.iterator();
			while (iterator.hasNext()) {
				Vertex vertex = iterator.next();
				if (ts - vertex.getLifeManager().getDeathTs() >= timeOut) {
					vertex.remove();
					iterator.remove();
				}
			}
		}
	}
}
