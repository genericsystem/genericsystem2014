package org.genericsystem.defaults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
	default T getInstance(T override, Serializable value, T... components) {
		return getNonAmbiguousResult(getInstances(override, value, components).get());
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getInstance(List<T> overrides, Serializable value, T... components) {
		return getNonAmbiguousResult(getInstances(overrides, value, components).get());
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getInstances(Serializable value, T... components) {
		return getInstances(components).filter(valueFilter(value));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getInstances() {
		return getCurrentCache().getInstances((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getInstances(T... components) {
		return getInstances().filter(componentsFilter(components));
	}

	@Override
	@SuppressWarnings("unchecked")
	default Snapshot<T> getInstances(T override, Serializable value, T... components) {
		return getInstances(Collections.singletonList(override), value, components);
	}

	@Override
	@SuppressWarnings("unchecked")
	default Snapshot<T> getInstances(List<T> overrides, Serializable value, T... components) {
		List<T> supers = getCurrentCache().computeAndCheckOverridesAreReached((T) this, overrides, value, Arrays.asList(components));
		return getInstances(value, components).filter(overridesFilter(supers));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getAllInstances(Serializable value, T... components) {
		return getAllInstances(components).filter(valueFilter(value));
	}

	@Override
	default Snapshot<T> getAllInstances() {
		return () -> getAllInheritings().get().flatMap(inheriting -> inheriting.getInstances().get());
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getAllInstances(T... components) {
		return getAllInstances().filter(componentsFilter(components));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getAllInstances(T override, Serializable value, T... components) {
		return getAllInstances(Collections.singletonList(override), value, components);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getAllInstances(List<T> overrides, Serializable value, T... components) {
		List<T> supers = getCurrentCache().computeAndCheckOverridesAreReached((T) this, overrides, value, Arrays.asList(components));
		return getAllInstances(value, components).filter(overridesFilter(supers));
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getInheriting(Serializable value, T... components) {
		return getNonAmbiguousResult(getInheritings(value, components).get());
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getInheriting(T... components) {
		return getNonAmbiguousResult(getInheritings(components).get());
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
		return getInheritings().filter(componentsFilter(components));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getAllInheritings(Serializable value, T... components) {
		return getAllInheritings(components).filter(valueFilter(value));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getAllInheritings(T... components) {
		return getAllInheritings().filter(componentsFilter(components));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getAllInheritings() {
		return () -> Stream.concat(Stream.of((T) this), getInheritings().get().flatMap(inheriting -> inheriting.getAllInheritings().get())).distinct();
	}

	@Override
	default T getComposite(Serializable value) {
		return getNonAmbiguousResult(getComposites(value).get());
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
		return x -> overrides.isEmpty() ? x.getSupers().isEmpty() : filter(x.getSupers(), overrides).test(x);
	}

	@SuppressWarnings("unchecked")
	static <T extends DefaultVertex<T>> Predicate<T> componentsFilter(T... components) {
		return x -> filter(x.getComponents(), Arrays.asList(components)).test(x);
	}

	static <T extends DefaultVertex<T>> Predicate<T> filter(List<T> ancestors, List<T> ancestorsReached) {
		return attribute -> {
			List<T> attributeAncestors = new ArrayList<>(ancestors);
			for (T ancestorsReach : ancestorsReached) {
				T matchedComponent = attributeAncestors.stream().filter(attributeAncestor -> attributeAncestor.equals(ancestorsReach)).findFirst().orElse(null);
				if (matchedComponent != null)
					attributeAncestors.remove(matchedComponent);
				else
					return false;
			}
			return true;
		};
	}

	T getNonAmbiguousResult(Stream<T> stream);

}
