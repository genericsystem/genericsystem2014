package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;

public interface BindingService extends AncestorsService<Vertex>, DependenciesService, FactoryService, InheritanceService {

	default Vertex addInstance(Serializable value, Vertex... components) {
		return addInstance(Statics.EMPTY_VERTICES, value, components);
	}

	default Vertex addInstance(Vertex[] overrides, Serializable value, Vertex... components) {
		return getFactory().buildVertex((Vertex) this, overrides, value, components).plug(true);
	}

	default Vertex setInstance(Serializable value, Vertex... components) {
		return setInstance(Statics.EMPTY_VERTICES, value, components);
	}

	default Vertex setInstance(Vertex[] overrides, Serializable value, Vertex... components) {
		return getFactory().buildVertex((Vertex) this, overrides, value, components).plug(false);
	}

	default Vertex getInstance(Serializable value, Vertex... components) {
		return getFactory().buildVertex((Vertex) this, Statics.EMPTY_VERTICES, value, components).getPlugged();
	}

	default Vertex getInstance(Vertex[] supers, Serializable value, Vertex... components) {
		Vertex result = getInstance(value, components);
		if (result != null && Arrays.stream(supers).allMatch(superVertex -> result.inheritsFrom(result)))
			return result;
		return null;
	}

	default Stream<Vertex> select() {
		return Stream.of((Vertex) this);
	}

	default Stream<Vertex> getAllInheritings() {
		return Stream.concat(select(), Statics.concat(getInheritings().stream(), inheriting -> inheriting.getAllInheritings()).distinct());
	}

	default Stream<Vertex> selectInstances() {
		return getAllInheritings().map(inheriting -> inheriting.getInstances().stream()).reduce(Stream.empty(), Stream::concat);
	}

	default Stream<Vertex> selectInstances(Predicate<Vertex> valuePredicate) {
		return selectInstances().filter(valuePredicate);
	}

	default Stream<Vertex> selectInstances(Serializable value) {
		return selectInstances(instance -> Objects.equals(value, instance.getValue()));
	}

	default Stream<Vertex> selectInstances(Serializable value, Vertex[] components) {
		return selectInstances(value, instance -> componentsDepends(components, instance.getComponents()));
	}

	default Stream<Vertex> selectInstances(Serializable value, Predicate<Vertex> componentsPredicate) {
		return selectInstances(value).filter(componentsPredicate);
	}

	default Stream<Vertex> selectInstances(Predicate<Vertex> valuePredicate, Vertex... components) {
		return selectInstances(valuePredicate, instance -> componentsDepends(components, instance.getComponents()));
	}

	default Stream<Vertex> selectInstances(Predicate<Vertex> valuePredicate, Predicate<Vertex> componentsPredicate) {
		return selectInstances(valuePredicate).filter(componentsPredicate);
	}

	default Stream<Vertex> selectInstances(Predicate<Vertex> supersPredicate, Serializable value, Vertex... components) {
		return selectInstances(value, components).filter(supersPredicate);
	}

	default Stream<Vertex> selectInstances(Predicate<Vertex> supersPredicate, Serializable value, Predicate<Vertex> componentsPredicate) {
		return selectInstances(value, componentsPredicate).filter(supersPredicate);
	}

	default Stream<Vertex> selectInstances(Predicate<Vertex> supersPredicate, Predicate<Vertex> valuePredicate, Vertex... components) {
		return selectInstances(valuePredicate, components).filter(supersPredicate);
	}

	default Stream<Vertex> selectInstances(Predicate<Vertex> supersPredicate, Predicate<Vertex> valuePredicate, Predicate<Vertex> componentsPredicate) {
		return selectInstances(valuePredicate, componentsPredicate).filter(supersPredicate);
	}

	default Stream<Vertex> selectInstances(Stream<Vertex> supers, Serializable value, Predicate<Vertex> componentsPredicate) {
		return selectInstances(instance -> supers.allMatch(superVertex -> instance.inheritsFrom(superVertex)), value, componentsPredicate);
	}

	default Stream<Vertex> selectInstances(Stream<Vertex> supers, Predicate<Vertex> valuePredicate, Vertex... components) {
		return selectInstances((Predicate<Vertex>) (instance -> supers.allMatch(superVertex -> instance.inheritsFrom(superVertex))), valuePredicate, components);
	}

	default Stream<Vertex> selectInstances(Stream<Vertex> supers, Predicate<Vertex> valuePredicate, Predicate<Vertex> componentsPredicate) {
		return selectInstances((Predicate<Vertex>) (instance -> supers.allMatch(superVertex -> instance.inheritsFrom(superVertex))), valuePredicate, componentsPredicate);
	}
	// static interface Filter<T> extends Predicate<T> {
	// public static final Predicate<?> NO_FILTER = x -> true;
	//
	// public static Predicate<Serializable> valueFilter(Vertex vertex) {
	// return s -> Objects.equals(s, vertex.getValue());
	// }
	//
	// public static Predicate<Vertex[]> componentsDependsFilter(Vertex[] components) {
	// return InheritanceService.componentsDepends(subMeta, subComponents, superComponents);
	// }
	// }

	// default NavigableSet<Vertex> getDependencies(final Vertex[] toReplace, final boolean existException) {
	// Iterator<Vertex> iterator = new AbstractFilterIterator<Vertex>(new AbstractPreTreeIterator<Vertex>((getMeta())) {
	// private static final long serialVersionUID = 3038922934693070661L;
	// {
	// next();
	// }
	//
	// @Override
	// public Iterator<Vertex> children(Vertex node) {
	// return !isAncestorOf(node) ? node.dependenciesIterator() : Collections.<Vertex> emptyIterator();
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
	// default boolean isExtention(Vertex candidate) {
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
