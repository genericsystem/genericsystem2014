package org.genericsystem.cache;

import java.io.Serializable;
import java.util.List;

import org.genericsystem.kernel.annotations.InstanceClass;

public abstract class AbstractBuilder<T extends AbstractGeneric<T>> extends org.genericsystem.kernel.AbstractBuilder<T> {

	protected AbstractBuilder(Cache<T> context) {
		super(context);
	}

	@Override
	public Cache<T> getContext() {
		return (Cache<T>) super.getContext();
	}

	@Override
	protected abstract T newT();

	@Override
	protected abstract T[] newTArray(int dim);

	@Override
	protected T newT(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components) {
		return newT(clazz, meta).init(meta, supers, value, components);
	}

	@SuppressWarnings("unchecked")
	protected T newT(Class<?> clazz, T meta) {
		InstanceClass metaAnnotation = meta == null ? null : meta.getClass().getAnnotation(InstanceClass.class);
		if (metaAnnotation != null)
			if (clazz == null || clazz.isAssignableFrom(metaAnnotation.value()))
				clazz = metaAnnotation.value();
			else if (!metaAnnotation.value().isAssignableFrom(clazz))
				getContext().discardWithException(new InstantiationException(clazz + " must extends " + metaAnnotation.value()));
		T newT = newT();// Instantiates T in all cases...

		if (clazz == null || clazz.isAssignableFrom(newT.getClass()))
			return newT;
		if (newT.getClass().isAssignableFrom(clazz))
			try {
				return (T) clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
				getContext().discardWithException(e);
			}
		else
			getContext().discardWithException(new InstantiationException(clazz + " must extends " + newT.getClass()));
		return null; // Not reached
	}

	@Override
	protected T addInstance(Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
		return super.addInstance(clazz, meta, overrides, value, components);
	}

	@Override
	protected T setInstance(Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
		return super.setInstance(clazz, meta, overrides, value, components);
	}

	@Override
	protected T setMeta(int dim) {
		return super.setMeta(dim);
	}

	public static class GenericBuilder extends AbstractBuilder<Generic> {

		public GenericBuilder(Cache<Generic> context) {
			super(context);
		}

		@Override
		protected Generic newT() {
			return new Generic();
		}

		@Override
		protected Generic[] newTArray(int dim) {
			return new Generic[dim];
		}

	}

}
