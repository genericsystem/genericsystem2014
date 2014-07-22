package org.genericsystem.concurrency;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.genericsystem.kernel.Statics;

public class GarbageCollector<T extends AbstractGeneric<T, U, ?, ?>, U extends EngineService<T, U, ?, ?>> extends LinkedHashSet<T> {

	private static final long serialVersionUID = -2021341943811568201L;
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	private EngineService<T, U, ?, ?> engine;

	public GarbageCollector(EngineService<T, U, ?, ?> engine) {
		this.engine = engine;
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
		long ts = engine.pickNewTs();
		synchronized (engine) {
			Iterator<T> iterator = GarbageCollector.this.iterator();
			while (iterator.hasNext()) {
				T generic = iterator.next();
				if (ts - generic.getDeathTs() >= timeOut) {
					generic.unplug();
					iterator.remove();
				}
			}
		}
	}
}
