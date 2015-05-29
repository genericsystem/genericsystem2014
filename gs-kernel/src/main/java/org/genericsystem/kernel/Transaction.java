package org.genericsystem.kernel;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.core.exceptions.ConcurrencyControlException;
import org.genericsystem.api.core.exceptions.OptimisticLockConstraintViolationException;

public class Transaction extends Context {

	private final long ts;

	protected Transaction(Root root, long ts) {
		super(root);
		this.ts = ts;
	}

	protected Transaction(Root root) {
		this(root, root.pickNewTs());
	}

	@Override
	public final long getTs() {
		return ts;
	}

	public void apply(Iterable<Generic> removes, Iterable<Generic> adds) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
		new LockedLifeManager().apply(removes, adds);
	}

	@Override
	protected final Generic plug(Generic generic) {
		if (getRoot().isInitialized())
			generic.getLifeManager().beginLife(getTs());
		Set<Generic> set = new HashSet<>();
		if (!generic.isMeta())
			set.add(generic.getMeta());
		set.addAll(generic.getSupers());
		set.addAll(generic.getComponents());
		set.stream().forEach(ancestor -> getRoot().getDependencies(ancestor).add(generic));
		getChecker().checkAfterBuild(true, false, generic);
		return generic;
	}

	@Override
	protected void unplug(Generic generic) {
		getChecker().checkAfterBuild(false, false, generic);
		generic.getLifeManager().kill(getTs());
		// if (!result)
		// discardWithException(new NotFoundException(generic.info()));
		Set<Generic> set = new HashSet<>();
		if (!generic.isMeta())
			set.add(generic.getMeta());
		set.addAll(generic.getSupers());
		set.addAll(generic.getComponents());
		set.stream().forEach(ancestor -> getRoot().getDependencies(ancestor).remove(generic));
	}

	@Override
	public Snapshot<Generic> getDependencies(Generic generic) {
		return new Snapshot<Generic>() {

			@Override
			public Stream<Generic> stream() {
				return getRoot().getDependencies(generic).stream(getTs());
			}

			@Override
			public Generic get(Object o) {
				return getRoot().getDependencies(generic).get((Generic) o, getTs());
			}
		};
	}

	private class LockedLifeManager {

		private Set<LifeManager> lockedLifeManagers = new HashSet<>();

		private void apply(Iterable<Generic> removes, Iterable<Generic> adds) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
			try {
				writeLockAllAndCheckMvcc(adds, removes);
				for (Generic generic : removes)
					unplug(generic);
				for (Generic generic : adds)
					plug(generic);
			} finally {
				writeUnlockAll();
			}
		}

		private void applyAdd(Generic add) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
			try {
				writeLockAndCheckMvccForAdd(add);
				plug(add);
			} finally {
				writeUnlockAll();
			}
		}

		private void applyRemove(Generic remove) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
			try {
				writeLockAndCheckMvcc(remove);
				unplug(remove);
			} finally {
				writeUnlockAll();
			}
		}

		private void writeLockAllAndCheckMvcc(Iterable<Generic> adds, Iterable<Generic> removes) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
			for (Generic remove : removes)
				writeLockAndCheckMvcc(remove);
			for (Generic add : adds)
				writeLockAndCheckMvccForAdd(add);
		}

		private void writeLockAndCheckMvccForAdd(Generic add) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
			writeLockAndCheckMvcc(add.getMeta());
			for (Generic superT : add.getSupers())
				writeLockAndCheckMvcc(superT);
			for (Generic component : add.getComponents())
				writeLockAndCheckMvcc(component);
			writeLockAndCheckMvcc(add);
		}

		private void writeLockAndCheckMvcc(Generic generic) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
			if (generic != null) {
				LifeManager manager = generic.getLifeManager();
				if (!lockedLifeManagers.contains(manager)) {
					manager.writeLock();
					lockedLifeManagers.add(manager);
					manager.checkMvcc(getTs());
				}
			}
		}

		private void writeUnlockAll() {
			for (LifeManager lifeManager : lockedLifeManagers)
				lifeManager.writeUnlock();
			lockedLifeManagers = new HashSet<>();
		}
	}

}
