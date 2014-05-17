package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.exceptions.ExistsException;
import org.genericsystem.kernel.exceptions.NotFoundException;

public interface BindingService<T extends BindingService<T>> extends AncestorsService<T>, DependenciesService<T>, FactoryService<T>, InheritanceService<T>, ExceptionAdviserService<T>, DisplayService<T> {

	T[] getEmptyArray();

	@SuppressWarnings("unchecked")
	default T addInstance(Serializable value, T... components) {
		return addInstance(getEmptyArray(), value, components);
	}

	@SuppressWarnings("unchecked")
	default T addInstance(T[] overrides, Serializable value, T... components) {
		T instance = getInstance(overrides, value, components);
		if (instance != null)
			rollbackAndThrowException(new ExistsException(instance.info()));

		return build().initFromOverrides((T) this, Arrays.stream(overrides), value, Arrays.stream(components)).plug();
	}

	default T setInstance(Serializable value, @SuppressWarnings("unchecked") T... components) {
		return setInstance(getEmptyArray(), value, components);
	}

	@SuppressWarnings("unchecked")
	default T setInstance(T[] overrides, Serializable value, T... components) {
		T instance = getInstance(overrides, value, components);
		if (instance != null)
			return instance;
		return build().initFromOverrides((T) this, Arrays.stream(overrides), value, Arrays.stream(components)).plug();
	}

	@SuppressWarnings("unchecked")
	T getInstance(Serializable value, T... components);

	@Override
	public Dependencies<T> getInstances();

	@Override
	public Dependencies<T> getInheritings();

	@SuppressWarnings("unchecked")
	default T plug() {
		T t = getMeta().getInstances().set((T) this);
		getSupersStream().forEach(superGeneric -> superGeneric.getInheritings().set((T) this));
		getComponentsStream().forEach(component -> component.getMetaComposites().setByIndex(getMeta(), (T) this));
		getSupersStream().forEach(superGeneric -> getComponentsStream().forEach(component -> component.getSuperComposites().setByIndex(superGeneric, (T) this)));

		// assert getSupersStream().allMatch(superGeneric -> this == superGeneric.getInheritings().get((T) this));
		// assert Arrays.stream(getComponents()).allMatch(component -> this == component.getMetaComposites(getMeta()).get((T) this));
		// assert getSupersStream().allMatch(superGeneric -> Arrays.stream(getComponents()).allMatch(component -> component == component.getSuperComposites(superGeneric).get((T) this)));

		return t;
	}

	@SuppressWarnings("unchecked")
	default boolean unplug() {
		boolean result = getMeta().getInstances().remove((T) this);
		if (!result)
			rollbackAndThrowException(new NotFoundException(((DisplayService<T>) this).info()));
		getSupersStream().forEach(superGeneric -> superGeneric.getInheritings().remove((T) this));
		getComponentsStream().forEach(component -> component.getMetaComposites().removeByIndex(getMeta(), (T) this));
		getSupersStream().forEach(superGeneric -> getComponentsStream().forEach(component -> component.getSuperComposites().removeByIndex(superGeneric, (T) this)));
		return result;
	}

	CompositesDependencies<T> getMetaComposites();

	CompositesDependencies<T> getSuperComposites();

	@SuppressWarnings("unchecked")
	default T getInstance(T[] supers, Serializable value, T... components) {
		T result = getInstance(value, components);
		if (result != null && Arrays.stream(supers).allMatch(superT -> result.inheritsFrom(superT)))
			return result;
		return null;
	}

	@SuppressWarnings("unchecked")
	default void removeInstance(Serializable value, T... components) {
		T t = getInstance(value, components);
		if (t == null)
			rollbackAndThrowException(new NotFoundException(((DisplayService<T>) this).info()));
		((BindingService<T>) t).unplug();
	}

	@SuppressWarnings("unchecked")
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

	@SuppressWarnings("unchecked")
	default Stream<T> selectInstances(Predicate<T> valuePredicate, T... components) {
		return selectInstances(valuePredicate, instance -> componentsDepends(components, ((InheritanceService<T>) instance).getComponents()));
	}

	default Stream<T> selectInstances(Predicate<T> valuePredicate, Predicate<T> componentsPredicate) {
		return selectInstances(valuePredicate).filter(componentsPredicate);
	}

	@SuppressWarnings("unchecked")
	default Stream<T> selectInstances(Predicate<T> supersPredicate, Serializable value, T... components) {
		return selectInstances(value, components).filter(supersPredicate);
	}

	default Stream<T> selectInstances(Predicate<T> supersPredicate, Serializable value, Predicate<T> componentsPredicate) {
		return selectInstances(value, componentsPredicate).filter(supersPredicate);
	}

	@SuppressWarnings("unchecked")
	default Stream<T> selectInstances(Predicate<T> supersPredicate, Predicate<T> valuePredicate, T... components) {
		return selectInstances(valuePredicate, components).filter(supersPredicate);
	}

	default Stream<T> selectInstances(Predicate<T> supersPredicate, Predicate<T> valuePredicate, Predicate<T> componentsPredicate) {
		return selectInstances(valuePredicate, componentsPredicate).filter(supersPredicate);
	}

	default Stream<T> selectInstances(Stream<T> supers, Serializable value, Predicate<T> componentsPredicate) {
		return selectInstances(instance -> supers.allMatch(superT -> instance.inheritsFrom(superT)), value, componentsPredicate);
	}

	@SuppressWarnings("unchecked")
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
