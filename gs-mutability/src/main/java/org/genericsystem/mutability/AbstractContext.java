package org.genericsystem.mutability;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.concurrency.AbstractVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractContext<M extends AbstractGeneric<M, T, V>, T extends org.genericsystem.concurrency.AbstractGeneric<T, V>, V extends AbstractVertex<V>> {

	static Logger log = LoggerFactory.getLogger(AbstractContext.class);

	public abstract DefaultEngine<M, T, V> getEngine();

	public abstract boolean isAlive(M generic);

	protected void apply(Iterable<M> adds, Iterable<M> removes) throws ConcurrencyControlException, ConstraintViolationException {
		removes.forEach(this::simpleRemove);
		adds.forEach(this::simpleAdd);
	}

	protected abstract void simpleAdd(M generic);

	protected abstract boolean simpleRemove(M generic);

	abstract Snapshot<M> getInheritings(M generic);

	abstract Snapshot<M> getInstances(M generic);

	abstract Snapshot<M> getComposites(M generic);

	abstract T unwrap(M generic);

	abstract M wrap(T vertex);

}
