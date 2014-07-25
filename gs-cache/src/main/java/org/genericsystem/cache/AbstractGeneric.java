package org.genericsystem.cache;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.services.RootService;

public abstract class AbstractGeneric<T extends AbstractGeneric<T, U, V, W>, U extends EngineService<T, U, V, W>, V extends AbstractVertex<V, W>, W extends RootService<V, W>> extends org.genericsystem.impl.AbstractGeneric<T, U, V, W> implements
GenericService<T, U, V, W> {

	@SuppressWarnings("unchecked")
	@Override
	public T plug() {
		return getCurrentCache().plug((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean unplug() {
		return getCurrentCache().unplug((T) this);
	}

	@Override
	protected V getVertex() {
		return super.getVertex();
	}

	@Override
	protected V unwrap() {
		return super.unwrap();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected T wrap(V vertex) {
		if (vertex.isRoot())
			return (T) getRoot();
		V alive = vertex.getAlive();
		T meta = wrap(alive.getMeta());
		return meta.newT().init(alive.isThrowExistException(), meta, alive.getSupersStream().map(this::wrap).collect(Collectors.toList()), alive.getValue(), alive.getComponentsStream().map(this::wrap).collect(Collectors.toList()));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected T bindInstance(boolean throwExistException, List<T> overrides, Serializable value, T... components) {
		return super.bindInstance(throwExistException, overrides, value, components);
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

	// @SuppressWarnings("unchecked")
	// @Override
	// public T getInstance(Serializable value, T... components) {
	// T nearestMeta = adjustMeta(Collections.emptyList(), value, Arrays.asList(components));
	// if (!equals(nearestMeta))
	// return nearestMeta.getInstance(value, components);
	// for (T instance : getCurrentCache().getInstances(nearestMeta))
	// if (instance.equiv(this, value, Arrays.asList(components)))
	// return instance;
	// return null;
	// }

	@SuppressWarnings("unchecked")
	@Override
	public Snapshot<T> getComposites() {
		return getCurrentCache().getComposites((T) this);
	}

	@Override
	protected LinkedHashSet<T> computeDependencies() {
		return super.computeDependencies();
	}

}
