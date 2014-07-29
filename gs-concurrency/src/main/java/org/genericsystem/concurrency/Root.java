package org.genericsystem.concurrency;

import java.io.Serializable;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.services.ApiService;

public class Root extends Vertex implements RootService<Vertex, Root> {

	private final TsGenerator generator = new TsGenerator();

	private final EngineService<?, ?, Vertex, Root> engine;

	private final GarbageCollector<Vertex, Root> garbageCollector;

	Root(EngineService<?, ?, Vertex, Root> engine, Serializable value) {
		init(false, null, Collections.emptyList(), value, Collections.emptyList());
		this.engine = engine;
		long ts = pickNewTs();
		restore(ts, 0L, 0L, Long.MAX_VALUE);
		garbageCollector = new GarbageCollector<>(this);
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
