package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.genericsystem.kernel.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface AncestorsService<T extends AncestorsService<T>> extends Comparable<AncestorsService<T>> {

	static Logger log = LoggerFactory.getLogger(AncestorsService.class);

	T getMeta();

	List<T> getComponents();

	Stream<T> getComponentsStream();

	abstract Serializable getValue();

	default int getLevel() {
		return 0;
	}

	default boolean isRoot() {
		return false;
	}

	default T getRoot() {
		return getMeta().getRoot();
	}

	default boolean isMeta() {
		return getLevel() == 0;
	}

	default boolean isStructural() {
		return getLevel() == 1;
	}

	default boolean isFactual() {
		return getLevel() == 2;
	}

	Stream<T> getSupersStream();

	default boolean inheritsFrom(T superVertex) {
		if (this == superVertex || equals(superVertex))
			return true;
		if (getLevel() != superVertex.getLevel())
			return false;
		return getSupersStream().anyMatch(vertex -> vertex.inheritsFrom(superVertex));
	}

	default boolean isInstanceOf(T metaVertex) {
		return getMeta().inheritsFrom(metaVertex);
	}

	default boolean isAttributeOf(T vertex) {
		return isRoot() || getComponentsStream().anyMatch(component -> vertex.inheritsFrom(component) || vertex.isInstanceOf(component));
	}

	default boolean equiv(AncestorsService<? extends AncestorsService<?>> service) {
		return service == null ? false : equiv(service.getMeta(), service.getValue(), service.getComponents());
	}

	default boolean equiv(AncestorsService<?> ancestorsService, Serializable value, List<? extends AncestorsService<?>> components) {
		return this.getMeta().equiv(ancestorsService) && Objects.equals(getValue(), value) && equivComponents(components);
	}

	default boolean equivComponents(List<? extends AncestorsService<?>> components) {
		if (getComponentsStream().count() != components.size())
			return false;
		Iterator<? extends AncestorsService<?>> otherComponents = components.iterator();
		return getComponentsStream().allMatch(x -> x.equiv(otherComponents.next()));
	}

	default boolean isAlive() {
		return equals(getAlive());
	}

	default T getAlive() {
		T pluggedMeta = getMeta().getAlive();
		if (pluggedMeta == null)
			return null;
		Iterator<T> it = ((DependenciesService<T>) pluggedMeta).getInstances().iterator();
		while (it.hasNext()) {
			T next = it.next();
			if (equiv(next))
				return next;
		}
		return null;
	}

	default Vertex getVertex() {
		Vertex pluggedMeta = getMeta().getVertex();
		if (pluggedMeta == null)
			return null;
		Iterator<Vertex> it = pluggedMeta.getInstances().iterator();
		while (it.hasNext()) {
			Vertex next = it.next();
			if (equiv(next))
				return next;
		}
		return null;
	}
}
