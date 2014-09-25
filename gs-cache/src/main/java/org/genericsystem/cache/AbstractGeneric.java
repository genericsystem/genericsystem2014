package org.genericsystem.cache;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.impl.constraints.AbstractConstraintImpl.CheckingType;
import org.genericsystem.kernel.AbstractVertex;

public abstract class AbstractGeneric<T extends AbstractGeneric<T, U, V, W>, U extends IEngine<T, U, V, W>, V extends AbstractVertex<V, W>, W extends IRoot<V, W>> extends org.genericsystem.impl.AbstractGeneric<T, U, V, W> implements IGeneric<T, U, V, W> {

	@Override
	protected T check(CheckingType checkingType, boolean isFlushTime) throws RollbackException {
		return super.check(checkingType, isFlushTime);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected T plug() {
		return getCurrentCache().plug((T) this).check(CheckingType.CHECK_ON_ADD_NODE, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean unplug() {
		boolean unplug = getCurrentCache().unplug((T) this);
		check(CheckingType.CHECK_ON_REMOVE_NODE, false);
		return unplug;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected V unwrap() {
		return getCurrentCache().unwrap((T) this);
	}

	@Override
	protected T wrap(V vertex) {
		return getCurrentCache().wrap(vertex);
	}

	@Override
	protected void forceRemove() {
		super.forceRemove();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Snapshot<T> getInstances() {
		return getCurrentCache().getInstances((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Snapshot<T> getInheritings() {
		return getCurrentCache().getInheritings((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Snapshot<T> getMetaComposites(T meta) {
		return getCurrentCache().getMetaComposites((T) this, meta);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Snapshot<T> getSuperComposites(T superVertex) {
		return getCurrentCache().getSuperComposites((T) this, superVertex);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Snapshot<T> getComponents() {
		return getCurrentCache().getComposites((T) this);
	}

	@Override
	protected LinkedHashSet<T> computeDependencies() {
		return super.computeDependencies();
	}

	@Override
	protected T newT(Class<?> clazz, boolean throwExistException, T meta, List<T> supers, Serializable value, List<T> components) {
		return getRoot().getOrBuildT(clazz, throwExistException, meta, supers, value, components);
	}

	@Override
	protected abstract T newT();

	@Override
	protected T init(boolean throwExistException, T meta, List<T> supers, Serializable value, List<T> components) {
		return super.init(throwExistException, meta, supers, value, components);
	}

	@Override
	protected T newT(Class<?> clazz) {
		return super.newT(clazz);
	}

}
