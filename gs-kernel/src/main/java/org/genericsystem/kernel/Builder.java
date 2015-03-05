package org.genericsystem.kernel;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.List;
import org.genericsystem.api.defaults.DefaultLifeManager;
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
		return (Class<T>) Generic.class;
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

	abstract Class<?> getAnnotedClass(T vertex);

	abstract T buildAndPlug(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components);

	abstract T build(long ts, Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components, long[] otherTs);

	public static class GenericBuilder extends Builder<Generic> {

		public GenericBuilder(Context<Generic> context) {
			super(context);
		}

		@Override
		Generic build(long ts, Class<?> clazz, Generic meta, List<Generic> supers, Serializable value, List<Generic> components, long[] otherTs) {
			return ((Root) getContext().getRoot()).init(newT(clazz, meta), ts, meta, supers, value, components, otherTs);
		}

		@Override
		Generic buildAndPlug(Class<?> clazz, Generic meta, List<Generic> supers, Serializable value, List<Generic> components) {
			return getContext().plug(build(((Root) getContext().getRoot()).pickNewTs(), clazz, meta, supers, value, components, ((Root) getContext().getRoot()).isInitialized() ? DefaultLifeManager.USER_TS : DefaultLifeManager.SYSTEM_TS));
		}

		@Override
		Class<?> getAnnotedClass(Generic vertex) {
			if (vertex.isSystem()) {
				Class<?> annotedClass = ((Root) getContext().getRoot()).findAnnotedClass(vertex);
				if (annotedClass != null)
					return annotedClass;
			}
			return vertex.getClass();
		}

	}
}
