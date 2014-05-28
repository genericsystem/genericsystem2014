package org.genericsystem.cache;

import java.util.stream.Collectors;

import org.genericsystem.kernel.Vertex;

public class Transaction<T extends GenericService<T>> extends AbstractContext<T> {

	private transient final T engine;

	public Transaction(T engine) {
		this.engine = engine;
	}

	@Override
	public boolean isAlive(T generic) {
		return generic.getVertex() != null && generic.getVertex().isAlive();
	}

	@Override
	void simpleAdd(T generic) {
		generic.getMeta().getVertex().addInstance(generic.getSupersStream().map(g -> g.unwrap()).collect(Collectors.toList()), generic.getValue(), generic.getComponentsStream().map(g -> g.unwrap()).toArray(Vertex[]::new));
	}

	@Override
	void simpleRemove(T generic) {
		// TODO Auto-generated method stub

	}

	@Override
	public T getEngine() {
		return engine;
	}

}
