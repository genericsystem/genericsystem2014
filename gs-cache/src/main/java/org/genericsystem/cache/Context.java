package org.genericsystem.cache;

import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.exceptions.ConcurrencyControlException;
import org.genericsystem.kernel.exceptions.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Context<T extends AbstractGeneric<T>> {

	static Logger log = LoggerFactory.getLogger(Context.class);

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

	boolean simpleRemove(T generic);

	Snapshot<T> getInheritings(T generic);

	Snapshot<T> getInstances(T generic);

	Snapshot<T> getMetaComposites(T generic, T meta);

	Snapshot<T> getSuperComposites(T generic, T superT);

}
