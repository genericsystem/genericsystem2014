package org.genericsystem.concurrency.vertex;

import java.io.Serializable;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.exceptions.RollbackException;
import org.genericsystem.kernel.services.AncestorsService;

public class RootConcurrency extends VertexConcurrency {

	private final TsGenerator generator = new TsGenerator();

	public RootConcurrency() {
		this(Statics.ENGINE_VALUE);
	}

	public RootConcurrency(Serializable value) {
		init(0, null, Collections.emptyList(), value, Collections.emptyList());
	}

	// TODO
	// VertexConcurrency setMetaAttribute(VertexConcurrency... components) {
	// checkSameEngine(Arrays.asList(components));
	// VertexConcurrency allComponents[] = Statics.insertIntoArray(this, components, 0);
	// VertexConcurrency instance = getInstance(getRoot().getValue(), allComponents);
	// if (instance != null)
	// return instance;
	// List<VertexConcurrency> supersList = new ArrayList<>(new SupersComputer<>(0, meta, Collections.emptyList(), getRoot().getValue(), Arrays.asList(allComponents)));
	// VertexConcurrency meta = computeNearestMeta(Collections.emptyList(), value, Arrays.asList(components));
	// return meta.buildInstance().init(0, meta, supersList, getRoot().getValue(), Arrays.asList(allComponents)).plug();
	// }

	@Override
	public boolean isRoot() {
		return true;
	}

	@Override
	public RootConcurrency getRoot() {
		return this;
	}

	@Override
	public VertexConcurrency getMeta() {
		return this;
	}

	// public Serializable getCachedValue(Serializable value) {
	// return valueCache.get(value);
	// }

	@Override
	public void rollbackAndThrowException(Exception exception) throws RollbackException {
		rollback();
		throw new RollbackException(exception);
	}

	@Override
	public void rollback() {
		// Hook for cache management
	}

	/*
	 * public static class ValueCache extends HashMap<Serializable, Serializable> { private static final long serialVersionUID = 8474952153415905986L;
	 * 
	 * @Override public Serializable get(Object key) { Serializable result = super.get(key); if (result == null) put(result = (Serializable) key, result); return result; } }
	 */
	@Override
	public VertexConcurrency getAlive() {
		// TODO is enough ?
		return this;
	}

	@Override
	public boolean equiv(AncestorsService<?> service) {
		if (this == service)
			return true;
		return Objects.equals(getValue(), service.getValue()) && AncestorsService.equivComponents(getComponents(), service.getComponents());
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
