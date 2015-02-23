package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.List;
import org.genericsystem.api.defaults.DefaultVertex;

public class GenericHandler<T extends DefaultVertex<T>> {
	private final Context<T> context;
	private final Class<?> clazz;
	private final T meta;
	private T adjustedMeta;
	private List<T> overrides;
	private List<T> supers;
	private final Serializable value;
	private final List<T> components;
	private T gettable;

	public GenericHandler(Context<T> context, Class<?> clazz, T meta, List<T> overrides, Serializable value, List<T> components) {
		assert overrides != null;
		this.context = context;
		this.clazz = clazz;
		this.meta = meta;
		this.overrides = overrides;
		this.value = value;
		this.components = components;
		check();
		adjustMeta();
		reComputeSupers();
	}

	public GenericHandler(Context<T> context, T gettable) {
		this.context = context;
		this.clazz = gettable.getClass();
		this.meta = gettable.getMeta();
		this.supers = gettable.getSupers();
		this.value = gettable.getValue();
		this.components = gettable.getComponents();
		this.gettable = gettable;
	}

	public void check() {
		context.getChecker().checkBeforeBuild(clazz, meta, overrides, value, components);
	}

	public void adjustMeta() {
		adjustedMeta = meta.isMeta() ? context.setMeta(components.size()) : meta.adjustMeta(value, components);
	}

	public void reComputeSupers() {
		assert supers == null;
		supers = context.getBuilder().computeAndCheckOverridesAreReached(adjustedMeta, overrides, value, components);
	}

	public T get() {
		assert supers != null;
		if (gettable == null)
			gettable = adjustedMeta.getDirectInstance(supers, value, components);
		return gettable;
	}

	public T getEquiv() {
		assert adjustedMeta != null;
		return adjustedMeta.getDirectEquivInstance(value, components);
	}

	public T add() {
		assert supers != null;
		return context.getRestructurator().rebuildAll(null, () -> context.getBuilder().buildAndPlug(clazz, adjustedMeta, supers, value, components), context.computePotentialDependencies(adjustedMeta, supers, value, components));
	}

	public T set(T update) {
		assert update != null;
		assert supers != null;
		return context.getRestructurator().rebuildAll(update, () -> context.getBuilder().buildAndPlug(clazz, adjustedMeta, supers, value, components), context.computeDependencies(update));
	}

	public T update(T update) {
		assert update != null;
		assert supers != null;
		// assert !supers.contains(update);
		return context.getRestructurator().rebuildAll(update, () -> context.getBuilder().getOrBuild(clazz, adjustedMeta, supers, value, components), context.computeDependencies(update));
	}

}
