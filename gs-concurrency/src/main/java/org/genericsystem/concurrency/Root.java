package org.genericsystem.concurrency;

import java.io.Serializable;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.SystemCache;
import org.genericsystem.kernel.services.ApiService;

public class Root extends Vertex implements RootService<Vertex, Root> {

	private final TsGenerator generator = new TsGenerator();

	private final SystemCache<Vertex> systemCache = new SystemCache<>(this);
	private final EngineService<?, ?, Vertex, Root> engine;

	private final GarbageCollector<Vertex, Root> garbageCollector;

	Root(EngineService<?, ?, Vertex, Root> engine, Serializable value, Class<?>... userClasses) {
		this.engine = engine;
		long ts = pickNewTs();
		restore(ts, 0L, 0L, Long.MAX_VALUE);
		garbageCollector = new GarbageCollector<>(this);
	}

	// public static class RootFactory {
	// public static Root buildRoot(EngineService<?, ?, Vertex, Root> engine, Serializable value, Class<?>... userClasses) {
	// return new Root(engine, Statics.ENGINE_VALUE, userClasses);
	// }
	//
	// public static Root buildAndInitRoot(EngineService<?, ?, Vertex, Root> engine, Serializable value, Class<?>... userClasses) {
	// return buildRoot(engine, value, userClasses).init(value, userClasses);
	// }
	// }

	protected Root init(Serializable value, Class<?>... userClasses) {
		init(false, null, Collections.emptyList(), value, Collections.emptyList());
		systemCache.init(userClasses);
		return this;
	}

	@Override
	public Vertex find(Class<?> clazz) {
		return systemCache.get(clazz);
	}

	@Override
	public Root getRoot() {
		return RootService.super.getRoot();
	}

	@Override
	public Root getAlive() {
		return (Root) RootService.super.getAlive();
	}

	@Override
	public boolean equiv(ApiService<? extends ApiService<?, ?>, ?> service) {
		return RootService.super.equiv(service);
	}

	@Override
	public boolean isRoot() {
		return RootService.super.isRoot();
	}

	@Override
	public long pickNewTs() {
		return generator.pickNewTs();
	}

	@Override
	public EngineService<?, ?, Vertex, Root> getEngine() {
		return engine;
	}

	@Override
	public GarbageCollector<Vertex, Root> getGarbageCollector() {
		return garbageCollector;
	}

	static class TsGenerator {
		private final long startTime = System.currentTimeMillis() * Statics.MILLI_TO_NANOSECONDS - System.nanoTime();
		private final AtomicLong lastTime = new AtomicLong(0L);

		long pickNewTs() {
			long nanoTs;
			long current;
			for (;;) {
				nanoTs = startTime + System.nanoTime();
				current = lastTime.get();
				if (nanoTs - current > 0)
					if (lastTime.compareAndSet(current, nanoTs))
						return nanoTs;
			}
		}
	}

}
