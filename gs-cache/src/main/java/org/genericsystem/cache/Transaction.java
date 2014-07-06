package org.genericsystem.cache;

import java.util.Collections;
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
		return () -> generic.getVertex() != null ? generic.wrap(generic.unwrap().getInheritings().stream()).iterator() : Collections.emptyIterator();
	}

	@Override
	public Snapshot<T> getInstances(T generic) {
		return () -> generic.getVertex() != null ? generic.wrap(generic.unwrap().getInstances().stream()).iterator() : Collections.emptyIterator();
	}

	@Override
	public Snapshot<T> getMetaComposites(T generic, T meta) {
		return () -> generic.getVertex() != null ? generic.wrap(generic.unwrap().getMetaComposites(meta.unwrap()).stream()).iterator() : Collections.emptyIterator();
	}

	@Override
	public Snapshot<T> getSuperComposites(T generic, T superT) {
		return () -> generic.getVertex() != null ? generic.wrap(generic.unwrap().getSuperComposites(superT.unwrap()).stream()).iterator() : Collections.emptyIterator();
	};

}
