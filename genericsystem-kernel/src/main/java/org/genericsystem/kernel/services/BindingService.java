package org.genericsystem.kernel.services;

import java.io.Serializable;
import java.util.Arrays;
import org.genericsystem.kernel.Statics;
import org.genericsystem.kernel.Vertex;

public interface BindingService extends AncestorsService, FactoryService {

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

	// default Snapshot<Vertex> getInstances(Vertex[] supers, Serializable value, Vertex... components) {
	// return new AbstractSnapshot<Vertex>() {
	// @Override
	// public Iterator<Vertex> iterator() {
	// return null;// TODO
	// }
	// };
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
