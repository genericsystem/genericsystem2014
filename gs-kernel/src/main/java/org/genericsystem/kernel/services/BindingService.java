package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.exceptions.ExistsException;
import org.genericsystem.kernel.exceptions.NotFoundException;

public interface BindingService<T extends AncestorsService<T>> extends AncestorsService<T>, DependenciesService<T>, FactoryService<T>, InheritanceService<T>, ExceptionAdviserService<T> {

	T[] getEmptyArray();

	default T addInstance(Serializable value, T... components) {
		return addInstance(getEmptyArray(), value, components);
	}

	default T addInstance(T[] overrides, Serializable value, T... components) {
		T T = getInstance(overrides, value, components);
		if (T != null)
			rollbackAndThrowException(new ExistsException(((DisplayService<T>) T).info()));
		return ((BindingService<T>) getFactory().build((T) this, overrides, value, components)).plug();
	}

	default T setInstance(Serializable value, T... components) {
		return setInstance(getEmptyArray(), value, components);
	}

	default T setInstance(T[] overrides, Serializable value, T... components) {
		T T = getInstance(overrides, value, components);
		if (T != null)
			return T;
		return ((BindingService<T>) getFactory().build((T) this, overrides, value, components)).plug();
	}

	default T getInstance(Serializable value, T... components) {
		// TODO KK
		return (T) getFactory().build((T) this, getEmptyArray(), value, components).getAlive();
	}

	default T plug() {
		T t = ((DependenciesService<T>) getMeta()).<Dependencies<T>> getInstances().set((T) this);
		getSupersStream().forEach(superGeneric -> ((DependenciesService<T>) superGeneric).<Dependencies<T>> getInheritings().set((T) this));
		getComponentsStream().forEach(component -> ((DependenciesService<T>) component).getMetaComposites().setByIndex(getMeta(), (T) this));
		getSupersStream().forEach(superGeneric -> getComponentsStream().forEach(component -> ((DependenciesService<T>) component).getSuperComposites().setByIndex(superGeneric, (T) this)));

		// assert getSupersStream().allMatch(superGeneric -> this == superGeneric.getInheritings().get((T) this));
		// assert Arrays.stream(getComponents()).allMatch(component -> this == component.getMetaComposites(getMeta()).get((T) this));
		// assert getSupersStream().allMatch(superGeneric -> Arrays.stream(getComponents()).allMatch(component -> component == component.getSuperComposites(superGeneric).get((T) this)));

		return t;
	}

	default boolean unplug() {
		boolean result = ((DependenciesService<T>) getMeta()).<Dependencies<T>> getInstances().remove((T) this);
		if (!result)
			rollbackAndThrowException(new NotFoundException(((DisplayService<T>) this).info()));
		getSupersStream().forEach(superGeneric -> ((DependenciesService<T>) superGeneric).<Dependencies<T>> getInheritings().remove((T) this));
		getComponentsStream().forEach(component -> ((DependenciesService<T>) component).getMetaComposites().removeByIndex(getMeta(), (T) this));
		getSupersStream().forEach(superGeneric -> getComponentsStream().forEach(component -> ((DependenciesService<T>) component).getSuperComposites().removeByIndex(superGeneric, (T) this)));
		return result;
	}

	default T getInstance(T[] supers, Serializable value, T... components) {
		T result = getInstance(value, components);
		if (result != null && Arrays.stream(supers).allMatch(superT -> result.inheritsFrom(result)))
			return result;
		return null;
	}

	default void removeInstance(Serializable value, T... components) {
		T t = getInstance(value, components);
		if (t == null)
			rollbackAndThrowException(new NotFoundException(((DisplayService<T>) this).info()));
		((BindingService<T>) t).unplug();
	}

	default Stream<T> select() {
		return Stream.of((T) this);
	}

	default Stream<T> concat(Stream<T> stream, Function<T, Stream<T>> mappers) {
		return stream.<Stream<T>> map(mappers).flatMap(x -> x);
	}

	default Stream<T> getAllInheritings() {
		return Stream.concat(select(), concat(getInheritings().stream(), inheriting -> ((BindingService<T>) inheriting).getAllInheritings()).distinct());
	}

	default Stream<T> getAllInstances() {
		return getAllInheritings().map(inheriting -> ((DependenciesService<T>) inheriting).getInstances().stream()).flatMap(x -> x);// .reduce(Stream.empty(), Stream::concat);
	}

	default Stream<T> selectInstances(Predicate<T> valuePredicate) {
		return getAllInstances().filter(valuePredicate);
	}

	default Stream<T> selectInstances(Serializable value) {
		return selectInstances(instance -> Objects.equals(value, instance.getValue()));
	}

	default Stream<T> selectInstances(Serializable value, T[] components) {
		return selectInstances(value, instance -> componentsDepends(components, ((InheritanceService<T>) instance).getComponents()));
	}

	default Stream<T> selectInstances(Serializable value, Predicate<T> componentsPredicate) {
		return selectInstances(value).filter(componentsPredicate);
	}

	default Stream<T> selectInstances(Predicate<T> valuePredicate, T... components) {
		return selectInstances(valuePredicate, instance -> componentsDepends(components, ((InheritanceService<T>) instance).getComponents()));
	}

	default Stream<T> selectInstances(Predicate<T> valuePredicate, Predicate<T> componentsPredicate) {
		return selectInstances(valuePredicate).filter(componentsPredicate);
	}

	default Stream<T> selectInstances(Predicate<T> supersPredicate, Serializable value, T... components) {
		return selectInstances(value, components).filter(supersPredicate);
	}

	default Stream<T> selectInstances(Predicate<T> supersPredicate, Serializable value, Predicate<T> componentsPredicate) {
		return selectInstances(value, componentsPredicate).filter(supersPredicate);
	}

	default Stream<T> selectInstances(Predicate<T> supersPredicate, Predicate<T> valuePredicate, T... components) {
		return selectInstances(valuePredicate, components).filter(supersPredicate);
	}

	default Stream<T> selectInstances(Predicate<T> supersPredicate, Predicate<T> valuePredicate, Predicate<T> componentsPredicate) {
		return selectInstances(valuePredicate, componentsPredicate).filter(supersPredicate);
	}

	default Stream<T> selectInstances(Stream<T> supers, Serializable value, Predicate<T> componentsPredicate) {
		return selectInstances(instance -> supers.allMatch(superT -> instance.inheritsFrom(superT)), value, componentsPredicate);
	}

	default Stream<T> selectInstances(Stream<T> supers, Predicate<T> valuePredicate, T... components) {
		return selectInstances((Predicate<T>) (instance -> supers.allMatch(superT -> instance.inheritsFrom(superT))), valuePredicate, components);
	}

	default Stream<T> selectInstances(Stream<T> supers, Predicate<T> valuePredicate, Predicate<T> componentsPredicate) {
		return selectInstances((Predicate<T>) (instance -> supers.allMatch(superT -> instance.inheritsFrom(superT))), valuePredicate, componentsPredicate);
	}
	// static interface Filter<T> extends Predicate<T> {
	// public static final Predicate<?> NO_FILTER = x -> true;
	//
	// public static Predicate<Serializable> valueFilter(T T) {
	// return s -> Objects.equals(s, T.getValue());
	// }
	//
	// public static Predicate<T[]> componentsDependsFilter(T[] components) {
	// return InheritanceService.componentsDepends(subMeta, subComponents, superComponents);
	// }
	// }

	// default NavigableSet<T> getDependencies(final T[] toReplace, final boolean existException) {
	// Iterator<T> iterator = new AbstractFilterIterator<T>(new AbstractPreTreeIterator<T>((getMeta())) {
	// private static final long serialVersionUID = 3038922934693070661L;
	// {
	// next();
	// }
	//
	// @Override
	// public Iterator<T> children(T node) {
	// return !isAncestorOf(node) ? node.dependenciesIterator() : Collections.<T> emptyIterator();
	// }
	// }) {
	// @Override
	// public boolean isSelected() {
	// if (isAncestorOf((next)))
	// return true;
	// if (!existException && isExtention(next)) {
	// toReplace[0] = next;
	// return true;
	// }
	// return false;
	//
	// }
	// };
	//
	// OrderedDependencies dependencies = new OrderedDependencies();
	// while (iterator.hasNext())
	// dependencies.addDependencies(iterator.next());
	// return dependencies;
	// }
	//
	// default boolean isExtention(T candidate) {
	// if (isFactual() && candidate.getMeta().equals((getMeta()))) {
	// if (getMeta().isPropertyConstraintEnabled())
	// if (InheritanceService.componentsDepends(candidate.getMeta(), candidate.getComponents(), getComponents()))
	// return true;
	// for (int pos = 0; pos < candidate.getComponents().length; pos++)
	// if (getMeta().isSingularConstraintEnabled(pos) && !getMeta().isReferentialIntegrity(pos))
	// if (candidate.getComponents()[pos].equals(getComponents()[pos]))
	// if (!this.inheritsFrom(candidate))
	// return true;
	// }
	// return false;
	//
	// }
}
