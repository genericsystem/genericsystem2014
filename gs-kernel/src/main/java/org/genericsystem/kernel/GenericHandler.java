package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.List;

import org.genericsystem.api.defaults.DefaultVertex;

public class GenericHandler<T extends AbstractVertex<T>> {
	private final Builder<T> builder;
	private final Class<?> clazz;
	private final T meta;
	private T adjustedMeta;
	private List<T> overrides;
	private List<T> supers;
	private final Serializable value;
	private final List<T> components;
	private T gettable;

	public GenericHandler(Builder<T> builder, Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
		assert overrides != null;
		this.builder = builder;
		this.clazz = clazz;
		this.meta = meta;
		this.overrides = overrides;
		this.value = value;
		this.components = components;
		check();
		adjustMeta();
		reComputeSupers();
	}

	public GenericHandler(Builder<T> builder, T gettable) {
		this.builder = builder;
		this.clazz = gettable.getClass();
		this.meta = gettable.getMeta();
		this.supers = gettable.getSupers();
		this.value = gettable.getValue();
		this.components = gettable.getComponents();
		this.gettable = gettable;
	}

	public void check() {
		builder.getContext().getChecker().checkBeforeBuild(clazz, meta, overrides, value, components);
	}

	public void adjustMeta() {
		adjustedMeta = meta.isMeta() ? builder.setMeta(components.size()) : meta.adjustMeta(value, components);
	}

	public void reComputeSupers() {
		assert supers == null;
		supers = builder.computeAndCheckOverridesAreReached(adjustedMeta, overrides, value, components);
		if (supers.size() == 1 && supers.get(0).equalsRegardlessSupers(adjustedMeta, value, components)) {
			if (DefaultVertex.areOverridesReached(supers.get(0).getSupers(), overrides)) {
				gettable = supers.get(0);
				supers = supers.get(0).getSupers();
			}
		}
	}

	public T get() {
		assert supers != null;
		return gettable;
	}

	public T getEquiv() {
		assert adjustedMeta != null;
		return adjustedMeta.getDirectEquivInstance(value, components);
	}

	public T add() {
		assert supers != null;
		return builder.rebuildAll(null, () -> builder.buildAndPlug(clazz, adjustedMeta, supers, value, components), builder.getContext().computePotentialDependencies(adjustedMeta, supers, value, components));
	}

	public T set(T update) {
		assert update != null;
		assert supers != null;
		return builder.rebuildAll(update, () -> builder.buildAndPlug(clazz, adjustedMeta, supers, value, components), builder.getContext().computeDependencies(update));
	}

	public T update(T update) {
		assert update != null;
		assert supers != null;
		return builder.rebuildAll(update, () -> builder.getOrBuild(clazz, adjustedMeta, supers, value, components), builder.getContext().computeDependencies(update));
	}

}
