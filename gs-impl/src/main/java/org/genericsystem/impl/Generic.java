package org.genericsystem.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.genericsystem.kernel.Vertex;

public class Generic extends GenericSignature<Generic> implements GenericService<Generic> {

	private List<Generic> wrap(Stream<Vertex> stream) {
		return stream.map(this::wrap).collect(Collectors.toList());
	}

	private static List<Vertex> unwrap(Stream<Generic> stream) {
		return stream.map(Generic::unwrap).collect(Collectors.toList());
	}

	@Override
	public Generic wrap(Vertex vertex) {
		if (vertex.isRoot())
			return getRoot();
		Vertex alive = vertex.getAlive();
		Generic meta = wrap(alive.getMeta());
		return meta.buildInstance().init(meta, wrap(alive.getSupersStream()), alive.getValue(), wrap(alive.getComponentsStream()));
	}

	@Override
	public Vertex unwrap() {
		Vertex alive = getVertex();
		if (alive != null)
			return alive;
		alive = getMeta().unwrap();
		return alive.buildInstance(unwrap(getSupersStream()), getValue(), unwrap(getComponentsStream()));
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
