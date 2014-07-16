package org.genericsystem.concurrency.vertex;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

import org.genericsystem.kernel.Statics;

public class Root extends org.genericsystem.kernel.Root implements RootService<org.genericsystem.kernel.Vertex> {

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

	public Root(Class<?>... userClasses) {
		this(Statics.ENGINE_VALUE, userClasses);
	}

	public Root(Serializable value, Class<?>... userClasses) {
		super(value, userClasses);
		lifeManager = buildLifeManager();
	}

	@Override
	// TODO use throwExistException ?
	public Root newT(boolean throwExistException) {
		Root rootConcurrency = new Root();
		rootConcurrency.lifeManager = buildLifeManager();
		return rootConcurrency;
	}

	@Override
	public Root[] newTArray(int dim) {
		return new Root[dim];
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
