package org.genericsystem.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.services.AncestorsService;
import org.genericsystem.kernel.services.BindingService;
import org.genericsystem.kernel.services.CompositesInheritanceService;
import org.genericsystem.kernel.services.DependenciesService;
import org.genericsystem.kernel.services.DisplayService;
import org.genericsystem.kernel.services.FactoryService;
import org.genericsystem.kernel.services.InheritanceService;

public interface GenericService<T extends GenericService<T>> extends AncestorsService<T>, DependenciesService<T>, InheritanceService<T>, BindingService<T>, CompositesInheritanceService<T>, FactoryService<T>, DisplayService<T> {

	default T wrap(Vertex vertex) {
		return vertex.isRoot() ? getRoot() : build().initFromSupers(wrap(vertex.getAlive().getMeta()), vertex.getAlive().getSupersStream().map(this::wrap).collect(Collectors.toList()), vertex.getValue(),
				vertex.getAlive().getComponentsStream().map(this::wrap).collect(Collectors.toList()));
	}

	default Vertex unwrap() {
		Vertex alive = getAlive();
		if (alive != null)
			return alive;
		alive = getMeta().getAlive();
		return alive.build().initFromOverrides(alive, getSupersStream().map(GenericService::getAlive).collect(Collectors.toList()), getValue(), getComponentsStream().map(GenericService::getAlive).collect(Collectors.toList()));
	}

	@Override
	default Dependencies<T> getInstances() {
		return getAlive().getInstances().project(this::wrap, GenericService::unwrap);
	}

	@Override
	default Dependencies<T> getInheritings() {
		return getAlive().getInheritings().project(this::wrap, GenericService::unwrap);
	}

	@Override
	default CompositesDependencies<T> getMetaComposites() {
		return getAlive().getMetaComposites().projectComposites(this::wrap, GenericService::unwrap);
	}

	@Override
	default CompositesDependencies<T> getSuperComposites() {
		return getAlive().getSuperComposites().projectComposites(this::wrap, GenericService::unwrap);
	}

	@Override
	default T getInstance(Serializable value, @SuppressWarnings("unchecked") T... components) {
		Vertex alive = getAlive();
		if (alive == null)
			return null;
		alive = alive.getInstance(value, Arrays.stream(components).map(GenericService::getAlive).collect(Collectors.toList()).toArray(new Vertex[components.length]));
		if (alive == null)
			return null;
		return wrap(alive);
	}

	@Override
	default Snapshot<T> getInheritings(T origin, int level) {
		return getAlive().getInheritings(origin.getAlive(), level).project(this::wrap);
	}

	@Override
	default Snapshot<T> getMetaComposites(T meta) {
		return getAlive().getMetaComposites(meta.getAlive()).project(this::wrap);
	}

	@Override
	default Snapshot<T> getSuperComposites(T superVertex) {
		return getAlive().getSuperComposites(superVertex.getAlive()).project(this::wrap);
	}
}
