package org.genericsystem.cache;

import java.util.Collections;
import java.util.stream.Collectors;
import org.genericsystem.kernel.Dependencies.CompositesSnapshot;
import org.genericsystem.kernel.Dependencies.DependenciesEntry;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.Vertex;

public class Transaction<T extends GenericService<T>> implements Context<T> {

	private transient final EngineService<T> engine;

	public Transaction(EngineService<T> engine) {
		this.engine = engine;
	}

	@Override
	public boolean isAlive(T generic) {
		return generic.getVertex() != null && generic.getVertex().isAlive();
	}

	@Override
	public void simpleAdd(T generic) {
		generic.getMeta().getVertex().addInstance(generic.getSupersStream().map(g -> g.unwrap()).collect(Collectors.toList()), generic.getValue(), generic.getComponentsStream().map(g -> g.unwrap()).toArray(Vertex[]::new));
	}

	@Override
	public void simpleRemove(T generic) {
		// TODO Auto-generated method stub
	}

	@Override
	public EngineService<T> getEngine() {
		return engine;
	}

	@Override
	public Snapshot<T> getInheritings(T generic) {
		return () -> generic.getVertex() != null ? generic.unwrap().getInheritings().project(generic::wrap).iterator() : Collections.emptyIterator();
	}

	@Override
	public Snapshot<T> getInstances(T generic) {
		return () -> generic.getVertex() != null ? generic.unwrap().getInstances().project(generic::wrap).iterator() : Collections.emptyIterator();
	}

	// @Override
	@Override
	public CompositesSnapshot<T> getMetaComposites(T generic) {
		return () -> generic.getVertex() != null ? generic.unwrap().getMetaComposites().stream().map(x -> new DependenciesEntry<>(generic.wrap(x.getKey()), generic.buildDependencies(() -> x.getValue().stream().map(generic::wrap).iterator()))).iterator()
				: Collections.emptyIterator();
	}

	@Override
	public CompositesSnapshot<T> getSuperComposites(T generic) {
		return () -> generic.getVertex() != null ? generic.unwrap().getSuperComposites().stream().map(x -> new DependenciesEntry<>(generic.wrap(x.getKey()), generic.buildDependencies(() -> x.getValue().stream().map(generic::wrap).iterator()))).iterator()
				: Collections.emptyIterator();
	};

}
