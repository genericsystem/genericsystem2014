package org.genericsystem.concurrency;

import java.util.HashSet;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.OptimisticLockConstraintViolationException;
import org.genericsystem.kernel.Builder;

public class Transaction<T extends AbstractGeneric<T>> extends org.genericsystem.cache.Transaction<T> {

	Transaction(DefaultEngine<T> engine) {
		super(engine, engine.pickNewTs());
	}

	@Override
	protected void apply(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, ConstraintViolationException {
		synchronized (getRoot()) {
			LifeManagersLocker lifeManagerLocker = new LifeManagersLocker();
			try {
				lifeManagerLocker.writeLockAllAndCheckMvcc(adds, removes);
				super.apply(adds, removes);
			} finally {
				lifeManagerLocker.writeUnlockAll();
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

	private class LifeManagersLocker extends HashSet<LifeManager> {

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

	@Override
	protected Builder<T> buildBuilder() {
		return new Builder<T>(this) {
			@Override
			@SuppressWarnings("unchecked")
			protected Class<T> getTClass() {
				return (Class<T>) Generic.class;
			}
		};
	}
}
