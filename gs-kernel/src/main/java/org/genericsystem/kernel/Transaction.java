package org.genericsystem.kernel;

import java.util.Iterator;
import java.util.function.Supplier;

import org.genericsystem.api.core.IteratorSnapshot;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.defaults.DefaultRoot;
import org.genericsystem.api.exception.NotFoundException;

public class Transaction<T extends AbstractVertex<T>> extends Context<T> {

	private final long ts;

	protected Transaction(DefaultRoot<T> root, long ts) {
		super(root);
		this.ts = ts;
	}

	protected Transaction(DefaultRoot<T> root) {
		this(root, root.pickNewTs());
	}

	@Override
	public final long getTs() {
		return ts;
	}

	public void apply(Iterable<T> removes, Iterable<T> adds) {
		for (T generic : removes)
			unplug(generic);
		for (T generic : adds)
			plug(generic);
	}

	private class AbstractIteratorSnapshot implements IteratorSnapshot<T> {

		private final Supplier<Dependencies<T>> dependenciesSupplier;

		private AbstractIteratorSnapshot(Supplier<Dependencies<T>> dependenciesSupplier) {
			this.dependenciesSupplier = dependenciesSupplier;
		}

		@Override
		public Iterator<T> iterator() {
			return dependenciesSupplier.get().iterator(getTs());
		}

		@Override
		public T get(Object o) {
			return dependenciesSupplier.get().get(o, getTs());
		}
	}

	@Override
	public Snapshot<T> getInstances(T vertex) {
		return new AbstractIteratorSnapshot(() -> vertex.getInstancesDependencies());
	}

	@Override
	public Snapshot<T> getInheritings(T vertex) {
		return new AbstractIteratorSnapshot(() -> vertex.getInheritingsDependencies());
	}

	@Override
	public Snapshot<T> getComposites(T vertex) {
		return new AbstractIteratorSnapshot(() -> vertex.getCompositesDependencies());
	}

	@Override
	protected T plug(T generic) {
		if (getRoot().isInitialized())
			generic.getLifeManager().beginLife(getTs());
		return internalPlug(generic);
	}

	@Override
	protected void unplug(T generic) {
		getChecker().checkAfterBuild(false, false, generic);
		generic.getLifeManager().kill(getTs());
		internalUnplug(generic);
	}

	T internalPlug(T generic) {
		if (!generic.isMeta())
			generic.getMeta().getInstancesDependencies().add(generic);
		generic.getSupers().forEach(superGeneric -> superGeneric.getInheritingsDependencies().add(generic));
		generic.getComponents().stream().filter(component -> component != null).distinct().forEach(component -> component.getCompositesDependencies().add(generic));
		getChecker().checkAfterBuild(true, false, generic);
		return generic;
	}

	void internalUnplug(T generic) {
		boolean result = generic != generic.getMeta() ? generic.getMeta().getInstancesDependencies().remove(generic) : true;
		if (!result)
			discardWithException(new NotFoundException(generic.info()));
		generic.getSupers().forEach(superGeneric -> superGeneric.getInheritingsDependencies().remove(generic));
		generic.getComponents().stream().filter(component -> component != null).forEach(component -> component.getCompositesDependencies().remove(generic));
	}

}
