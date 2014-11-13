package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.genericsystem.api.core.IRoot;
import org.genericsystem.api.exception.RollbackException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface DefaultRoot<T extends AbstractVertex<T>> extends IRoot<T> {

	static Logger log = LoggerFactory.getLogger(DefaultRoot.class);

	@Override
	default void discardWithException(Throwable exception) throws RollbackException {
		throw new RollbackException(exception);
	}

	default void check(boolean isOnAdd, boolean isFlushTime, T t) throws RollbackException {
		t.checkSystemConstraints(isOnAdd, isFlushTime);
		t.checkConsistency();
		t.checkConstraints(isOnAdd, isFlushTime);
	}

	@Override
	default T getMetaAttribute() {
		return getMeta(Statics.ATTRIBUTE_SIZE);
	}

	@Override
	default T getMetaRelation() {
		return getMeta(Statics.RELATION_SIZE);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T getMeta(int dim) {
		T adjustedMeta = ((T) this).adjustMeta(dim);
		return adjustedMeta != null && adjustedMeta.getComponents().size() == dim ? adjustedMeta : null;
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setMeta(int dim) {
		T adjustedMeta = ((T) this).adjustMeta(dim);
		if (adjustedMeta.getComponents().size() == dim)
			return adjustedMeta;
		List<T> components = new ArrayList<>();
		for (int i = 0; i < dim; i++)
			components.add((T) this);
		List<T> supers = Collections.singletonList(adjustedMeta);
		return ((T) this).rebuildAll(null, () -> ((T) this).newT(null, null, Collections.singletonList(adjustedMeta), getValue(), components).plug(), adjustedMeta.computePotentialDependencies(supers, getValue(), components));
	}

	@Override
	default T addType(Serializable value) {
		return addInstance(value, coerceToTArray());
	}

	@Override
	default T addType(T override, Serializable value) {
		return addInstance(override, value, coerceToTArray());
	}

	@Override
	default T addType(List<T> overrides, Serializable value) {
		return addInstance(overrides, value, coerceToTArray());
	}

	@Override
	default T setType(Serializable value) {
		return setInstance(value, coerceToTArray());
	}

	@Override
	default T setType(T override, Serializable value) {
		return setInstance(override, value, coerceToTArray());
	}

	@Override
	default T setType(List<T> overrides, Serializable value) {
		return setInstance(overrides, value, coerceToTArray());
	}

	@Override
	default T addTree(Serializable value) {
		return addTree(value, 1);
	}

	@Override
	default T addTree(Serializable value, int parentsCount) {
		return addInstance(value, coerceToTArray(new Object[parentsCount]));
	}

	@Override
	default T setTree(Serializable value) {
		return setTree(value, 1);
	}

	@Override
	@SuppressWarnings("unchecked")
	default T setTree(Serializable value, int parentsCount) {
		return setInstance(value, (T[]) new Object[parentsCount]);

	}

}
