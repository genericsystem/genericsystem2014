package org.genericsystem.kernel;

import java.io.Serializable;

import org.genericsystem.api.core.IVertex;
import org.genericsystem.api.core.Snapshot;

public interface DefaultCompositesInheritance<T extends AbstractVertex<T>> extends IVertex<T> {

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getAttributes(T attribute) {
		return ((T) this).getInheritings(attribute, Statics.STRUCTURAL);
	}

	@Override
	default Snapshot<T> getAttributes() {
		return getAttributes(getRoot().getMetaAttribute());
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getAttributes(int pos) {
		return () -> getAttributes().get().filter(attribute -> attribute.getComponent(pos) != null && ((T) this).isSpecializationOf(attribute.getComponent(pos)));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getHolders(T attribute) {
		return ((T) this).getInheritings(attribute, Statics.CONCRETE);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getHolders(T attribute, int pos) {
		return () -> getHolders(attribute).get().filter(holder -> holder.getComponent(pos) != null && ((T) this).isSpecializationOf(holder.getComponent(pos)));

	}

	@Override
	default Snapshot<T> getRelations() {
		return getRelations(getRoot().getMetaRelation());
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getRelations(int pos) {
		return () -> getRelations().get().filter(relation -> relation.getComponent(pos) != null && ((T) this).isSpecializationOf(relation.getComponent(pos)));
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getRelations(T relation) {
		return ((T) this).getInheritings(relation, Statics.STRUCTURAL);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getLinks(T relation) {
		return ((T) this).getInheritings(relation, Statics.CONCRETE);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getLinks(T relation, int pos) {
		return () -> getLinks(relation).get().filter(link -> link.getComponent(pos) != null && ((T) this).isSpecializationOf(link.getComponent(pos)));
	}

	@Override
	default Snapshot<Serializable> getValues(T attribute) {
		return () -> getHolders(attribute).get().map(T::getValue);
	}

	@Override
	default Snapshot<Serializable> getValues(T attribute, int pos) {
		return () -> getHolders(attribute, pos).get().map(T::getValue);
	}
}
