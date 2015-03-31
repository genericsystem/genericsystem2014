package org.genericsystem.defaults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.genericsystem.api.core.IVertex;
import org.genericsystem.api.core.Snapshot;

public interface DefaultDependencies<T extends DefaultVertex<T>> extends IVertex<T> {

	@SuppressWarnings("unchecked")
	@Override
	default boolean isAlive() {
		return getCurrentCache().isAlive((T) this);
	}

	@Override
	default boolean isAncestorOf(T dependency) {
		return equals(dependency) || (!dependency.isMeta() && isAncestorOf(dependency.getMeta())) || dependency.getSupers().stream().anyMatch(this::isAncestorOf) || dependency.getComponents().stream().filter(x -> x != null).anyMatch(this::isAncestorOf);
	}

	@Override
	default DefaultContext<T> getCurrentCache() {
		return (DefaultContext<T>) getRoot().getCurrentCache();
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getInstance(Serializable value, T... components) {
		return getNonAmbiguousResult(getInstances(value, components).get());
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getInstance(T... components) {
		return getNonAmbiguousResult(getInstances(components).get());
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getInstance(T superT, Serializable value, T... components) {
		return getInstance(Collections.singletonList(superT), value, components);
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getInstance(List<T> overrides, Serializable value, T... components) {
		return getNonAmbiguousResult(getInstances(value, components).filter(overridesFilter(overrides)).get());
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getInstances(Serializable value, T... components) {
		return getInstances(components).filter(valueFilter(value));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getInstances(T... components) {
		return getCurrentCache().getInstances((T) this).filter(componentsFilter((x, y) -> x.equals(y), components));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getAllInstances(Serializable value, T... components) {
		return getAllInstances(components).filter(valueFilter(value));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getAllInstances(T... components) {
		return () -> getAllInheritings().get().flatMap(inheriting -> inheriting.getInstances().get()).filter(componentsFilter((x, y) -> x.equals(y), components));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getInheritings(Serializable value, T... components) {
		return getInheritings(components).filter(valueFilter(value));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getInheritings() {
		return getCurrentCache().getInheritings((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getInheritings(T... components) {
		return getCurrentCache().getInheritings((T) this).filter(componentsFilter((x, y) -> x.equals(y), components));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getAllInheritings(Serializable value, T... components) {
		return getAllInheritings(components).filter(valueFilter(value));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getAllInheritings(T... components) {
		return () -> Stream.concat(Stream.of((T) this), getInheritings(components).get().flatMap(inheriting -> inheriting.getAllInheritings().get())).distinct();
	}

	@Override
	default Snapshot<T> getComposites(Serializable value) {
		return getComposites().filter(valueFilter(value));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getComposites() {
		return getCurrentCache().getComposites((T) this);
	}

	static <T extends DefaultVertex<T>> Predicate<T> valueFilter(Serializable value) {
		return attribute -> Objects.equals(attribute.getValue(), value);
	}

	static <T extends DefaultVertex<T>> Predicate<T> overridesFilter(List<T> overrides) {
		return attribute -> {
			List<T> attributeSupers = new ArrayList<>(attribute.getSupers());
			for (T override : overrides) {
				T matchedSuper = attributeSupers.stream().filter(attributeSuper -> attributeSuper.isSpecializationOf(override)).findFirst().orElse(null);
				if (matchedSuper != null)
					attributeSupers.remove(matchedSuper);
				else
					return false;
			}
			if (overrides.isEmpty())
				return attributeSupers.isEmpty();
			return true;
		};
	}

	@SuppressWarnings("unchecked")
	static <T extends DefaultVertex<T>> Predicate<T> componentsFilter(BiFunction<T, T, Boolean> componentsFilter, T... components) {
		return attribute -> {
			List<T> attributeComps = new ArrayList<>(attribute.getComponents());
			for (T component : components) {
				T matchedComponent = attributeComps.stream().filter(attributeComponent -> componentsFilter.apply(attributeComponent, component)).findFirst().orElse(null);
				if (matchedComponent != null)
					attributeComps.remove(matchedComponent);
				else
					return false;
			}
			return true;
		};
	}

	T getNonAmbiguousResult(Stream<T> stream);

}
