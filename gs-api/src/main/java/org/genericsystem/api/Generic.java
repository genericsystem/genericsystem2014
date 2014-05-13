package org.genericsystem.api;

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

public interface Generic extends AncestorsService<Generic>, DependenciesService<Generic>, DisplayService<Generic>, BindingService<Generic>, FactoryService<Generic>, CompositesInheritanceService<Generic>, InheritanceService<Generic> {

	final static Generic[] EMPTY_ARRAY = new Generic[] {};

	@Override
	default Generic[] getEmptyArray() {
		return EMPTY_ARRAY;
	}

	default Generic wrap(Vertex vertex) {
		return vertex.isRoot() ? getRoot() : build(wrap(vertex.getAlive().getMeta()), vertex.getAlive().getSupersStream().map(this::wrap), vertex.getValue(), vertex.getAlive().getComponentsStream().map(this::wrap));
	}

	default Vertex unwrap() {
		Vertex alive = getAlive();
		if (alive != null)
			return alive;
		alive = getMeta().getAlive();
		return alive.build(alive, getSupersStream().map(Generic::getAlive), getValue(), getComponentsStream().map(Generic::getAlive));
	}

	@Override
	default Dependencies<Generic> getInstances() {
		return getAlive().getInstances().project(this::wrap, Generic::unwrap);
	}

	@Override
	default Dependencies<Generic> getInheritings() {
		return getAlive().getInheritings().project(this::wrap, Generic::unwrap);
	}

	@Override
	default CompositesDependencies<Generic> getMetaComposites() {
		return getAlive().getMetaComposites().projectComposites(this::wrap, Generic::unwrap);
	}

	@Override
	default CompositesDependencies<Generic> getSuperComposites() {
		return getAlive().getSuperComposites().projectComposites(this::wrap, Generic::unwrap);
	}

	@Override
	default Generic getInstance(Serializable value, Generic... components) {
		Vertex alive = getAlive();
		if (alive == null)
			return null;
		alive = alive.getInstance(value, Arrays.stream(components).map(Generic::getAlive).collect(Collectors.toList()).toArray(new Vertex[components.length]));
		if (alive == null)
			return null;
		return wrap(alive);
	}

	@Override
	default Snapshot<Generic> getInheritings(Generic origin, int level) {
		return getAlive().getInheritings(origin.getAlive(), level).project(this::wrap);
	}
}
