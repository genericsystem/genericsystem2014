package org.genericsystem.kernel;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.List;

import org.genericsystem.api.defaults.DefaultVertex;
import org.genericsystem.kernel.annotations.InstanceClass;

public abstract class Builder<T extends DefaultVertex<T>> {

	private final Context<T> context;

	protected Builder(Context<T> context) {
		this.context = context;
	}

	public Context<T> getContext() {
		return context;
	}

	@SuppressWarnings("unchecked")
	protected Class<T> getTClass() {
		return (Class<T>) Vertex.class;
	}

	@SuppressWarnings("unchecked")
	public final T[] newTArray(int dim) {
		return (T[]) Array.newInstance(getTClass(), dim);
	}

	@SuppressWarnings("unchecked")
	protected T newT(Class<?> clazz, T meta) {
		InstanceClass metaAnnotation = meta == null ? null : getAnnotedClass(meta).getAnnotation(InstanceClass.class);
		if (metaAnnotation != null)
			if (clazz == null || clazz.isAssignableFrom(metaAnnotation.value()))
				clazz = metaAnnotation.value();
			else if (!metaAnnotation.value().isAssignableFrom(clazz))
				getContext().discardWithException(new InstantiationException(clazz + " must extends " + metaAnnotation.value()));

		try {
			if (clazz == null || !getTClass().isAssignableFrom(clazz))
				return getTClass().newInstance();
			return (T) clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			getContext().discardWithException(e);
		}
		return null; // Not reached
	}

	private Class<?> getAnnotedClass(T vertex) {
		if (vertex.isSystem()) {
			Class<?> annotedClass = context.getRoot().findAnnotedClass(vertex);
			if (annotedClass != null)
				return annotedClass;
		}
		return vertex.getClass();
	}

	T buildAndPlug(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components) {
		return context.plug(build(clazz, meta, supers, value, components, context.getRoot().isInitialized() ? Statics.USER_TS : Statics.SYSTEM_TS));
	}

	public abstract T build(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components, long[] otherTs);

}
