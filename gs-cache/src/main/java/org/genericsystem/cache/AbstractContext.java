package org.genericsystem.cache;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.kernel.AbstractVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractContext<T extends AbstractGeneric<T, U, V, W>, U extends IEngine<T, U, V, W>, V extends AbstractVertex<V, W>, W extends IRoot<V, W>> {

	static Logger log = LoggerFactory.getLogger(AbstractContext.class);

	public abstract U getEngine();

	public abstract boolean isAlive(T generic);

	public void apply(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, ConstraintViolationException {
		removes.forEach(this::simpleRemove);
		adds.forEach(this::simpleAdd);
	}

	protected abstract void simpleAdd(T generic);

	protected abstract boolean simpleRemove(T generic);

	abstract Snapshot<T> getInheritings(T generic);

	abstract Snapshot<T> getInstances(T generic);

	abstract Snapshot<T> getMetaComposites(T generic, T meta);

	abstract Snapshot<T> getSuperComposites(T generic, T superT);

	abstract V unwrap(T generic);

	abstract T wrap(V vertex);

}
