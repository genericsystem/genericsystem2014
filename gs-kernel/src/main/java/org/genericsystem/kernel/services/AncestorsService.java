package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

import org.genericsystem.kernel.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface AncestorsService<T extends AncestorsService<T>> {

	static Logger log = LoggerFactory.getLogger(AncestorsService.class);

	T getMeta();

	Stream<T> getComponentsStream();

	abstract Serializable getValue();

	default int getLevel() {
		return getMeta().getLevel() + 1;
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

	default boolean equiv(AncestorsService<?> service) {
		if (this == service)
			return true;
		return this.getMeta().equiv(service.getMeta()) && Objects.equals(getValue(), service.getValue()) && equivComponents(service);
	}

	default boolean equivComponents(AncestorsService<?> service) {
		if (getComponentsStream().count() != service.getComponentsStream().count())
			return false;
		Iterator<?> otherComponents = service.getComponentsStream().iterator();
		return getComponentsStream().allMatch(x -> x.equiv((AncestorsService<?>) otherComponents.next()));
	}

	default boolean isAlive() {
		T alive = getAlive();
		return alive != null ? equiv(alive) : false;
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
