package org.genericsystem.cache;

import java.util.List;
import java.util.stream.Collectors;

import org.genericsystem.impl.GenericSignature;
import org.genericsystem.kernel.Vertex;

public class Generic extends GenericSignature<Generic> implements GenericService<Generic> {

	@Override
	public Generic wrap(Vertex vertex) {
		List<Generic> supers = vertex.getAlive().getSupersStream().map(this::wrap).collect(Collectors.toList());
		if (vertex.isRoot())
			return getRoot();
		Generic wrap = wrap(vertex.getAlive().getMeta());
		return wrap.buildInstance().initFromSupers(wrap, supers, vertex.getValue(), vertex.getAlive().getComponentsStream().map(this::wrap).collect(Collectors.toList()));
	}

	@Override
	public Vertex unwrap() {
		Vertex alive = getVertex();
		if (alive != null)
			return alive;
		alive = getMeta().unwrap();
		return alive.buildInstance(getSupersStream().map(GenericService::unwrap).collect(Collectors.toList()), getValue(), getComponentsStream().map(GenericService::unwrap).collect(Collectors.toList()));
	}

	@Override
	public Generic buildInstance() {
		return new Generic();
	}

	@Override
	public void rollback() {
		getRoot().rollback();
	}

}
