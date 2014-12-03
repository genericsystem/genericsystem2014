package org.genericsystem.concurrency;

import java.util.HashSet;

import org.genericsystem.api.core.IteratorSnapshot;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.OptimisticLockConstraintViolationException;

public class Transaction<T extends AbstractGeneric<T>> extends org.genericsystem.cache.Transaction<T> {

	private final long ts;

	Transaction(DefaultEngine<T> engine) {
		this(engine.pickNewTs(), engine);
	}

	Transaction(long ts, DefaultEngine<T> engine) {
		super(engine);
		this.ts = ts;
	}

	public long getTs() {
		return ts;
	}

	// @Override
	// public boolean isAlive(T generic) {
	// return generic != null && generic.getLifeManager().isAlive(getTs());
	// }

	@Override
	protected void apply(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, ConstraintViolationException {
		synchronized (getRoot()) {
			LockedLifeManager lockedLifeManager = new LockedLifeManager();
			try {
				lockedLifeManager.writeLockAllAndCheckMvcc(adds, removes);
				super.apply(adds, removes);
			} finally {
				lockedLifeManager.writeUnlockAll();
			}
		}
	}

	@Override
	public T plug(T generic) {
		generic.getLifeManager().beginLife(getTs());
		T plug = super.plug(generic);
		return plug;
	}

	@Override
	public boolean unplug(T generic) {
		generic.getLifeManager().kill(getTs());
		getRoot().getGarbageCollector().add(generic);
		return true;
	}

	@Override
	public DefaultEngine<T> getRoot() {
		return (DefaultEngine<T>) super.getRoot();
	}

	@Override
	public IteratorSnapshot<T> getInstances(T generic) {
		return () -> generic.getInstancesDependencies().iterator(ts);
	}

	@Override
	public IteratorSnapshot<T> getInheritings(T generic) {
		return () -> generic.getInheritingsDependencies().iterator(ts);
	}

	@Override
	public IteratorSnapshot<T> getComposites(T vertex) {
		return () -> vertex.getCompositesDependencies().iterator(ts);
	}

	// @Override
	// protected void indexInstance(T generic, T instance) {
	// generic.getInstancesDependencies().add(instance);
	// }
	//
	// @Override
	// protected void indexInheriting(T generic, T inheriting) {
	// generic.getInheritingsDependencies().add(inheriting);
	// }
	//
	// @Override
	// protected void indexComposite(T generic, T composite) {
	// generic.getCompositesDependencies().add(composite);
	// }

	private class LockedLifeManager extends HashSet<LifeManager> {

		private static final long serialVersionUID = -8771313495837238881L;

		private void writeLockAllAndCheckMvcc(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
			for (T remove : removes)
				writeLockAndCheckMvcc(remove);
			for (T add : adds) {
				writeLockAndCheckMvcc(add.getMeta());
				for (T superT : add.getSupers())
					writeLockAndCheckMvcc(superT);
				for (T component : add.getComponents())
					if (component != null)
						writeLockAndCheckMvcc(component);
				writeLockAndCheckMvcc(add);
			}
		}

		private void writeLockAndCheckMvcc(T generic) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
			if (generic != null) {
				LifeManager manager = generic.getLifeManager();
				if (!contains(manager)) {
					manager.writeLock();
					add(manager);
					manager.checkMvcc(getTs());
				}
			}
		}

		private void writeUnlockAll() {
			for (LifeManager lifeManager : this)
				lifeManager.writeUnlock();
		}
	}
}
