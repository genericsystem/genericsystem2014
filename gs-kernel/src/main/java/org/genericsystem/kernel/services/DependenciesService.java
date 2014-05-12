package org.genericsystem.kernel.services;

import org.genericsystem.kernel.Dependencies.DependenciesEntry;
import org.genericsystem.kernel.Snapshot;

public interface DependenciesService<T extends AncestorsService<T>> extends AncestorsService<T> {

	Snapshot<T> getInstances();

	Snapshot<T> getInheritings();

	Snapshot<? extends DependenciesEntry<T>> getMetaComposites();

	Snapshot<? extends DependenciesEntry<T>> getSuperComposites();

}
