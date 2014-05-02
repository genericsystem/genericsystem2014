package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;
import org.genericsystem.kernel.Root;

public interface AncestorsService<T extends AncestorsService<T>> {

	T getMeta();

	Stream<T> getSupersStream();

	Stream<T> getComponentsStream();

	T[] getComponents();

	abstract Serializable getValue();

	default int getLevel() {
		return getMeta().getLevel() + 1;
	}

	default boolean isRoot() {
		return false;
	}

	default Root getRoot() {
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

	default boolean isAncestorOf(final T dependency) {
		return dependency.inheritsFrom((T) this) || dependency.getComponentsStream().filter(component -> !dependency.equals(component)).anyMatch(component -> isAncestorOf(component));
	}

	default boolean equals(T meta, Serializable value, T... components) {
		return this.getMeta().equals(meta) && Objects.equals(this.getValue(), value) && Arrays.equals(this.getComponents(), components);
	}
}
