package org.genericsystem.concurrency;

import java.util.HashSet;

import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.OptimisticLockConstraintViolationException;

public class Transaction extends org.genericsystem.cache.Transaction<Generic, Engine, Vertex, Root> {

	private final long ts;

	// TODO KK sould be protected
	public Transaction(Engine engine) {
		this(engine.unwrap().pickNewTs(), engine);
	}

	protected Transaction(long ts, Engine engine) {
		super(engine);
		this.ts = ts;
	}

	public long getTs() {
		return ts;
	}

	@Override
	public boolean isAlive(Generic generic) {
		Vertex vertex = unwrap(generic);
		return vertex != null && vertex.getLifeManager().isAlive(getTs());
	}

	@Override
	protected void apply(Iterable<Generic> adds, Iterable<Generic> removes) throws ConcurrencyControlException, ConstraintViolationException {
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
	protected boolean simpleRemove(Generic generic) {
		unwrap(generic).getLifeManager().kill(getTs());
		getEngine().unwrap().getGarbageCollector().add(unwrap(generic));
		vertices.put(generic, null);
		return true;
	}

	private class LockedLifeManager extends HashSet<LifeManager> {

		private static final long serialVersionUID = -8771313495837238881L;

		private void writeLockAllAndCheckMvcc(Iterable<Generic> adds, Iterable<Generic> removes) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
			for (Generic remove : removes)
				writeLockAndCheckMvcc(remove);
			for (Generic add : adds) {
				writeLockAndCheckMvcc(add.getMeta());
				for (Generic superT : add.getSupers())
					writeLockAndCheckMvcc(superT);
				for (Generic composite : add.getComponents())
					writeLockAndCheckMvcc(composite);
				writeLockAndCheckMvcc(add);
			}
		}

		private void writeLockAndCheckMvcc(Generic generic) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
			Vertex vertex = unwrap(generic);
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
