package org.genericsystem.concurrency;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.genericsystem.kernel.exceptions.ConcurrencyControlException;
import org.genericsystem.kernel.exceptions.OptimisticLockConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LifeManager {
	protected static Logger log = LoggerFactory.getLogger(LifeManager.class);

	private final long designTs;
	private long birthTs;
	private final AtomicLong lastReadTs;
	private long deathTs;
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	public LifeManager(long designTs, long birthTs, long lastReadTs, long deathTs) {
		this.designTs = designTs;
		this.birthTs = birthTs;
		this.lastReadTs = new AtomicLong(lastReadTs);
		this.deathTs = deathTs;
	}

	// public void beginLifeIfNecessary(long birthTs) {
	// if (this.birthTs == Long.MAX_VALUE)
	// this.birthTs = birthTs;
	// else
	// assert this.birthTs < birthTs : "Generic is already marked as borned but later";
	// }

	public void beginLife(long birthTs) {
		assert isWriteLockedByCurrentThread();
		assert this.birthTs == Long.MAX_VALUE : "Generic is already born";
		this.birthTs = birthTs;
	}

	void cancelBeginLife() {
		assert isWriteLockedByCurrentThread();
		birthTs = Long.MAX_VALUE;
	}

	public boolean isAlive(long contextTs) {
		if (contextTs < birthTs)
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
		long current = lastReadTs.get();
		if (contextTs <= current)
			return;
		for (;;) {
			current = lastReadTs.get();
			if (lastReadTs.compareAndSet(current, contextTs))
				break;
		}
	}

	void writeLock() {
		lock.writeLock().lock();
	}

	public void writeUnlock() {
		lock.writeLock().unlock();
	}

	public void readLock() {
		lock.readLock().lock();
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

}