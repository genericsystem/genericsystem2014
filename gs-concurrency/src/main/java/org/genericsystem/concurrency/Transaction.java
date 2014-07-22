package org.genericsystem.concurrency;

import java.util.HashSet;
import java.util.stream.Collectors;

import org.genericsystem.kernel.exceptions.ConcurrencyControlException;
import org.genericsystem.kernel.exceptions.ConstraintViolationException;
import org.genericsystem.kernel.exceptions.OptimisticLockConstraintViolationException;

public class Transaction<T extends AbstractGeneric<T, U, V, W>, U extends EngineService<T, U, V, W>, V extends AbstractVertex<V, W>, W extends RootService<V, W>> extends org.genericsystem.cache.Transaction<T, U, V, W> {

	private transient long ts;

	protected Transaction(U engine) {
		this(((Engine) engine).getVertex().pickNewTs(), engine);
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
		return generic.getVertex() != null && getLifeManager(generic).isAlive(getTs());
	}

	@SuppressWarnings("unchecked")
	private LifeManager getLifeManager(T generic) {
		return ((org.genericsystem.concurrency.AbstractVertex<Vertex, Root>) generic.getVertex()).getLifeManager();
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
	protected void simpleAdd(T generic) {
		V vertex = generic.getMeta().getVertex();
		vertex.addInstance(generic.getSupersStream().map(g -> g.unwrap()).collect(Collectors.toList()), generic.getValue(), vertex.coerceToArray(generic.getComponentsStream().map(T::unwrap).toArray()));
		vertex.getLifeManager().beginLife(getTs());
	}

	@Override
	protected boolean simpleRemove(T generic) {
		getLifeManager(generic).kill(getTs());
		getEngine().getVertex().getGarbageCollector().add(generic.getVertex());
		return true;
	}

	private class LockedLifeManager extends HashSet<LifeManager> {

		private static final long serialVersionUID = -8771313495837238881L;

		private void writeLockAllAndCheckMvcc(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
			for (T generic : removes)
				writeLockAndCheckMvcc(getLifeManager(generic));
			for (T generic : adds) {
				writeLockAndCheckMvcc(getLifeManager(generic.getMeta()));
				for (T effectiveSuper : generic.getSupers())
					writeLockAndCheckMvcc(getLifeManager(effectiveSuper));
				for (T component : generic.getComponents())
					writeLockAndCheckMvcc(getLifeManager(component));
				writeLockAndCheckMvcc(getLifeManager(generic));
			}
		}

		private void writeLockAndCheckMvcc(LifeManager manager) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
			if (!contains(manager)) {
				manager.writeLock();
				add(manager);
				manager.checkMvcc(getTs());
			}
		}

		private void writeUnlockAll() {
			for (LifeManager lifeManager : this)
				lifeManager.writeUnlock();
		}
	}
}
