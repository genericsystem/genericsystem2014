package org.genericsystem.cache;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractContext<T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> {

	static Logger log = LoggerFactory.getLogger(AbstractContext.class);

	public abstract DefaultEngine<T, V> getEngine();

	public abstract boolean isAlive(T generic);

	protected void apply(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, ConstraintViolationException {
		removes.forEach(this::simpleRemove);
		adds.forEach(this::simpleAdd);
	}

	protected abstract void simpleAdd(T generic);

	protected abstract boolean simpleRemove(T generic);

	abstract Snapshot<T> getInheritings(T generic);

	abstract Snapshot<T> getInstances(T generic);

	abstract Snapshot<T> getCompositesByMeta(T generic, T meta);

	abstract Snapshot<T> getCompositesBySuper(T generic, T superT);

	abstract V unwrap(T generic);

	abstract T wrap(V vertex);

}
