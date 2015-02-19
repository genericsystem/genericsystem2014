package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.List;

import org.genericsystem.api.defaults.DefaultContext;
import org.genericsystem.api.defaults.DefaultRoot;
import org.genericsystem.api.defaults.DefaultVertex;
import org.genericsystem.api.exception.ExistsException;
import org.genericsystem.kernel.GenericHandler.GenericHandlerFactory;

public abstract class Context<T extends DefaultVertex<T>> implements DefaultContext<T> {

	private final DefaultRoot<T> root;
	private final Checker<T> checker;
	private final Builder<T> builder;

	protected Context(DefaultRoot<T> root) {
		this.root = root;
		this.checker = buildChecker();
		this.builder = buildBuilder();
	}

	public abstract long getTs();

	protected Checker<T> buildChecker() {
		return new Checker<>(this);
	}

	protected abstract Builder<T> buildBuilder();

	protected Checker<T> getChecker() {
		return checker;
	}

	Builder<T> getBuilder() {
		return builder;
	}

	@Override
	public DefaultRoot<T> getRoot() {
		return root;
	}

	@Override
	public final T[] newTArray(int dim) {
		return builder.newTArray(dim);
	}

	protected T getMeta(int dim) {
		T adjustedMeta = GenericHandlerFactory.newMetaHandler(builder, dim).get();
		return adjustedMeta != null && adjustedMeta.getComponents().size() == dim ? adjustedMeta : null;
	}

	@Override
	public T addInstance(T meta, List<T> overrides, Serializable value, List<T> components) {
		GenericHandler<T> genericBuilder = GenericHandlerFactory.newHandlerWithComputeSupers(builder, null, meta, overrides, value, components);
		T generic = genericBuilder.get();
		if (generic != null)
			discardWithException(new ExistsException("An equivalent instance already exists : " + generic.info()));
		return genericBuilder.add();
	}

	@Override
	public T setInstance(T meta, List<T> overrides, Serializable value, List<T> components) {
		GenericHandler<T> genericBuilder = GenericHandlerFactory.newHandlerWithComputeSupers(builder, null, meta, overrides, value, components);
		T generic = genericBuilder.get();
		if (generic != null)
			return generic;
		generic = genericBuilder.getEquiv();
		return generic == null ? genericBuilder.add() : genericBuilder.set(generic);
	}

	@Override
	public T update(T update, List<T> overrides, Serializable newValue, List<T> newComponents) {
		return GenericHandlerFactory.newHandlerWithComputeSupers(builder, update.getClass(), update.getMeta(), overrides, newValue, newComponents).update(update);
	}

	@Override
	public void forceRemove(T generic) {
		GenericHandlerFactory.newRebuildHandler(builder, null, null, builder.getContext().computeDependencies(generic)).rebuildAll();
	}

	@Override
	public void remove(T generic) {
		GenericHandlerFactory.newRebuildHandler(builder, null, null, builder.getContext().computeRemoveDependencies(generic)).rebuildAll();
	}

	@Override
	public void conserveRemove(T generic) {
		GenericHandlerFactory.newRebuildHandler(builder, generic, () -> generic, builder.getContext().computeDependencies(generic)).rebuildAll();
	}

	protected abstract T plug(T generic);

	protected abstract void unplug(T generic);

	protected void triggersMutation(T oldDependency, T newDependency) {
	}

}
