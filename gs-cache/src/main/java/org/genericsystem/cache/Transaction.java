package org.genericsystem.cache;

import java.util.stream.Collectors;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.Vertex;

public class Transaction<T extends GenericService<T>> extends AbstractContext<T> {

	private transient final EngineService<T> engine;

	public Transaction(EngineService<T> engine) {
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
	public EngineService<T> getEngine() {
		return engine;
	}

	@Override
	public Dependencies<T> getInheritings(T generic) {
		return generic.unwrap().getInheritings().project(generic::wrap, org.genericsystem.impl.GenericService::unwrap);
	}

	@Override
	public Dependencies<T> getInstances(T generic) {
		return generic.unwrap().getInstances().project(generic::wrap, org.genericsystem.impl.GenericService::unwrap);
	}

	@Override
	public CompositesDependencies<T> getMetaComposites(T generic) {
		return generic.unwrap().getMetaComposites().projectComposites(generic::wrap, org.genericsystem.impl.GenericService::unwrap);
	}

	@Override
	public CompositesDependencies<T> getSuperComposites(T generic) {
		return generic.unwrap().getSuperComposites().projectComposites(generic::wrap, org.genericsystem.impl.GenericService::unwrap);
	}

}
