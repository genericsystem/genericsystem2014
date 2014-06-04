package org.genericsystem.concurrency.vertex;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.genericsystem.concurrency.generic.GenericConcurrency;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.DependenciesImpl;
import org.genericsystem.kernel.exceptions.ConcurrencyControlException;
import org.genericsystem.kernel.exceptions.OptimisticLockConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LifeManager {
	protected static Logger log = LoggerFactory.getLogger(LifeManager.class);

	private final long designTs;
	private long birthTs;
	private AtomicLong lastReadTs;
	private long deathTs;
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	final Dependencies<GenericConcurrency> engineInheritings = new DependenciesImpl<>();
	final Dependencies<GenericConcurrency> engineInstances = new DependenciesImpl<>();
	final CompositesDependencies<GenericConcurrency> engineMetaComposites = buildCompositeDependencies();
	final CompositesDependencies<GenericConcurrency> engineSuperComposites = buildCompositeDependencies();

	public LifeManager(long designTs, long birthTs, long lastReadTs, long deathTs) {
		this.designTs = designTs;
		this.birthTs = birthTs;
		this.lastReadTs = new AtomicLong(lastReadTs);
		this.deathTs = deathTs;
	}

	private CompositesDependencies<GenericConcurrency> buildCompositeDependencies() {
		// TODO Auto-generated method stub
		return null;
	}

	public void beginLife(long birthTs) {
		assert isWriteLockedByCurrentThread();
		assert this.birthTs == Long.MAX_VALUE : "Generic is already born";
		this.birthTs = birthTs;
	}

	// public void notConcurrentBeginLife(long birthTs) {
	// this.birthTs = birthTs;
	// }

	void cancelBeginLife() {
		assert isWriteLockedByCurrentThread();
		birthTs = Long.MAX_VALUE;
	}

	public boolean isAlive(long contextTs) {
		// NotThreadSafe at all
		if (contextTs < birthTs)// Pas de reference à deathTs ici
			return false;
		readLock();
		try {
			atomicAdjustLastReadTs(contextTs);
			return contextTs >= birthTs && contextTs < deathTs;
		} finally {
			readUnlock();
		}
	}

	void checkMvcc(long contextTs) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
		if (deathTs != Long.MAX_VALUE)
			throw new OptimisticLockConstraintViolationException("Attempt to kill a generic that is already killed by another thread");
		assert isWriteLockedByCurrentThread();
		if (contextTs < lastReadTs.get())
			throw new ConcurrencyControlException("" + contextTs + " " + lastReadTs.get());
	}

	void kill(long contextTs) {
		assert lock.isWriteLockedByCurrentThread();
		assert contextTs >= birthTs : "Can not kill a generic that is not yet born";
		assert deathTs == Long.MAX_VALUE : "Can not kill a generic that will die in the future";
		assert contextTs >= getLastReadTs() : "Mvcc rule violation";
		atomicAdjustLastReadTs(contextTs);
		deathTs = contextTs;
	}

	void resurect() {
		assert isWriteLockedByCurrentThread();
		deathTs = Long.MAX_VALUE;
	}

	long getLastReadTs() {
		return lastReadTs.get();
	}

	long getDesignTs() {
		return designTs;
	}

	long getDeathTs() {
		return deathTs;
	}

	public void atomicAdjustLastReadTs(long contextTs) {
		for (;;) {
			long current = lastReadTs.get();
			if (contextTs <= current)
				break;
			if (lastReadTs.compareAndSet(current, contextTs))
				break;
		}
	}

	void writeLock() {
		lock.writeLock().lock();
		// try {
		// if (!lock.writeLock().tryLock(Statics.TIMEOUT,
		// TimeUnit.MILLISECONDS))
		// throw new IllegalStateException("Can't acquire a write lock");
		// } catch (InterruptedException e) {
		// throw new IllegalStateException("Can't acquire a write lock");
		// }
	}

	public void writeUnlock() {
		lock.writeLock().unlock();
	}

	public void readLock() {
		lock.readLock().lock();
		// try {
		// if (!lock.readLock().tryLock(Statics.TIMEOUT, TimeUnit.MILLISECONDS))
		// throw new IllegalStateException("Can't acquire a read lock");
		// } catch (InterruptedException e) {
		// throw new IllegalStateException("Can't acquire a read lock");
		// }
	}

	public void readUnlock() {
		lock.readLock().unlock();
	}

	public boolean isWriteLockedByCurrentThread() {
		return lock.isWriteLockedByCurrentThread();
	}

	long getBirthTs() {
		return birthTs;
	}

	public boolean willDie() {
		return deathTs != Long.MAX_VALUE;
	}

	public Dependencies<GenericConcurrency> getEngineInheritings() {
		return engineInheritings;
	}

	public Dependencies<GenericConcurrency> getEngineInstances() {
		return engineInstances;
	}

	public CompositesDependencies<GenericConcurrency> getEngineMetaComposites() {
		return engineMetaComposites;
	}

	public CompositesDependencies<GenericConcurrency> getEngineSuperComposites() {
		return engineSuperComposites;
	}

}