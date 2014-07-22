package org.genericsystem.cache;

import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.exceptions.ConcurrencyControlException;
import org.genericsystem.kernel.exceptions.ConstraintViolationException;
import org.genericsystem.kernel.services.RootService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractContext<T extends AbstractGeneric<T, U, V, W>, U extends EngineService<T, U, V, W>, V extends AbstractVertex<V, W>, W extends RootService<V, W>> {

	static Logger log = LoggerFactory.getLogger(AbstractContext.class);

	public abstract U getEngine();

	protected abstract boolean isAlive(T generic);

	protected void apply(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, ConstraintViolationException {
		removeAll(removes);
		addAll(adds);
	}

	protected void addAll(Iterable<T> generics) {
		for (T generic : generics)
			simpleAdd(generic);
	}

	protected void removeAll(Iterable<T> generics) {
		for (T generic : generics)
			simpleRemove(generic);
	}

	protected abstract void simpleAdd(T generic);

	protected abstract boolean simpleRemove(T generic);

	protected abstract Snapshot<T> getInheritings(T generic);

	protected abstract Snapshot<T> getInstances(T generic);

	protected abstract Snapshot<T> getMetaComposites(T generic, T meta);

	protected abstract Snapshot<T> getSuperComposites(T generic, T superT);

}
