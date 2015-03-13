package org.genericsystem.cache;

import java.util.stream.Stream;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.OptimisticLockConstraintViolationException;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.kernel.Checker;
import org.genericsystem.kernel.Generic;
import org.genericsystem.kernel.PseudoConcurrentCollection;

public class CacheElement extends AbstractCacheElement {

	private final AbstractCacheElement subCache;
	private final PseudoConcurrentCollection<Generic> adds = new PseudoConcurrentCollection<>();
	private final PseudoConcurrentCollection<Generic> removes = new PseudoConcurrentCollection<>();

	public CacheElement(AbstractCacheElement subCache) {
		this.subCache = subCache;
	}

	public AbstractCacheElement getSubCache() {
		return subCache;
	}

	public int getCacheLevel() {
		return subCache instanceof CacheElement ? ((CacheElement) subCache).getCacheLevel() + 1 : 0;
	}

	@Override
	boolean isAlive(Generic generic) {
		return adds.contains(generic) || (!removes.contains(generic) && subCache.isAlive(generic));
	}

	void checkConstraints(Checker checker) throws RollbackException {
		adds.forEach(x -> checker.checkAfterBuild(true, true, x));
		removes.forEach(x -> checker.checkAfterBuild(false, true, x));
	}

	protected Generic plug(Generic generic) {
		adds.add(generic);
		return generic;
	}

	protected void unplug(Generic generic) {
		if (!adds.remove(generic))
			removes.add(generic);
	}

	@Override
	Snapshot<Generic> getDependencies(Generic generic) {
		return new Snapshot<Generic>() {
			@Override
			public Generic get(Object o) {
				Generic result = adds.get(o);
				if (result != null)
					return generic.isDirectAncestorOf(result) ? result : null;
				return !removes.contains(o) ? subCache.getDependencies(generic).get(o) : null;
			}

			@Override
			public Stream<Generic> get() {
				return Stream.concat(subCache.getDependencies(generic).get().filter(x -> !removes.contains(x)), adds.get().filter(x -> generic.isDirectAncestorOf(x)));
			}
		};
	}

	void apply() throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
		getSubCache().apply(removes, adds);
	}

	@Override
	protected void apply(Iterable<Generic> removes, Iterable<Generic> adds) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
		for (Generic generic : removes)
			unplug(generic);
		for (Generic generic : adds)
			plug(generic);
	}
}
