package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface AncestorsService<T extends AncestorsService<T>> {

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

	@SuppressWarnings("unchecked")
	default boolean isAncestorOf(final T dependency) {
		return dependency.inheritsFrom((T) this) || dependency.getComponentsStream().filter(component -> !dependency.equals(component)).anyMatch(component -> isAncestorOf(component));
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
		// TODO improve streams comparison
		return this.getMeta().equals(service.getMeta()) && Objects.equals(this.getValue(), service.getValue()) && this.getComponentsStream().collect(Collectors.toList()).equals(service.getComponentsStream().collect(Collectors.toList()));
	}

	default boolean isPlugged() {
		return this == getPlugged();
	}

	default <U extends DependenciesService> U getPlugged() {
		U pluggedMeta = getMeta().getPlugged();
		if (pluggedMeta == null)
			return null;
		Iterator<U> it = (Iterator<U>) pluggedMeta.getInstances().iterator();
		while (it.hasNext()) {
			U next = it.next();
			if (equiv(next))
				return next;
		}
		return null;
	}

}
