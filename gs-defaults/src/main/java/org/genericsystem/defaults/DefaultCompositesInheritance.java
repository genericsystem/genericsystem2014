package org.genericsystem.defaults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.genericsystem.api.core.ApiStatics;
import org.genericsystem.api.core.IVertex;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.core.systemproperty.NonHeritableProperty;

public interface DefaultCompositesInheritance<T extends DefaultVertex<T>> extends IVertex<T> {

	@SuppressWarnings("unchecked")
	@Override
	default T getAttribute(Serializable value, T... targets) {
		return getNonAmbiguousResult(getAttributes(value, targets).get());
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getAttribute(T... targets) {
		return getNonAmbiguousResult(getAttributes(targets).get());
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getAttributes(Serializable value, T... targets) {
		return getAttributes(targets).filter(DefaultDependencies.valueFilter(value));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getAttributes(T... targets) {
		return getAttributes(getRoot().getMetaAttribute()).filter(componentsFilter(addThisToTargets(targets)));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getAttributes(int pos) {
		return () -> getAttributes().get().filter(attribute -> attribute.getComponent(pos) != null && ((T) this).isSpecializationOf(attribute.getComponent(pos)));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getAttributes(T attribute) {
		T nonHeritableProperty = getKey(NonHeritableProperty.class, ApiStatics.NO_POSITION);
		if (nonHeritableProperty == null || attribute.inheritsFrom(nonHeritableProperty) || attribute.isHeritableEnabled())
			return () -> new InheritanceComputer<>((T) DefaultCompositesInheritance.this, attribute, ApiStatics.STRUCTURAL).inheritanceStream();
		return () -> this.getComposites().get().filter(holder -> holder.isSpecializationOf(attribute) && holder.getLevel() == ApiStatics.STRUCTURAL);
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getHolder(T attribute, Serializable value, T... targets) {
		return getNonAmbiguousResult(getHolders(attribute, value, targets).get());
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getHolder(T attribute, T... targets) {
		return getNonAmbiguousResult(getHolders(attribute, targets).get());
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getHolders(T attribute, Serializable value, T... targets) {
		return getHolders(attribute).filter(DefaultDependencies.valueFilter(value)).filter(componentsFilter(addThisToTargets(targets)));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getHolders(T attribute, T... targets) {
		return getHolders(attribute).filter(componentsFilter(addThisToTargets(targets)));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getHolders(T attribute, int pos) {
		return () -> getHolders(attribute).get().filter(holder -> holder.getComponent(pos) != null && ((T) this).isSpecializationOf(holder.getComponent(pos)));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getHolders(T attribute) {
		T nonHeritableProperty = getKey(NonHeritableProperty.class, ApiStatics.NO_POSITION);
		if (nonHeritableProperty == null || attribute.inheritsFrom(nonHeritableProperty) || attribute.isHeritableEnabled())
			return () -> new InheritanceComputer<>((T) DefaultCompositesInheritance.this, attribute, ApiStatics.CONCRETE).inheritanceStream();
		return () -> this.getComposites().get().filter(holder -> holder.isSpecializationOf(attribute) && holder.getLevel() == ApiStatics.CONCRETE);
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getRelation(Serializable value, T... targets) {
		return getNonAmbiguousResult(getRelations(value, targets).get());
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getRelation(T... targets) {
		return getNonAmbiguousResult(getRelations(targets).get());
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getRelations(Serializable value, T... targets) {
		return getRelations(targets).filter(DefaultDependencies.valueFilter(value));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getRelations(T... targets) {
		return getRelations(getRoot().getMetaRelation()).filter(componentsFilter(addThisToTargets(targets)));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getRelations(int pos) {
		return () -> getRelations().get().filter(relation -> relation.getComponent(pos) != null && ((T) this).isSpecializationOf(relation.getComponent(pos)));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getRelations(T relation) {
		return ((T) this).getAttributes(relation);
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getLink(T relation, Serializable value, T... targets) {
		return getNonAmbiguousResult(getLinks(relation, value, targets).get());
	}

	@SuppressWarnings("unchecked")
	@Override
	default T getLink(T relation, T... targets) {
		return getNonAmbiguousResult(getLinks(relation, targets).get());
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getLinks(T relation, Serializable value, T... targets) {
		return getLinks(relation).filter(DefaultDependencies.valueFilter(value)).filter(componentsFilter(addThisToTargets(targets)));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getLinks(T relation, T... targets) {
		return getLinks(relation).filter(componentsFilter(addThisToTargets(targets)));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getLinks(T relation, int pos) {
		return () -> getLinks(relation).get().filter(link -> link.getComponent(pos) != null && ((T) this).isSpecializationOf(link.getComponent(pos)));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getLinks(T relation) {
		return ((T) this).getHolders(relation);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Serializable getValue(T attribute, Serializable value, T... targets) {
		return getNonAmbiguousResult(getLinks(attribute, value, targets).get()).getValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	default Serializable getValue(T attribute, T... targets) {
		return getNonAmbiguousResult(getLinks(attribute, targets).get()).getValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<Serializable> getValues(T attribute, Serializable value, T... targets) {
		return () -> getLinks(attribute, value, targets).get().map(x -> x.getValue());
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<Serializable> getValues(T attribute, T... targets) {
		return () -> getLinks(attribute, targets).get().map(x -> x.getValue());
	}

	@Override
	default Snapshot<Serializable> getValues(T attribute, int pos) {
		return () -> getHolders(attribute, pos).get().map(x -> x.getValue());
	}

	@SuppressWarnings("unchecked")
	static <T extends DefaultVertex<T>> Predicate<T> componentsFilter(T... componentsReached) {
		return attribute -> {
			List<T> attributeComps = new ArrayList<>(attribute.getComponents());
			for (T componentReach : componentsReached) {
				T matchedComponent = attributeComps.stream().filter(attributeComp -> componentsReached[0].isSpecializationOf(attributeComp) ? true : componentReach.equals(attributeComp)).findFirst().orElse(null);
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
