package org.genericsystem.kernel;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.genericsystem.api.defaults.DefaultBuilder;
import org.genericsystem.kernel.Vertex.SystemClass;
import org.genericsystem.kernel.annotations.InstanceClass;

public abstract class Builder<T extends AbstractVertex<T>> implements DefaultBuilder<T> {

	private final Context<T> context;

	protected Builder(Context<T> context) {
		this.context = context;
	}

	@Override
	public Context<T> getContext() {
		return context;
	}

	@SuppressWarnings("unchecked")
	protected Class<T> getTClass() {
		return (Class<T>) Vertex.class;
	}

	@SuppressWarnings("unchecked")
	protected Class<T> getSystemTClass() {
		return (Class<T>) SystemClass.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public final T[] newTArray(int dim) {
		return (T[]) Array.newInstance(getTClass(), dim);
	}

	abstract protected T newT(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components);

	@SuppressWarnings("unchecked")
	protected T newT(Class<?> clazz, T meta) {
		InstanceClass metaAnnotation = meta == null ? null : getAnnotedClass(meta).getAnnotation(InstanceClass.class);
		if (metaAnnotation != null)
			if (clazz == null || clazz.isAssignableFrom(metaAnnotation.value()))
				clazz = metaAnnotation.value();
			else if (!metaAnnotation.value().isAssignableFrom(clazz))
				getContext().discardWithException(new InstantiationException(clazz + " must extends " + metaAnnotation.value()));

		try {
			if (clazz == null)
				return getTClass().newInstance();
			if (!getTClass().isAssignableFrom(clazz))
				return getSystemTClass().newInstance();
			return (T) clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			getContext().discardWithException(e);
		}
		return null; // Not reached
	}

	Class<?> getAnnotedClass(T vertex) {
		Class<?> vertexClass = vertex.getClass();
		if (vertexClass.equals(context.getBuilder().getSystemTClass()))
			return context.getRoot().findAnnotedClass(vertex);
		return vertexClass;
	}

	abstract T rebuildAll(T toRebuild, Supplier<T> rebuilder, Set<T> dependenciesToRebuild);

	@SuppressWarnings("unchecked")
	T setMeta(int dim) {
		T root = (T) context.getRoot();
		T adjustedMeta = adjustMeta(root, dim);
		if (adjustedMeta.getComponents().size() == dim)
			return adjustedMeta;
		T[] components = newTArray(dim);
		Arrays.fill(components, root);
		return rebuildAll(null, () -> build(null, null, Collections.singletonList(adjustedMeta), root.getValue(), Arrays.asList(components)),
				context.computePotentialDependencies(adjustedMeta, Collections.singletonList(adjustedMeta), root.getValue(), Arrays.asList(components)));
	}

	T adjustMeta(T meta, int dim) {
		assert meta.isMeta();
		int size = meta.getComponents().size();
		if (size > dim)
			return null;
		if (size == dim)
			return meta;
		T directInheriting = meta.getInheritings().first();
		return directInheriting != null && directInheriting.getComponents().size() <= dim ? adjustMeta(directInheriting, dim) : meta;
	}

	abstract protected T getOrBuild(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components);

	T build(Class<?> clazz, T meta, List<T> supers, Serializable value, List<T> components) {
		return context.plug(newT(clazz, meta, supers, value, components));
	}

	abstract List<T> computeAndCheckOverridesAreReached(T adjustedMeta, List<T> overrides, Serializable value, List<T> components);
}
