package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.List;
import org.genericsystem.defaults.DefaultLifeManager;
import org.genericsystem.kernel.Generic.GenericImpl;
import org.genericsystem.kernel.annotations.InstanceClass;

public abstract class Builder {

	private final Context context;

	protected Builder(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	public final Generic[] newTArray(int dim) {
		return new Generic[dim];
	}

	protected Generic newT(Class<?> clazz, Generic meta) {
		InstanceClass metaAnnotation = meta == null ? null : getAnnotedClass(meta).getAnnotation(InstanceClass.class);
		if (metaAnnotation != null)
			if (clazz == null || clazz.isAssignableFrom(metaAnnotation.value()))
				clazz = metaAnnotation.value();
			else if (!metaAnnotation.value().isAssignableFrom(clazz))
				getContext().discardWithException(new InstantiationException(clazz + " must extends " + metaAnnotation.value()));

		try {
			if (clazz == null || !Generic.class.isAssignableFrom(clazz))
				return new GenericImpl();
			return (Generic) clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			getContext().discardWithException(e);
		}
		return null; // Not reached
	}

	abstract Class<?> getAnnotedClass(Generic vertex);

	abstract Generic buildAndPlug(Class<?> clazz, Generic meta, List<Generic> supers, Serializable value, List<Generic> components);

	abstract Generic build(long ts, Class<?> clazz, Generic meta, List<Generic> supers, Serializable value, List<Generic> components, long[] otherTs);

	public static class GenericBuilder extends Builder {

		public GenericBuilder(Context context) {
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
