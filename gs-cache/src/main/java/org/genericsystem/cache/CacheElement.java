package org.genericsystem.cache;

import java.util.stream.Stream;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.OptimisticLockConstraintViolationException;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.kernel.Checker;
import org.genericsystem.kernel.DependenciesImpl;

public class CacheElement<T extends AbstractGeneric<T>> extends AbstractCacheElement<T> {

	private final AbstractCacheElement<T> subCache;
	private final DependenciesImpl<T> adds = new DependenciesImpl<>();
	private final DependenciesImpl<T> removes = new DependenciesImpl<>();

	public CacheElement(AbstractCacheElement<T> subCache) {
		this.subCache = subCache;
	}

	public AbstractCacheElement<T> getSubCache() {
		return subCache;
	}

	public int getCacheLevel() {
		return subCache instanceof CacheElement ? ((CacheElement<T>) subCache).getCacheLevel() + 1 : 0;
	}

	@Override
	boolean isAlive(T generic) {
		return adds.contains(generic) || (!removes.contains(generic) && subCache.isAlive(generic));
	}

	void checkConstraints(Checker<T> checker) throws RollbackException {
		adds.forEach(x -> checker.checkAfterBuild(true, true, x));
		removes.forEach(x -> checker.checkAfterBuild(false, true, x));
	}

	protected T plug(T generic) {
		adds.add(generic);
		return generic;
	}

	protected void unplug(T generic) {
		if (!adds.remove(generic))
			removes.add(generic);
	}

	@Override
	Snapshot<T> getDependencies(T generic) {
		return new Snapshot<T>() {
			// @Override
			// public T get(Object o) {
			// T result = adds.get(o);
			// if (result != null)
			// return generic.isDirectAncestorOf(result) ? result : null;
			// return !removes.contains(o) ? subCache.getDependencies(generic).get(o) : null;
			// }

			@Override
			public Stream<T> get() {
				return Stream.concat(subCache.getDependencies(generic).get().filter(x -> !removes.contains(x)), adds.get().filter(x -> generic.isDirectAncestorOf(x)));
			}
		};
	}

	void apply() throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
		getSubCache().apply(removes, adds);
	}

	@Override
	protected void apply(Iterable<T> removes, Iterable<T> adds) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
		for (T generic : removes)
			unplug(generic);
		for (T generic : adds)
			plug(generic);
	}
}
