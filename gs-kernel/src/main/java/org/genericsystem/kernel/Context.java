package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.api.defaults.DefaultContext;
import org.genericsystem.api.defaults.DefaultRoot;
import org.genericsystem.api.defaults.DefaultVertex;
import org.genericsystem.api.exception.UnreachableOverridesException;
import org.genericsystem.kernel.GenericHandler.AddHandler;
import org.genericsystem.kernel.GenericHandler.MetaHandler;
import org.genericsystem.kernel.GenericHandler.SetHandler;
import org.genericsystem.kernel.GenericHandler.UpdateHandler;

public abstract class Context<T extends DefaultVertex<T>> implements DefaultContext<T> {

	private final DefaultRoot<T> root;
	private final Checker<T> checker;
	private final Builder<T> builder;
	private final Restructurator<T> restructurator;

	protected Context(DefaultRoot<T> root) {
		this.root = root;
		this.checker = buildChecker();
		this.builder = buildBuilder();
		this.restructurator = buildRestructurator();
	}

	public abstract long getTs();

	protected Checker<T> buildChecker() {
		return new Checker<>(this);
	}

	protected abstract Builder<T> buildBuilder();

	protected Restructurator<T> buildRestructurator() {
		return new Restructurator<>(this);
	}

	protected Checker<T> getChecker() {
		return checker;
	}

	Builder<T> getBuilder() {
		return builder;
	}

	Restructurator<T> getRestructurator() {
		return restructurator;
	}

	@Override
	public DefaultRoot<T> getRoot() {
		return root;
	}

	@Override
	public final T[] newTArray(int dim) {
		return builder.newTArray(dim);
	}

	T[] rootComponents(int dim) {
		T[] components = newTArray(dim);
		Arrays.fill(components, root);
		return components;
	}

	List<T> computeAndCheckOverridesAreReached(T adjustedMeta, List<T> overrides, Serializable value, List<T> components) {
		List<T> supers = new ArrayList<>(new SupersComputer<>(adjustedMeta, overrides, value, components));
		if (!ApiStatics.areOverridesReached(supers, overrides))
			discardWithException(new UnreachableOverridesException("Unable to reach overrides : " + overrides + " with computed supers : " + supers));
		return supers;
	}

	T adjustMeta(T meta, int dim) {
		assert meta.isMeta();
		return meta.adjustMeta(root.getValue(), rootComponents(dim));
		// assert meta.isMeta();
		// int size = meta.getComponents().size();
		// if (size > dim)
		// return null;
		// if (size == dim)
		// return meta;
		// T directInheriting = meta.getInheritings().first();
		// return directInheriting != null && directInheriting.getComponents().size() <= dim ? adjustMeta(directInheriting, dim) : meta;
	}

	@SuppressWarnings("unchecked")
	protected T getMeta(int dim) {
		T adjustedMeta = adjustMeta((T) getRoot(), dim);
		return adjustedMeta != null && adjustedMeta.getComponents().size() == dim ? adjustedMeta : null;
	}

	T setMeta(int dim) {
		return new MetaHandler<>(this, dim).resolve();
		// T root = (T) getRoot();
		// T adjustedMeta = adjustMeta(root, dim);
		// if (adjustedMeta.getComponents().size() == dim)
		// return adjustedMeta;
		// T[] components = rootComponents(dim);
		// return getRestructurator().rebuildAll(null, () -> getBuilder().buildAndPlug(null, null, Collections.singletonList(adjustedMeta), root.getValue(), Arrays.asList(components)),
		// computePotentialDependencies(adjustedMeta, Collections.singletonList(adjustedMeta), root.getValue(), Arrays.asList(components)));
	}

	@Override
	public T addInstance(T meta, List<T> overrides, Serializable value, List<T> components) {
		return new AddHandler<>(this, null, meta, overrides, value, components).resolve();
	}

	@Override
	public T setInstance(T meta, List<T> overrides, Serializable value, List<T> components) {
		return new SetHandler<>(this, null, meta, overrides, value, components).resolve();

	}

	@Override
	public T update(T update, List<T> overrides, Serializable newValue, List<T> newComponents) {
		return new UpdateHandler<>(this, update.getClass(), update, update.getMeta(), overrides, newValue, newComponents).resolve();
	}

	@Override
	public void forceRemove(T generic) {
		getRestructurator().rebuildAll(null, null, builder.getContext().computeDependencies(generic));
	}

	@Override
	public void remove(T generic) {
		getRestructurator().rebuildAll(null, null, builder.getContext().computeRemoveDependencies(generic));
	}

	@Override
	public void conserveRemove(T generic) {
		getRestructurator().rebuildAll(generic, () -> generic, builder.getContext().computeDependencies(generic));
	}

	protected abstract T plug(T generic);

	protected abstract void unplug(T generic);

	protected void triggersMutation(T oldDependency, T newDependency) {
	}

}
