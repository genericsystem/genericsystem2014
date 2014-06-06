package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Arrays;

import org.genericsystem.kernel.Snapshot;

public interface DependenciesService<T extends DependenciesService<T>> extends AncestorsService<T> {

	Snapshot<T> getInstances();

	Snapshot<T> getInheritings();

	default boolean instanceAlreadyExists(Serializable value, T... components) {
		return this.getInstances().stream().anyMatch(currentVertex -> currentVertex.equiv(this, value, Arrays.asList(components)));
	}
}
