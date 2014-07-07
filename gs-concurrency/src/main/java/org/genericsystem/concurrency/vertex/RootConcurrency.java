package org.genericsystem.concurrency.vertex;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

import org.genericsystem.kernel.Root;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;

public class RootConcurrency extends Root implements RootServiceConcurrency<Vertex> {

	private final TsGenerator generator = new TsGenerator();

	// TODO KK DEBUT KK cf TsProvider
	private final TsProvider tsProvider = new TsProvider(pickNewTs());

	public TsProvider getTsProvider() {
		return tsProvider;
	}

	class TsProvider {

		long ts;

		public TsProvider(long ts) {
			this.ts = ts;
		}

		public void updateTs(long ts) {
			this.ts = ts;
		}

		public long getTs() {
			return ts;
		}
	}

	// TODO KK FIN KK

	// TODO KK DEBUT KK cf VertexConcurrency
	private LifeManager lifeManager;

	void restore(Long designTs, long birthTs, long lastReadTs, long deathTs) {
		lifeManager = buildLifeManager(designTs, birthTs, lastReadTs, deathTs);
	}

	@Override
	public LifeManager getLifeManager() {
		return lifeManager;
	}

	public boolean isAlive(long ts) {
		return lifeManager.isAlive(ts);
	}

	// TODO KK FIN KK

	public RootConcurrency(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public RootConcurrency(Serializable value, Class<?>... userClasses) {
		super(value, userClasses);
		lifeManager = buildLifeManager();
	}

	@Override
	public VertexConcurrency buildInstance() {
		return new VertexConcurrency();
	}

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

}
