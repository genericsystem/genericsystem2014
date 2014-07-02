package org.genericsystem.cache;

import java.util.stream.Collectors;
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
		return () -> generic.unwrap().getInheritings().project(generic::wrap).iterator();
	}

	@Override
	public Snapshot<T> getInstances(T generic) {
		return () -> generic.unwrap().getInstances().project(generic::wrap).iterator();
	}

	@Override
	public Snapshot<T> getComposites(T generic) {
		return () -> generic.unwrap().getComposites().project(generic::wrap).iterator();
	}

	@Override
	public Snapshot<T> getCompositesByMeta(T generic, T meta) {
		return () -> generic.unwrap().getCompositesByMeta(meta.unwrap()).project(generic::wrap).iterator();
	}

	@Override
	public Snapshot<T> getCompositesBySuper(T generic, T superT) {
		return () -> generic.unwrap().getCompositesBySuper(superT.unwrap()).project(generic::wrap).iterator();
	}
}
