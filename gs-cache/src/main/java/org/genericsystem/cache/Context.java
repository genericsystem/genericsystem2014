package org.genericsystem.cache;

import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.exceptions.ConcurrencyControlException;
import org.genericsystem.kernel.exceptions.ConstraintViolationException;

public interface Context<T extends GenericService<T>> {

	EngineService<T> getEngine();

	boolean isAlive(T generic);

	default void apply(Iterable<T> adds, Iterable<T> removes) throws ConcurrencyControlException, ConstraintViolationException {
		removeAll(removes);
		addAll(adds);
	}

	default void addAll(Iterable<T> generics) {
		for (T generic : generics)
			simpleAdd(generic);
	}

	default void removeAll(Iterable<T> generics) {
		for (T generic : generics)
			simpleRemove(generic);
	}

	void simpleAdd(T generic);

	void simpleRemove(T generic);

	Snapshot<T> getInheritings(T generic);

	Snapshot<T> getInstances(T generic);

	Snapshot<T> getComposites(T generic);

	Snapshot<T> getCompositesByMeta(T generic, T meta);

	Snapshot<T> getCompositesBySuper(T generic, T superT);
	// CompositesDependencies<T> getMetaComposites(T generic);
	//
	// CompositesDependencies<T> getSuperComposites(T generic);

}
