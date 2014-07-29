package org.genericsystem.concurrency;

import java.util.HashSet;

import org.genericsystem.kernel.exceptions.ConcurrencyControlException;
import org.genericsystem.kernel.exceptions.ConstraintViolationException;
import org.genericsystem.kernel.exceptions.OptimisticLockConstraintViolationException;

public class Transaction<T extends AbstractGeneric<T, U, V, W>, U extends EngineService<T, U, V, W>, V extends AbstractVertex<V, W>, W extends RootService<V, W>> extends org.genericsystem.cache.Transaction<T, U, V, W> {

	private final long ts;

	protected Transaction(U engine) {
		this(engine.unwrap().pickNewTs(), engine);
	}

	protected Transaction(long ts, U engine) {
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
	protected void apply(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, ConstraintViolationException {
		synchronized (getEngine()) {
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
		getEngine().unwrap().getGarbageCollector().add(unwrap(generic));
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
				for (T component : add.getComponents())
					writeLockAndCheckMvcc(component);
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
