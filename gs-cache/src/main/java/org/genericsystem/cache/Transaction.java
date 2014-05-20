package org.genericsystem.cache;

import org.genericsystem.impl.GenericService;
import org.genericsystem.kernel.Vertex;

public class Transaction<T extends GenericService<T>> extends AbstractContext<T> {

	private transient final Engine engine;

	public Transaction(Engine engine) {
		this.engine = engine;
	}

	@Override
	public Engine getEngine() {
		return engine;
	}

	@Override
	public boolean isAlive(T generic) {
		return generic.getVertex() != null && generic.getVertex().isAlive();
	}

	@Override
	void simpleAdd(T generic) {
		generic.getMeta().getVertex().addInstance(generic.getValue(), generic.getComponentsStream().map(g -> g.unwrap()).toArray(Vertex[]::new));
	}

	@Override
	void simpleRemove(T generic) {
		// TODO Auto-generated method stub

	}

}
