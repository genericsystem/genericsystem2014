package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.List;
import org.genericsystem.api.defaults.DefaultContext;
import org.genericsystem.api.defaults.DefaultRoot;
import org.genericsystem.api.defaults.DefaultVertex;
import org.genericsystem.api.exception.ExistsException;

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

	protected Builder<T> buildBuilder() {
		return new Builder<>(this);
	}

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

	@Override
	public T addInstance(T meta, List<T> overrides, Serializable value, List<T> components) {
		GenericHandler<T> genericBuilder = new GenericHandler<>(builder, null, meta, overrides, value, components);
		T generic = genericBuilder.get();
		if (generic != null)
			discardWithException(new ExistsException("An equivalent instance already exists : " + generic.info()));
		return genericBuilder.add();
	}

	@Override
	public T setInstance(T meta, List<T> overrides, Serializable value, List<T> components) {
		GenericHandler<T> genericBuilder = new GenericHandler<>(builder, null, meta, overrides, value, components);
		T generic = genericBuilder.get();
		if (generic != null)
			return generic;
		generic = genericBuilder.getEquiv();
		return generic == null ? genericBuilder.add() : genericBuilder.set(generic);
	}

	@Override
	public T update(T update, List<T> overrides, Serializable newValue, List<T> newComponents) {
		return new GenericHandler<>(builder, update.getClass(), update.getMeta(), overrides, newValue, newComponents).update(update);
	}

	@Override
	public void forceRemove(T generic) {
		getBuilder().rebuildAll(null, null, builder.getContext().computeDependencies(generic));
	}

	@Override
	public void remove(T generic) {
		builder.rebuildAll(null, null, builder.getContext().computeRemoveDependencies(generic));
	}

	@Override
	public void conserveRemove(T generic) {
		builder.rebuildAll(generic, () -> generic, builder.getContext().computeDependencies(generic));
	}

	protected T begin(T generic) {
		if (root.isInitialized())
			generic.getLifeManager().beginLife(getTs());
	}

	protected T kill(T generic) {
		generic.getLifeManager().kill(getTs());
	}

	protected abstract T plug(T generic);

	protected void unplug(T generic) {
		getChecker().checkAfterBuild(false, false, generic);
		kill(generic);
		internalUnplug(generic);
	}

	protected abstract void internalUnplug(T generic);

	@SuppressWarnings("unchecked")
	protected T getMeta(int dim) {
		T adjustedMeta = getBuilder().adjustMeta((T) getRoot(), dim);
		return adjustedMeta != null && adjustedMeta.getComponents().size() == dim ? adjustedMeta : null;
	}

	protected void triggersMutation(T oldDependency, T newDependency) {}

}
