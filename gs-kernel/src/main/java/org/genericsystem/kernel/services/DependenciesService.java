package org.genericsystem.kernel.services;

import org.genericsystem.kernel.Snapshot;

public interface DependenciesService<T extends AncestorsService<T>> extends AncestorsService<T> {

	Snapshot<T> getInstances();

	Snapshot<T> getInheritings();

	//Snapshot<T> getMetaComposites(T meta);

	//Snapshot<?> getSuperComposites(T superVertex);
}
