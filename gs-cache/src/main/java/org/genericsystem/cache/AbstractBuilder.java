package org.genericsystem.cache;

import java.io.Serializable;
import java.util.List;

import org.genericsystem.cache.annotations.InstanceClass;
import org.genericsystem.kernel.Context;

public abstract class AbstractBuilder<T extends AbstractGeneric<T, ?>> extends org.genericsystem.kernel.AbstractBuilder<T> {

	public AbstractBuilder(Context<T> context) {
		super(context);
	}

	@Override
	protected abstract T newT();

	@Override
	protected abstract T[] newTArray(int dim);

	@Override
	public T newT(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> composites) {
		return ((DefaultEngine<T, ?>) context.getRoot()).getOrBuildT(clazz, meta, supers, value, composites);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected T newT(Class<?> clazz, T meta) {
		InstanceClass metaAnnotation = meta == null ? null : meta.getClass().getAnnotation(InstanceClass.class);
		if (metaAnnotation != null)
			if (clazz == null || clazz.isAssignableFrom(metaAnnotation.value()))
				clazz = metaAnnotation.value();
			else if (!metaAnnotation.value().isAssignableFrom(clazz))
				context.discardWithException(new InstantiationException(clazz + " must extends " + metaAnnotation.value()));
		T newT = newT();// Instantiates T in all cases...

		if (clazz == null || clazz.isAssignableFrom(newT.getClass()))
			return newT;
		if (newT.getClass().isAssignableFrom(clazz))
			try {
				return (T) clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
				context.discardWithException(e);
			}
		else
			context.discardWithException(new InstantiationException(clazz + " must extends " + newT.getClass()));
		return null; // Not reached
	}

}
