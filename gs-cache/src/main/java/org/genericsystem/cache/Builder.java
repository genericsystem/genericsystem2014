package org.genericsystem.cache;

import java.io.Serializable;
import java.util.List;

import org.genericsystem.cache.annotations.InstanceClass;

public class Builder<T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends org.genericsystem.kernel.Builder<T> {

	public Builder(DefaultEngine<T, V> engine) {
		super(engine);
	}

	@SuppressWarnings("unchecked")
	@Override
	public DefaultEngine<T, V> getRoot() {
		return (DefaultEngine<T, V>) super.getRoot();
	}

	@Override
	protected T newT() {
		return ((AbstractGeneric<T, V>) getRoot()).newT();
	}

	@Override
	protected T[] newTArray(int dim) {
		return ((AbstractGeneric<T, V>) getRoot()).newTArray(dim);
	}

	@Override
	public T newT(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> composites) {
		return getRoot().getOrBuildT(clazz, meta, supers, value, composites);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T newT(Class<?> clazz, T meta) {
		InstanceClass metaAnnotation = meta == null ? null : meta.getClass().getAnnotation(InstanceClass.class);
		if (metaAnnotation != null)
			if (clazz == null || clazz.isAssignableFrom(metaAnnotation.value()))
				clazz = metaAnnotation.value();
			else if (!metaAnnotation.value().isAssignableFrom(clazz))
				getRoot().discardWithException(new InstantiationException(clazz + " must extends " + metaAnnotation.value()));
		T newT = newT();// Instantiates T in all cases...

		if (clazz == null || clazz.isAssignableFrom(newT.getClass()))
			return newT;
		if (newT.getClass().isAssignableFrom(clazz))
			try {
				return (T) clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
				getRoot().discardWithException(e);
			}
		else
			getRoot().discardWithException(new InstantiationException(clazz + " must extends " + newT.getClass()));
		return null; // Not reached
	}

	// class GenericsCache {
	//
	// private final Map<T, T> map = new ConcurrentHashMap<>();
	//
	// public T getOrBuildT(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> composites) {
	// T disposable = newT(clazz, meta).init(meta, supers, value, composites);
	// T result = map.get(disposable);
	// if (result != null)
	// return result;
	// T alreadyPresent = map.putIfAbsent(disposable, disposable);
	// return alreadyPresent != null ? alreadyPresent : disposable;
	// }
	// }

}
