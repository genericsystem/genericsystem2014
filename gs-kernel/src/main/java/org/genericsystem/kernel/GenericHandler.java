package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NavigableSet;
import java.util.function.Supplier;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.api.defaults.DefaultVertex;
import org.genericsystem.api.exception.UnreachableOverridesException;

public class GenericHandler<T extends DefaultVertex<T>> {
	protected final Builder<T> builder;

	private final Class<?> clazz;
	private final T meta;
	private T adjustedMeta;
	private final List<T> overrides;
	private List<T> supers;
	private final Serializable value;
	private final List<T> components;
	private T gettable;

	public static class GenericHandlerFactory {
		public static <T extends DefaultVertex<T>> GenericHandler<T> newHandlerWithComputeSupers(Builder<T> builder, Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
			return new GenericHandler<>(builder, clazz, meta, overrides, value, components).check().adjustMeta().computeSupers();
		}

		public static <T extends DefaultVertex<T>> GenericHandler<T> newHandler(Builder<T> builder, Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
			return new GenericHandler<>(builder, clazz, meta, overrides, value, components).adjustMeta().affectSupers();
		}

		@SuppressWarnings("unchecked")
		public static <T extends DefaultVertex<T>> GenericHandler<T> newMetaHandler(Builder<T> builder, int dim) {
			T root = (T) builder.getContext().getRoot();
			T[] components = builder.newTArray(dim);
			Arrays.fill(components, root);
			return new GenericHandler<>(builder, null, null, null, root.getValue(), Arrays.asList(components)).adjustMeta(dim);
		}

		public static <T extends DefaultVertex<T>> RebuildHandler<T> newRebuildHandler(Builder<T> builder, T toRebuild, Supplier<T> rebuilder, NavigableSet<T> dependenciesToRebuild) {
			return new RebuildHandler<>(builder, toRebuild, rebuilder, dependenciesToRebuild);
		}
	}

	protected GenericHandler(Builder<T> builder) {
		// assert overrides != null;
		this.builder = builder;
		this.clazz = null;
		this.meta = null;
		this.overrides = null;
		this.value = null;
		this.components = null;
	}

	private GenericHandler(Builder<T> builder, Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
		// assert overrides != null;
		this.builder = builder;
		this.clazz = clazz;
		this.meta = meta;
		this.overrides = overrides;
		this.value = value;
		this.components = components;
	}

	public GenericHandler<T> check() {
		builder.getContext().getChecker().checkBeforeBuild(clazz, meta, overrides, value, components);
		return this;
	}

	public GenericHandler<T> adjustMeta() {
		if (meta == null) {
			assert overrides.size() == 1;
			adjustedMeta = meta;
		} else
			adjustedMeta = meta.isMeta() ? builder.setMeta(components.size()) : meta.adjustMeta(value, components);
		return this;
	}

	@SuppressWarnings("unchecked")
	private GenericHandler<T> adjustMeta(int dim) {
		adjustedMeta = adjustMeta((T) builder.getContext().getRoot(), dim);
		if (adjustedMeta.getComponents().size() == dim)
			gettable = adjustedMeta;
		return this;
	}

	private T adjustMeta(T meta, int dim) {
		assert meta.isMeta();
		int size = meta.getComponents().size();
		if (size > dim)
			return null;
		if (size == dim)
			return meta;
		T directInheriting = meta.getInheritings().first();
		return directInheriting != null && directInheriting.getComponents().size() <= dim ? adjustMeta(directInheriting, dim) : meta;
	}

	public GenericHandler<T> computeSupers() {
		assert supers == null;
		supers = computeAndCheckOverridesAreReached(adjustedMeta, overrides, value, components);
		return this;
	}

	List<T> computeAndCheckOverridesAreReached(T adjustedMeta, List<T> overrides, Serializable value, List<T> components) {
		List<T> supers = new ArrayList<>(new SupersComputer<>(adjustedMeta, overrides, value, components));
		if (!ApiStatics.areOverridesReached(supers, overrides))
			builder.getContext().discardWithException(new UnreachableOverridesException("Unable to reach overrides : " + overrides + " with computed supers : " + supers));
		return supers;
	}

	public GenericHandler<T> affectSupers() {
		assert supers == null;
		supers = overrides;
		return this;
	}

	public T get() {
		// assert supers != null;
		if (gettable == null)
			gettable = adjustedMeta.getDirectInstance(supers, value, components);
		return gettable;
	}

	public T getEquiv() {
		assert adjustedMeta != null;
		return adjustedMeta.getDirectEquivInstance(value, components);
	}

	T addMeta() {
		return GenericHandlerFactory.newRebuildHandler(builder, null, () -> builder.buildAndPlug(clazz, null, Collections.singletonList(adjustedMeta), value, components),
				builder.getContext().computePotentialDependencies(adjustedMeta, Collections.singletonList(adjustedMeta), value, components)).rebuildAll();
	}

	public T add() {
		assert supers != null;
		return GenericHandlerFactory.newRebuildHandler(builder, null, () -> buildAndPlug(), builder.getContext().computePotentialDependencies(adjustedMeta, supers, value, components)).rebuildAll();
	}

	public T set(T update) {
		assert update != null;
		assert supers != null;
		return GenericHandlerFactory.newRebuildHandler(builder, update, () -> buildAndPlug(), builder.getContext().computeDependencies(update)).rebuildAll();
	}

	public T update(T update) {
		assert update != null;
		assert supers != null;
		// assert !supers.contains(update);
		return GenericHandlerFactory.newRebuildHandler(builder, update, () -> getOrBuild(), builder.getContext().computeDependencies(update)).rebuildAll();
	}

	protected T getOrBuild() {
		T instance = meta.getDirectInstance(supers, value, components);
		return instance == null ? buildAndPlug() : instance;
	}

	T buildAndPlug() {
		return builder.buildAndPlug(clazz, adjustedMeta, supers, value, components);
	}

}
