package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.genericsystem.api.core.IteratorSnapshot;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.defaults.DefaultContext;
import org.genericsystem.api.defaults.DefaultRoot;
import org.genericsystem.api.exception.ExistsException;
import org.genericsystem.api.exception.NotFoundException;

public abstract class Context<T extends AbstractVertex<T>> implements DefaultContext<T> {

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
	public T addInstance(T meta, List<T> overrides, Serializable value, List<T> components) {
		GenericHandler<T> genericBuilder = new GenericHandler<>(builder, null, meta, overrides, value, components);
		T generic = genericBuilder.get();
		if (generic != null)
			discardWithException(new ExistsException("An equivalent instance already exists : " + generic.info()));
		return genericBuilder.add();
	}

	@Override
	public void forceRemove(T generic) {
		new GenericHandler<>(builder, generic).forceRemove();
	}

	@Override
	public void remove(T generic) {
		new GenericHandler<>(builder, generic).remove();
	}

	@Override
	public void conserveRemove(T generic) {
		new GenericHandler<>(builder, generic).conserveRemove();
	}

	@Override
	public final boolean isAlive(T vertex) {
		return vertex != null && vertex.equals(getAlive(vertex));
	}

	protected T plug(T generic) {
		if (root.isInitialized())
			generic.getLifeManager().beginLife(getTs());
		return internalPlug(generic);
	}

	T internalPlug(T generic) {
		if (!generic.isMeta())
			indexInstance(generic.getMeta(), generic);
		generic.getSupers().forEach(superGeneric -> indexInheriting(superGeneric, generic));
		generic.getComponents().stream().filter(component -> component != null).distinct().forEach(component -> indexComposite(component, generic));
		getChecker().checkAfterBuild(true, false, generic);
		return generic;
	}

	protected void unplug(T generic) {
		getChecker().checkAfterBuild(false, false, generic);
		generic.getLifeManager().kill(getTs());
		// internalUnplug(generic);
	}

	void internalUnplug(T generic) {
		boolean result = generic != generic.getMeta() ? unIndexInstance(generic.getMeta(), generic) : true;
		if (!result)
			discardWithException(new NotFoundException(generic.info()));
		generic.getSupers().forEach(superGeneric -> unIndexInheriting(superGeneric, generic));
		generic.getComponents().stream().filter(component -> component != null).forEach(component -> unIndexComposite(component, generic));
	}

	private T getAlive(T vertex) {
		if (vertex.isRoot())
			return vertex;
		if (vertex.isMeta()) {
			T aliveSuper = getAlive(vertex.getSupers().get(0));
			return aliveSuper != null ? getInheritings(aliveSuper).get(vertex) : null;
		}
		T aliveMeta = getAlive(vertex.getMeta());
		return aliveMeta != null ? getInstances(aliveMeta).get(vertex) : null;
	}

	@SuppressWarnings("unchecked")
	protected T getMeta(int dim) {
		T adjustedMeta = getBuilder().adjustMeta((T) getRoot(), dim);
		return adjustedMeta != null && adjustedMeta.getComponents().size() == dim ? adjustedMeta : null;
	}

	@Override
	public T getInstance(T meta, List<T> overrides, Serializable value, @SuppressWarnings("unchecked") T... components) {
		List<T> componentsList = Arrays.asList(components);
		T adjustMeta = meta.adjustMeta(value, componentsList);
		if (adjustMeta.getComponents().size() < components.length)
			return null;
		return adjustMeta.getDirectInstance(overrides, value, componentsList);
	}

	@Override
	public Snapshot<T> getInstances(T vertex) {
		return new IteratorSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return vertex.getInstancesDependencies().iterator(getTs());
			}

			@Override
			public T get(Object o) {
				return vertex.getInstancesDependencies().get(o, getTs());
			}
		};
	}

	@Override
	public Snapshot<T> getInheritings(T vertex) {
		return new IteratorSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return vertex.getInheritingsDependencies().iterator(getTs());
			}

			@Override
			public T get(Object o) {
				return vertex.getInheritingsDependencies().get(o, getTs());
			}
		};
	}

	@Override
	public Snapshot<T> getComposites(T vertex) {
		return new IteratorSnapshot<T>() {
			@Override
			public Iterator<T> iterator() {
				return vertex.getCompositesDependencies().iterator(getTs());
			}

			@Override
			public T get(Object o) {
				return vertex.getCompositesDependencies().get(o, getTs());
			}
		};
	}

	protected void triggersMutation(T oldDependency, T newDependency) {
	}

	private void indexInstance(T generic, T instance) {
		generic.getInstancesDependencies().add(instance);
	}

	private void indexInheriting(T generic, T inheriting) {
		generic.getInheritingsDependencies().add(inheriting);
	}

	private void indexComposite(T generic, T composite) {
		generic.getCompositesDependencies().add(composite);
	}

	private boolean unIndexInstance(T generic, T instance) {
		return generic.getInstancesDependencies().remove(instance);
	}

	private boolean unIndexInheriting(T generic, T inheriting) {
		return generic.getInheritingsDependencies().remove(inheriting);
	}

	private boolean unIndexComposite(T generic, T composite) {
		return generic.getCompositesDependencies().remove(composite);
	}

}
