package org.genericsystem.kernel.services;

import java.util.List;
import java.util.stream.Stream;

public interface AncestorsService<T extends AncestorsService<T>> extends SignatureService<T> {

	default int getLevel() {
		return isRoot() || getValue().equals(getRoot().getValue()) || getComponentsStream().allMatch(c -> c.isRoot()) ? 0 : getMeta().getLevel() + 1;
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

	List<T> getSupers();

	default Stream<T> getSupersStream() {
		return getSupers().stream();
	}

	// default boolean hasSuperSameMeta() {
	// return getSupersStream().anyMatch(x -> getMeta().equals(x.getMeta()));
	// }

	// default Stream<T> getSuprasStream() {
	// return isRoot() || hasSuperSameMeta() ? getSupersStream() : Stream.concat(Stream.of(getMeta()), getSupersStream());
	// }

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

	default boolean isSpecializationOf(T supra) {
		return getLevel() == supra.getLevel() ? inheritsFrom(supra) : (getLevel() > supra.getLevel() && getMeta().isSpecializationOf(supra));
	}

	default boolean isAttributeOf(T vertex) {
		return isRoot() || getComponentsStream().anyMatch(component -> vertex.isSpecializationOf(component));
	}
}