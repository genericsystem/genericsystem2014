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

	public abstract boolean isAlive(T generic);

	protected void apply(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, ConstraintViolationException {
		removeAll(removes);
		addAll(adds);
	}

	void addAll(Iterable<T> generics) {
		for (T generic : generics)
			simpleAdd(generic);
	}

	void removeAll(Iterable<T> generics) {
		for (T generic : generics)
			simpleRemove(generic);
	}

	protected abstract void simpleAdd(T generic);

	protected abstract boolean simpleRemove(T generic);

	abstract Snapshot<T> getInheritings(T generic);

	abstract Snapshot<T> getInstances(T generic);

	abstract Snapshot<T> getMetaComposites(T generic, T meta);

	abstract Snapshot<T> getSuperComposites(T generic, T superT);

	abstract V unwrap(T generic);

}
