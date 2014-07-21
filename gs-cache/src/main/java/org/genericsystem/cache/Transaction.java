package org.genericsystem.cache;

import java.util.Collections;
import java.util.stream.Collectors;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.services.RootService;

public class Transaction<T extends AbstractGeneric<T, U, V, W>, U extends EngineService<T, U, V, W>, V extends AbstractVertex<V, W>, W extends RootService<V, W>> implements Context<T, U, V, W> {

	private transient final U engine;

	public Transaction(U engine) {
		this.engine = engine;
	}

	@Override
	public boolean isAlive(T generic) {
		AbstractVertex<?, ?> avatar = generic.getVertex();
		return avatar != null && avatar.isAlive();
	}

	@Override
	public void simpleAdd(T generic) {
		// Here we have to do smething with ts
		V vertex = generic.getMeta().getVertex();
		vertex.addInstance(generic.getSupersStream().map(g -> g.unwrap()).collect(Collectors.toList()), generic.getValue(), vertex.coerceToArray(generic.getComponentsStream().map(T::unwrap).toArray()));
	}

	// TODO : check performance
	// remove should return a boolean.
	@Override
	public boolean simpleRemove(T generic) {
		generic.getVertex().remove();
		return true;
	}

	@Override
	public U getEngine() {
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
