package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.genericsystem.kernel.Vertex;
import org.genericsystem.kernel.services.SystemPropertiesService.QuadriPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface AncestorsService<T extends AncestorsService<T>> {

	static Logger log = LoggerFactory.getLogger(AncestorsService.class);

	T getMeta();

	List<T> getComponents();

	Stream<T> getComponentsStream();

	abstract Serializable getValue();

	int getLevel();

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

	List<T> getSupers();

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

	default boolean isAlive() {
		return equals(getAlive());
	}

	default T getAlive() {
		T pluggedMeta = getMeta().getAlive();
		if (pluggedMeta == null)
			return null;
		Iterator<T> it = ((DependenciesService) pluggedMeta).getInstances().iterator();
		while (it.hasNext()) {
			T next = it.next();
			if (equiv(next))
				return next;
		}
		return null;
	}

	default T getWeakInstanceAlive(Serializable value, List<Vertex> components) {
		T pluggedMeta = getAlive();
		if (pluggedMeta == null)
			return null;
		Iterator<T> it = ((DependenciesService) pluggedMeta).getInstances().iterator();
		while (it.hasNext()) {
			T next = it.next();
			if (next.weakEquiv(pluggedMeta, value, components))
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

	default boolean equiv(AncestorsService<? extends AncestorsService<?>> service) {
		return service == null ? false : equiv(service.getMeta(), service.getValue(), service.getComponents());
	}

	default boolean equiv(AncestorsService<?> meta, Serializable value, List<? extends AncestorsService<?>> components) {
		return this.getMeta().equiv(meta) && Objects.equals(getValue(), value) && equivComponents(getComponents(), components);
	}

	static boolean equivComponents(List<? extends AncestorsService<?>> components, List<? extends AncestorsService<?>> otherComponents) {
		if (otherComponents.size() != components.size())
			return false;
		Iterator<? extends AncestorsService<?>> otherComponentsIt = otherComponents.iterator();
		return components.stream().allMatch(x -> x.equiv(otherComponentsIt.next()));
	}

	default boolean weakEquiv(AncestorsService<? extends AncestorsService<?>> service) {
		return service == null ? false : service == this ? true : weakEquiv(service.getMeta(), service.getValue(), service.getComponents());
	}

	//
	QuadriPredicate getQuadriPredicate();

	//
	// BiPredicate<List<? extends AncestorsService<?>>, List<? extends AncestorsService<?>>> getComponentsBiPredicate();
	//
	default boolean weakEquiv(AncestorsService<?> meta, Serializable value, List<? extends AncestorsService<?>> components) {
		return this.getMeta().weakEquiv(meta) && getMeta().getQuadriPredicate().test(getValue(), getComponents(), value, components);
	}
	//

}
