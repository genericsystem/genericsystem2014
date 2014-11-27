package org.genericsystem.cache;

import java.io.Serializable;
import java.util.List;

import org.genericsystem.cache.annotations.InstanceClass;

public abstract class AbstractBuilder<T extends AbstractGeneric<T, V>, V extends AbstractVertex<V>> extends org.genericsystem.kernel.AbstractBuilder<T> {

	public AbstractBuilder(DefaultEngine<T, V> engine) {
		super(engine);
	}

	@SuppressWarnings("unchecked")
	@Override
	public DefaultEngine<T, V> getRoot() {
		return (DefaultEngine<T, V>) super.getRoot();
	}

	@Override
	protected abstract T newT();

	@Override
	protected abstract T[] newTArray(int dim);

	// @Override
	// @SuppressWarnings("unchecked")
	// protected T newT() {
	// return (T) new Generic();
	// }
	//
	// @Override
	// @SuppressWarnings("unchecked")
	// protected T[] newTArray(int dim) {
	// return (T[]) new Generic[dim];
	// }

	@Override
	public T newT(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> composites) {
		return getRoot().getOrBuildT(clazz, meta, supers, value, composites);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected T newT(Class<?> clazz, T meta) {
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

}
