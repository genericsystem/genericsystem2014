package org.genericsystem.cache;

import java.io.Serializable;
import java.util.stream.Stream;

import org.genericsystem.kernel.Vertex;

public interface Cache {

	void addInstance(Vertex meta, Serializable value, Stream<Vertex> components);

	Stream<Vertex> getInstances(Vertex vertex);

	LifeManager getLifeManager(Vertex vertex);
}
