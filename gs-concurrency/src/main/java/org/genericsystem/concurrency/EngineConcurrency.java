package org.genericsystem.concurrency;

import java.util.concurrent.atomic.AtomicLong;

import org.genericsystem.cache.Cache;
import org.genericsystem.kernel.Statics;

public class EngineConcurrency extends GenericConcurrency implements EngineServiceConcurrency<GenericConcurrency> {

	private final TsGenerator generator = new TsGenerator();

	public long pickNewTs() {
		return generator.pickNewTs();
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
				if (nanoTs > current)
					if (lastTime.compareAndSet(current, nanoTs))
						return nanoTs;
			}
		}
	}

	@Override
	public Cache<GenericConcurrency> start(Cache<GenericConcurrency> cache) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stop(Cache<GenericConcurrency> cache) {
		// TODO Auto-generated method stub

	}
}
