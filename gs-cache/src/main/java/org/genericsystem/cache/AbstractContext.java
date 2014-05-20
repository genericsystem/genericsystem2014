package org.genericsystem.cache;

import org.genericsystem.impl.GenericService;
import org.genericsystem.kernel.exceptions.ConcurrencyControlException;
import org.genericsystem.kernel.exceptions.ConstraintViolationException;

public abstract class AbstractContext<T extends GenericService<T>> {

	abstract Engine getEngine();

	public abstract boolean isAlive(T generic);

	void apply(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, ConstraintViolationException {
		removeAll(removes);
		addAll(adds);
	}

	final void addAll(Iterable<T> generics) {
		for (T generic : generics)
			simpleAdd(generic);
	}

	void removeAll(Iterable<T> generics) {
		for (T generic : generics)
			simpleRemove(generic);
	}

	abstract void simpleAdd(T generic);

	abstract void simpleRemove(T generic);

}
