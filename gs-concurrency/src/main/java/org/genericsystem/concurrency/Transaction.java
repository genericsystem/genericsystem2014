package org.genericsystem.concurrency;

import java.util.HashSet;

import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.OptimisticLockConstraintViolationException;

public class Transaction<T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends org.genericsystem.cache.Transaction<T, V> {

	private final long ts;

	@Override
	public DefaultEngine<T, V> getRoot() {
		return (DefaultEngine<T, V>) super.getRoot();
	}

	Transaction(DefaultEngine<T, V> engine) {
		this(engine.unwrap().pickNewTs(), engine);
	}

	Transaction(long ts, DefaultEngine<T, V> engine) {
		super(engine);
		this.ts = ts;
	}

	public long getTs() {
		return ts;
	}

	@Override
	public boolean isAlive(T generic) {
		V vertex = unwrap(generic);
		return vertex != null && vertex.getLifeManager().isAlive(getTs());
	}

	@Override
	public void apply(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, ConstraintViolationException {
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
	protected boolean simpleRemove(T generic) {
		unwrap(generic).getLifeManager().kill(getTs());
		getRoot().unwrap().getGarbageCollector().add(unwrap(generic));
		vertices.put(generic, null);
		return true;
	}

	private class LockedLifeManager extends HashSet<LifeManager> {

		private static final long serialVersionUID = -8771313495837238881L;

		private void writeLockAllAndCheckMvcc(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
			for (T remove : removes)
				writeLockAndCheckMvcc(remove);
			for (T add : adds) {
				writeLockAndCheckMvcc(add.getMeta());
				for (T superT : add.getSupers())
					writeLockAndCheckMvcc(superT);
				for (T composite : add.getComponents())
					writeLockAndCheckMvcc(composite);
				writeLockAndCheckMvcc(add);
			}
		}

		private void writeLockAndCheckMvcc(T generic) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
			V vertex = unwrap(generic);
			if (vertex != null) {
				LifeManager manager = vertex.getLifeManager();
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
