package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.genericsystem.kernel.Dependencies;
import org.genericsystem.kernel.Dependencies.CompositesDependencies;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.exceptions.ExistsException;
import org.genericsystem.kernel.exceptions.NotFoundException;

public interface BindingService<T extends BindingService<T>> extends AncestorsService<T>, DependenciesService<T>, FactoryService<T>, CompositesInheritanceService<T>, InheritanceService<T>, ExceptionAdviserService<T>, DisplayService<T> {

	@SuppressWarnings("unchecked")
	default T addInstance(Serializable value, T... components) {
		return addInstance(Collections.emptyList(), value, components);
	}

	@SuppressWarnings("unchecked")
	default T addInstance(T superGeneric, Serializable value, T... components) {
		return addInstance(Collections.singletonList(superGeneric), value, components);
	}

	@SuppressWarnings("unchecked")
	default T addInstance(List<T> overrides, Serializable value, T... components) {
		T instance = getInstance(overrides, value, components);
		if (instance != null)
			rollbackAndThrowException(new ExistsException(instance.info()));

		return buildInstance(overrides, value, Arrays.asList(components)).plug();
	}

	default T setInstance(Serializable value, @SuppressWarnings("unchecked") T... components) {
		return setInstance(Collections.emptyList(), value, components);
	}

	default T setInstance(T superGeneric, Serializable value, @SuppressWarnings("unchecked") T... components) {
		return setInstance(Collections.singletonList(superGeneric), value, components);
	}

	@SuppressWarnings("unchecked")
	default T setInstance(List<T> overrides, Serializable value, T... components) {
		T instance = getInstance(overrides, value, components);
		if (instance != null)
			return instance;
		return buildInstance(overrides, value, Arrays.asList(components)).plug();
	}

	@SuppressWarnings("unchecked")
	T getInstance(Serializable value, T... components);

	@Override
	public Dependencies<T> getInstances();

	@Override
	public Dependencies<T> getInheritings();

	CompositesDependencies<T> getMetaComposites();

	CompositesDependencies<T> getSuperComposites();

	@Override
	default Snapshot<T> getMetaComposites(T meta) {
		return getMetaComposites().getByIndex(meta);
	}

	@Override
	default Snapshot<T> getSuperComposites(T superVertex) {
		return getSuperComposites().getByIndex(superVertex);
	}

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

	@SuppressWarnings("unchecked")
	default T getInstance(List<T> supers, Serializable value, T... components) {
		T result = getInstance(value, components);
		if (result != null && supers.stream().allMatch(superT -> result.inheritsFrom(superT)))
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
		return selectInstances(value, instance -> componentsDepends(Arrays.asList(components), ((InheritanceService<T>) instance).getComponents()));
	}

	default Stream<T> selectInstances(Serializable value, Predicate<T> componentsPredicate) {
		return selectInstances(value).filter(componentsPredicate);
	}

	@SuppressWarnings("unchecked")
	default Stream<T> selectInstances(Predicate<T> valuePredicate, T... components) {
		return selectInstances(valuePredicate, instance -> componentsDepends(Arrays.asList(components), ((InheritanceService<T>) instance).getComponents()));
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

	default Snapshot<T> getComposites() {
		return () -> Statics.concat(getMetaComposites().stream(), entry -> entry.getValue().stream()).iterator();
	}

	default boolean isAncestorOf(final T dependency) {
		return equiv(dependency) || (!dependency.equals(dependency.getMeta()) && isAncestorOf(dependency.getMeta())) || dependency.getSupersStream().anyMatch(component -> this.isAncestorOf(component))
				|| dependency.getComponentsStream().filter(component -> !dependency.equals(component)).anyMatch(component -> this.isAncestorOf(component))
				|| inheritsFrom(dependency.getMeta(), dependency.getValue(), dependency.getComponents(), getMeta(), getValue(), getComponents());
	}

	default LinkedHashSet<T> computeAllDependencies() {
		class DirectDependencies extends LinkedHashSet<T> {
			private static final long serialVersionUID = -5970021419012502402L;
			private Set<T> alreadyVisited = new HashSet<>();

			public DirectDependencies() {
				visit(getMeta());
			}

			public void visit(T node) {
				if (!alreadyVisited.contains(node))
					if (!isAncestorOf(node)) {
						alreadyVisited.add(node);
						node.getInheritings().forEach(this::visit);
						node.getInstances().forEach(this::visit);
						node.getComposites().forEach(this::visit);
					} else
						add(node);
			}

			@Override
			public boolean add(T node) {
				if (!alreadyVisited.contains(node)) {
					super.add(node);
					alreadyVisited.add(node);
					node.getInheritings().forEach(this::add);
					node.getInstances().forEach(this::add);
					node.getComposites().forEach(this::add);
				}
				return true;
			}
		}
		return new DirectDependencies();
	}

	// default boolean isExtention(T candidate) {
	// if (isFactual() && candidate.getMeta().equals((getMeta()))) {
	// if (getMeta().isPropertyConstraintEnabled())
	// if (InheritanceService.componentsDepends(candidate.getMeta(), candidate.getComponents(), getComponents()))
	// return true;
	// for (int pos = 0; pos < candidate.getComponents().size(); pos++)
	// if (getMeta().isSingularConstraintEnabled(pos) && !getMeta().isReferentialIntegrity(pos))
	// if (candidate.getComponents().get(pos).equals(getComponents().get(pos)))
	// if (!this.inheritsFrom(candidate))
	// return true;
	// }
	// return false;
	//
	// }
}
