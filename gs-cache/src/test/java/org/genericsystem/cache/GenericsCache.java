package org.genericsystem.cache;

import org.genericsystem.kernel.AbstractVertex;

public interface GenericsCache {

	public Generic setGenericInSystemCache(Generic generic);

	public Generic getGenericOfVertexFromSystemCache(AbstractVertex<?, ?> vertex);
}
