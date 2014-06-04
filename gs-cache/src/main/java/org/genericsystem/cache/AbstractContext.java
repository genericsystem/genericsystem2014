package org.genericsystem.cache;

import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.exceptions.ConcurrencyControlException;
import org.genericsystem.kernel.exceptions.ConstraintViolationException;

public abstract class AbstractContext<T extends GenericService<T>> {

	abstract EngineService<T> getEngine();

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

	public abstract Dependencies<T> getInheritings(T generic);

	public abstract Dependencies<T> getInstances(T generic);

	public abstract CompositesDependencies<T> getMetaComposites(T generic);

	public abstract CompositesDependencies<T> getSuperComposites(T generic);

}
