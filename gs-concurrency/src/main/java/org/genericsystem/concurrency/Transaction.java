package org.genericsystem.concurrency;

import org.genericsystem.concurrency.vertex.Root;

public class Transaction<T extends AbstractGeneric<T>> extends org.genericsystem.cache.Transaction<T> implements Context<T> {

	private transient long ts;

	@SuppressWarnings("unchecked")
	public Transaction(EngineService<T> engine) {
		this(((Root) ((AbstractGeneric<T>) engine).getVertex()).pickNewTs(), engine);
	}

	public Transaction(long ts, EngineService<T> engine) {
		super(engine);
		this.ts = ts;
	}

	@Override
	public long getTs() {
		return ts;
	}

	@Override
	public boolean isAlive(T generic) {
		return generic.getLifeManager().isAlive(getTs());
	}

	// TODO clean
	// @Override
	// public Snapshot<T> getInheritings(T generic) {
	// return () -> generic.getVertex() != null ? generic.unwrap().getInheritings().project(generic::wrap).iterator() : Collections.emptyIterator();
	// }
	//
	// @Override
	// public Snapshot<T> getInstances(T generic) {
	// return () -> generic.getVertex() != null ? generic.unwrap().getInstances().project(generic::wrap).iterator() : Collections.emptyIterator();
	// }
	//
	// @Override
	// public CompositesSnapshot<T> getMetaComposites(T generic) {
	// return () -> generic.getVertex() != null ? generic.unwrap().getMetaComposites().stream().map(x -> new DependenciesEntry<>(generic.wrap(x.getKey()), generic.buildDependencies(() -> x.getValue().stream().map(generic::wrap).iterator()))).iterator()
	// : Collections.emptyIterator();
	// }
	//
	// @Override
	// public CompositesSnapshot<T> getSuperComposites(T generic) {
	// return () -> generic.getVertex() != null ? generic.unwrap().getSuperComposites().stream().map(x -> new DependenciesEntry<>(generic.wrap(x.getKey()), generic.buildDependencies(() -> x.getValue().stream().map(generic::wrap).iterator()))).iterator()
	// : Collections.emptyIterator();
	// }

}
