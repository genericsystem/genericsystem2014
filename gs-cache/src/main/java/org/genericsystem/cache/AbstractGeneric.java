package org.genericsystem.cache;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;

import org.genericsystem.api.core.Snapshot;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.ISystemProperties.Constraint.CheckingType;

public abstract class AbstractGeneric<T extends AbstractGeneric<T, U, V, W>, U extends IEngine<T, U, V, W>, V extends AbstractVertex<V, W>, W extends IRoot<V, W>> extends org.genericsystem.impl.AbstractGeneric<T, U, V, W> implements IGeneric<T, U, V, W> {

	@SuppressWarnings("unchecked")
	@Override
	protected T plug() {
		T plug = getCurrentCache().plug((T) this);
		getRoot().check(CheckingType.CHECK_ON_ADD, false, (T) this);
		return plug;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean unplug() {
		getRoot().check(CheckingType.CHECK_ON_ADD, false, (T) this);
		return getCurrentCache().unplug((T) this);
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
	public Snapshot<T> getMetaComponents(T meta) {
		return getCurrentCache().getMetaComponents((T) this, meta);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Snapshot<T> getSuperComponents(T superVertex) {
		return getCurrentCache().getSuperComponents((T) this, superVertex);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Snapshot<T> getComponents() {
		return getCurrentCache().getComponents((T) this);
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
