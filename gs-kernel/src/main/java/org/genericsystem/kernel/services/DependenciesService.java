package org.genericsystem.kernel.services;

import org.genericsystem.kernel.Snapshot;

public interface DependenciesService<T extends DependenciesService<T>> extends AncestorsService<T> {

	Snapshot<T> getInstances();

	Snapshot<T> getInheritings();

}
