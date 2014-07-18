package org.genericsystem.concurrency.vertex;

import java.io.Serializable;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.SystemCache;
import org.genericsystem.kernel.services.ApiService;

public class Root extends Vertex implements RootService<Vertex, Root> {

	private final TsGenerator generator = new TsGenerator();

	private final SystemCache<Vertex> systemCache = new SystemCache<Vertex>(this);

	public Root(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Root(Serializable value, Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, null, userClasses);
	}

	public Root(Serializable value, SystemCache<Vertex> systemCache, Class<?>... userClasses) {
		init(false, null, Collections.emptyList(), value, Collections.emptyList());
		long ts = getRoot().pickNewTs();
		restore(ts, ts, 0L, Long.MAX_VALUE);
		this.systemCache.init(userClasses);
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

	static class TsGenerator {
		private final long startTime = System.nanoTime();
		private final AtomicLong lastTime = new AtomicLong(0L);

		long pickNewTs() {
			long nanoTs;
			long current;
			for (;;) {
				nanoTs = startTime - System.nanoTime();
				current = lastTime.get();
				if (nanoTs - current > 0)
					if (lastTime.compareAndSet(current, nanoTs))
						return nanoTs;
			}
		}
	}

}
