package org.genericsystem.api.defaults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.genericsystem.api.core.IVertex;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.AmbiguousSelectionException;

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
	default T getInstance(List<T> overrides, Serializable value, T... components) {
		return getCurrentCache().getInstance((T) this, overrides, value, components);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getInstances() {
		return getCurrentCache().getInstances((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getInheritings() {
		return getCurrentCache().getInheritings((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getComposites() {
		return getCurrentCache().getComposites((T) this);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getAllInheritings() {
		return () -> Stream.concat(Stream.of((T) this), getInheritings().get().flatMap(inheriting -> inheriting.getAllInheritings().get())).distinct();
	}

	@Override
	default Snapshot<T> getAllInstances() {
		return () -> getAllInheritings().get().flatMap(inheriting -> inheriting.getInstances().get());
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getInstance(T superT, Serializable value, T... components) {
		return getInstance(Collections.singletonList(superT), value, components);
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getInstance(Serializable value, T... components) {
		return getInstance(Collections.emptyList(), value, components);
	}

	public static <T extends DefaultVertex<T>> Predicate<T> valueFilter(Serializable value) {
		return attribute -> Objects.equals(attribute.getValue(), value);
	}

	@SuppressWarnings("unchecked")
	default Predicate<T> componentsFilter(T... components) {
		return attribute -> {
			int subIndex = 0;
			loop: for (T component : components) {
				for (; subIndex < attribute.getComponents().size(); subIndex++) {
					T subTarget = attribute.getComponents().get(subIndex);
					if (subTarget.isSpecializationOf(component)) {
						if (isSingularConstraintEnabled(subIndex))
							return true;
						subIndex++;
						continue loop;
					}
				}
				return false;
			}
			return true;
		};
	}

	default Predicate<T> componentsFilter2(T... components) {
		return attribute -> {
			List<T> attributeComps = new ArrayList<>(attribute.getComponents());
			for (T component : components) {
				T matchedComponent = attributeComps.stream().filter(subTarget -> subTarget.isSpecializationOf(component)).findFirst().orElse(null);
				if (matchedComponent != null)
					attributeComps.remove(matchedComponent);
				else
					return false;
			}
			return true;
		};
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getAttribute(Serializable value, T... targets) {
		return getNonAmbiguousResult(getAttributes().get().filter(valueFilter(value)).filter(componentsFilter(targets)).iterator());
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getHolder(T attribute, Serializable value, T... targets) {
		return getNonAmbiguousResult(getHolders(attribute).get().filter(valueFilter(value)).filter(componentsFilter(targets)).iterator());
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getRelation(Serializable value, T... targets) {
		return getNonAmbiguousResult(getRelations().get().filter(valueFilter(value)).filter(componentsFilter(targets)).iterator());
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getLink(T relation, Serializable value, T... targets) {
		return getNonAmbiguousResult(getLinks(relation).get().filter(valueFilter(value)).filter(componentsFilter(targets)).iterator());
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getHolder(T attribute, T... targets) {
		return getNonAmbiguousResult(getHolders(attribute).get().filter(componentsFilter(targets)).iterator());
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getLink(T relation, T... targets) {
		return getNonAmbiguousResult(getLinks(relation).get().filter(componentsFilter2(addThisToTargets(targets))).iterator());
	}

	default T getNonAmbiguousResult(Iterator<T> iterator) {
		if (!iterator.hasNext())
			return null;
		T result = iterator.next();
		if (iterator.hasNext())
			getCurrentCache().discardWithException(new AmbiguousSelectionException(result.info() + " " + iterator.next().info()));
		return result;
	}

	default T getNonAmbiguousResult(Stream<T> stream) {
		Iterator<T> iterator = stream.iterator();
		if (!iterator.hasNext())
			return null;
		T result = iterator.next();
		if (iterator.hasNext())
			getCurrentCache().discardWithException(new AmbiguousSelectionException(result.info() + " " + iterator.next().info()));
		return result;
	}
}
