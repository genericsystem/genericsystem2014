package org.genericsystem.concurrency;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

import org.genericsystem.kernel.AbstractBuilder;
import org.genericsystem.kernel.Checker;
import org.genericsystem.kernel.Context;
import org.genericsystem.kernel.Statics;

public class Root extends Vertex implements DefaultRoot<Vertex> {

	private final TsGenerator generator = new TsGenerator();

	private final DefaultEngine<Generic, Vertex> engine;

	private Archiver<Vertex> archiver;

	private final Context<Vertex> context;

	private final GarbageCollector<Vertex> garbageCollector;

	public Root(DefaultEngine<Generic, Vertex> engine) {
		init(null, Collections.emptyList(), engine.getValue(), Collections.emptyList());
		this.engine = engine;
		long ts = pickNewTs();
		restore(ts, 0L, 0L, Long.MAX_VALUE);
		garbageCollector = new GarbageCollector<>(this);

		context = new Context<Vertex>(this);
		context.init(new Checker<>(context), new AbstractBuilder<Vertex>(this) {

			@Override
			protected Vertex newT() {
				return new Vertex().restore(((Root) getRoot()).pickNewTs(), ((Root) getRoot()).getEngine().getCurrentCache().getTs(), 0L, Long.MAX_VALUE);
			}

			@Override
			protected Vertex[] newTArray(int dim) {
				return new Vertex[dim];
			}
		});
	}

	public void buildAndStartArchiver(String persistentDirectoryPath) {
		if (persistentDirectoryPath != null && archiver == null) {
			archiver = new Archiver<Vertex>(this, persistentDirectoryPath);
			archiver.startScheduler();
		}
	}

	// TODO mount this in API
	public void close() {
		if (archiver != null)
			archiver.close();
	}

	@Override
	public Root getRoot() {
		return super.getRoot();
	}

	@Override
	public Context<Vertex> getCurrentCache() {
		return context;
	}

	@Override
	public long pickNewTs() {
		return generator.pickNewTs();
	}

	@Override
	public DefaultEngine<Generic, Vertex> getEngine() {
		return engine;
	}

	@Override
	public GarbageCollector<Vertex> getGarbageCollector() {
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
