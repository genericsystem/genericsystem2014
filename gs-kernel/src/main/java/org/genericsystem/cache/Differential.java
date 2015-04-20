package org.genericsystem.cache;

import java.util.stream.Stream;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.core.exceptions.ConcurrencyControlException;
import org.genericsystem.api.core.exceptions.OptimisticLockConstraintViolationException;
import org.genericsystem.api.core.exceptions.RollbackException;
import org.genericsystem.kernel.Checker;
import org.genericsystem.kernel.Generic;

public class Differential extends AbstractDifferential {

	private final AbstractDifferential differential;
	private final PseudoConcurrentCollection<Generic> adds = new PseudoConcurrentCollection<>();
	private final PseudoConcurrentCollection<Generic> removes = new PseudoConcurrentCollection<>();

	public Differential(AbstractDifferential subCache) {
		this.differential = subCache;
	}

	public AbstractDifferential getSubCache() {
		return differential;
	}

	public int getCacheLevel() {
		return differential instanceof Differential ? ((Differential) differential).getCacheLevel() + 1 : 0;
	}

	@Override
	boolean isAlive(Generic generic) {
		return adds.contains(generic) || (!removes.contains(generic) && differential.isAlive(generic));
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
				return !removes.contains(o) ? differential.getDependencies(generic).get(o) : null;
			}

			@Override
			public Stream<Generic> stream() {
				return Stream.concat(differential.getDependencies(generic).stream().filter(x -> !removes.contains(x)), adds.stream().filter(x -> generic.isDirectAncestorOf(x)));
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
