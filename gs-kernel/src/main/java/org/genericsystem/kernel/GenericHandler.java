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
import org.genericsystem.api.exception.AmbiguousSelectionException;
import org.genericsystem.api.exception.UnreachableOverridesException;

//TODO must inherits directly from AbstractVertex in fine i think...
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

		// TODO must be called by systemCache && archiver
		public static <T extends DefaultVertex<T>> GenericHandler<T> newHandlerWithComputeSupers(Builder<T> builder, Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
			return new GenericHandler<>(builder, clazz, meta, overrides, value, components).check().adjustMeta().computeSupers();
		}

		// TODO to remove, systemCache && archiver must recompute supers
		public static <T extends DefaultVertex<T>> GenericHandler<T> newHandler(Builder<T> builder, Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
			return new GenericHandler<>(builder, clazz, meta, overrides, value, components).adjustMeta().affectSupers();
		}

		// TODO must be called by archiver for upload the metas
		// To remove, metacreation withe setMeta(int i) must not be lazy, it must be a "one step" and "eager" operation
		@SuppressWarnings("unchecked")
		public static <T extends DefaultVertex<T>> GenericHandler<T> newMetaHandler(Builder<T> builder, int dim) {
			T root = (T) builder.getContext().getRoot();
			List<T> components = new ArrayList<>(dim);
			for (int i = 0; i < dim; i++)
				components.add(root);
			return new GenericHandler<>(builder, null, null, null, root.getValue(), components).adjustMeta(dim);
		}

		// TODO to remove, adjustMeta must a lazy operation i think
		@SuppressWarnings("unchecked")
		public static <T extends DefaultVertex<T>> GenericHandler<T> newMetaHandler(Builder<T> builder, T meta, Serializable value, T... components) {
			List<T> componentsList = Arrays.asList(components);
			return new GenericHandler<>(builder, null, meta, null, value, componentsList).adjustMeta(meta, value, componentsList);
		}

		// TODO to remove, Restructurator must not be called from external
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
		} else {
			if (meta.isMeta())
				adjustedMeta = GenericHandlerFactory.newMetaHandler(builder, components.size()).setMeta();
			else
				adjustedMeta = getAdjustMeta(meta, value, components);
		}
		return this;
	}

	private GenericHandler<T> adjustMeta(T meta, Serializable value, List<T> components) {
		gettable = getAdjustMeta(meta, value, components);
		return this;
	}

	private T getAdjustMeta(T meta, Serializable value, List<T> components) {
		T result = null;
		if (!components.equals(meta.getComponents()))
			for (T directInheriting : meta.getInheritings()) {
				if (meta.componentsDepends(components, directInheriting.getComponents())) {
					if (result == null)
						result = directInheriting;
					else
						builder.getContext().discardWithException(new AmbiguousSelectionException("Ambigous selection : " + result.info() + directInheriting.info()));
				}
			}
		return result == null ? meta : getAdjustMeta(result, value, components);
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

	private List<T> computeAndCheckOverridesAreReached(T adjustedMeta, List<T> overrides, Serializable value, List<T> components) {
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

	T setMeta() {
		T meta = get();
		if (meta != null)
			return meta;
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
