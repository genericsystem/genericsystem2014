package org.genericsystem.cache;

import java.util.Collections;
import java.util.stream.Collectors;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.Vertex;

public class Transaction<T extends AbstractGeneric<T>> implements Context<T> {

	private transient final EngineService<T> engine;

	public Transaction(EngineService<T> engine) {
		this.engine = engine;
	}

	@Override
	public boolean isAlive(T generic) {
		Vertex avatar = generic.getVertex();
		return avatar != null && avatar.isAlive();
	}

	@Override
	public void simpleAdd(T generic) {
		generic.getMeta().getVertex().addInstance(generic.getSupersStream().map(g -> g.unwrap()).collect(Collectors.toList()), generic.getValue(), generic.getComponentsStream().map(g -> g.unwrap()).toArray(Vertex[]::new));
	}

	// TODO : check performance
	// remove should return a boolean.
	@Override
	public boolean simpleRemove(T generic) {
		generic.getVertex().remove();
		return true;
	}

	@Override
	public EngineService<T> getEngine() {
		return engine;
	}

	@Override
	public Snapshot<T> getInheritings(T generic) {
		return () -> generic.getVertex() != null ? generic.unwrap().getInheritings().stream().map(generic::wrap).iterator() : Collections.emptyIterator();
	}

	@Override
	public Snapshot<T> getInstances(T generic) {
		return () -> generic.getVertex() != null ? generic.unwrap().getInstances().stream().map(generic::wrap).iterator() : Collections.emptyIterator();
	}

	@Override
	public Snapshot<T> getMetaComposites(T generic, T meta) {
		return () -> generic.getVertex() != null ? generic.unwrap().getMetaComposites(meta.unwrap()).stream().map(generic::wrap).iterator() : Collections.emptyIterator();
	}

	@Override
	public Snapshot<T> getSuperComposites(T generic, T superT) {
		return () -> generic.getVertex() != null ? generic.unwrap().getSuperComposites(superT.unwrap()).stream().map(generic::wrap).iterator() : Collections.emptyIterator();
	};

}
