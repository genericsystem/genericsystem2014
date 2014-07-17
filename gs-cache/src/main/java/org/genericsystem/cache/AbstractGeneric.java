package org.genericsystem.cache;

import java.util.LinkedHashSet;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.services.RootService;

public abstract class AbstractGeneric<T extends AbstractGeneric<T, U, V, W>, U extends EngineService<T, U, V, W>, V extends AbstractVertex<V, W>, W extends RootService<V, W>> extends org.genericsystem.impl.AbstractGeneric<T, U, V, W> implements
		GenericService<T, U, V, W> {

	public AbstractGeneric(boolean throwExistException) {
		super(throwExistException);
	}

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
	protected T wrap(V vertex) {
		U root = getRoot();
		T cachedGeneric = root.getGenericOfVertexFromSystemCache(vertex);
		if (cachedGeneric != null)
			return cachedGeneric;
		return root.setGenericInSystemCache(super.wrap(vertex));
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
