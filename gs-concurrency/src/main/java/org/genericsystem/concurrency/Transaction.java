package org.genericsystem.concurrency;

import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.exceptions.ConcurrencyControlException;
import org.genericsystem.kernel.exceptions.ConstraintViolationException;
import org.genericsystem.kernel.exceptions.OptimisticLockConstraintViolationException;
import org.genericsystem.kernel.services.RootService;

public class Transaction<T extends AbstractGeneric<T, U, V, W>, U extends EngineService<T, U, V, W>, V extends AbstractVertex<V, W>, W extends RootService<V, W>> extends org.genericsystem.cache.Transaction<T, U, V, W> implements Context<T, U, V, W> {

	private transient long ts;

	// private ExecutorService service = Executors.newSingleThreadExecutor(r -> new GsThread(r, getTs()));

	// private <R> R callWithTs(Callable<R> callable) {
	// try {
	// return service.submit(callable).get();
	// } catch (InterruptedException | ExecutionException e) {
	// getEngine().rollbackAndThrowException(e);
	// return null;
	// }
	// }

	public Transaction(U engine) {
		this(((Root) ((Engine) engine).getVertex()).pickNewTs(), engine);
	}

	public Transaction(long ts, U engine) {
		super(engine);
		this.ts = ts;
	}

	@Override
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
	public void apply(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, ConstraintViolationException {
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

	// TODO Apply with checkMvcc...
	@SuppressWarnings("unchecked")
	@Override
	public void simpleAdd(T generic) {
		V vertex = generic.getMeta().getVertex();
		vertex.addInstance(generic.getSupersStream().map(g -> g.unwrap()).collect(Collectors.toList()), generic.getValue(), vertex.coerceToArray(generic.getComponentsStream().map(T::unwrap).toArray()));
		((org.genericsystem.concurrency.AbstractVertex<Vertex, Root>) vertex).getLifeManager().beginLife(getTs());
	}

	// TODO : check performance
	// remove should return a boolean.
	@Override
	public boolean simpleRemove(T generic) {
		getLifeManager(generic).kill(getTs());
		getEngine().getRoot().getGarbageCollectorManager().add(generic);
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

	@Override
	public Snapshot<T> getInheritings(T generic) {
		return () -> generic.getVertex() != null ? generic.unwrap().getInheritings().stream().map(generic::wrap).iterator() : Collections.emptyIterator();
	}

	@Override
	public Snapshot<T> getInstances(T generic) {
		return () -> generic.getVertex() != null ? generic.unwrap().getInstances().stream().map(generic::wrap).iterator() : Collections.emptyIterator();
	}

	@Override
	public Snapshot<T> getMetaComposites(T generic, T meta) {
		return () -> generic.getVertex() != null ? generic.unwrap().getMetaComposites(meta.unwrap()).stream().map(generic::wrap).iterator() : Collections.emptyIterator();
	}

	@Override
	public Snapshot<T> getSuperComposites(T generic, T superT) {
		return () -> generic.getVertex() != null ? generic.unwrap().getSuperComposites(superT.unwrap()).stream().map(generic::wrap).iterator() : Collections.emptyIterator();

	};
}
