package org.genericsystem.kernel.services;

import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.Snapshot;

public interface DependenciesService<T extends AncestorsService<T>> extends AncestorsService<T> {

	<U extends Snapshot<T>> U getInstances();

	<U extends Snapshot<T>> U getInheritings();

	CompositesDependencies<T> getMetaComposites();

	CompositesDependencies<T> getSuperComposites();
}
