package org.genericsystem.concurrency;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.OptimisticLockConstraintViolationException;
import org.genericsystem.kernel.Builder;

public class Transaction<T extends AbstractGeneric<T>> extends org.genericsystem.cache.Transaction<T> {

	Transaction(DefaultEngine<T> engine) {
		this(engine, engine.pickNewTs());
	}

	Transaction(DefaultEngine<T> engine, long ts) {
		super(engine, ts);
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
		return super.plug(generic);
	}

	// for Archiver
	T simplePlug(T generic) {
		return super.plug(generic);
	}

	@Override
	public void unplug(T generic) {
		generic.getLifeManager().kill(getTs());
		getRoot().getGarbageCollector().add(generic);
	}

	@Override
	public DefaultEngine<T> getRoot() {
		return (DefaultEngine<T>) super.getRoot();
	}

	@Override
	protected T getMeta(int dim) {
		return super.getMeta(dim);
	}

	@Override
	public Set<T> computeDependencies(T node) {
		return new OrderedDependencies().visit(node);
	}

	private class OrderedDependencies extends TreeSet<T> {
		private static final long serialVersionUID = -5970021419012502402L;

		OrderedDependencies visit(T node) {
			if (!contains(node)) {
				getComposites(node).forEach(this::visit);
				getInheritings(node).forEach(this::visit);
				getInstances(node).forEach(this::visit);
				add(node);
			}
			return this;
		}
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
			protected Class<T> getTClass() {
				throw new UnsupportedOperationException();
			}
		};
	}

}
