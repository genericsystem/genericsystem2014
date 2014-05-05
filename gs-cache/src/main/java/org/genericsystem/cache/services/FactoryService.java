package org.genericsystem.cache.services;

import org.genericsystem.cache.CacheVertex;
import org.genericsystem.kernel.Vertex;

public interface FactoryService extends org.genericsystem.kernel.services.FactoryService {

	public static interface CacheFactory extends Factory {

		default CacheVertex buildCacheVertex(Vertex vertex) {
			return new CacheVertex(vertex);
		}
	}
}
