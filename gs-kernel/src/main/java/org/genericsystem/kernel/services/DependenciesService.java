package org.genericsystem.kernel.services;

import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.Vertex;

public interface DependenciesService extends AncestorsService<Vertex> {

	Snapshot<Vertex> getInstances();

	Snapshot<Vertex> getInheritings();

	Snapshot<Vertex> getMetaComposites(Vertex meta);

	Snapshot<?> getMetaComposites();

	Snapshot<?> getSuperComposites(Vertex superVertex);

	Snapshot<?> getSuperComposites();
}