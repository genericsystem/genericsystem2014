package org.genericsystem.kernel;

import java.io.Serializable;

import org.genericsystem.api.core.IVertex;
import org.genericsystem.api.core.IteratorSnapshot;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.kernel.iterator.AbstractProjectionIterator;

public interface DefaultCompositesInheritance<T extends DefaultVertex<T>> extends IVertex<T> {

	@SuppressWarnings("unchecked")
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
	default Snapshot<T> getHolders(T attribute, int pos) {
		return () -> getHolders(attribute).get().filter(holder -> holder.getComponent(pos) != null && ((T) this).isSpecializationOf(holder.getComponent(pos)));

	}

	@Override
	default Snapshot<Serializable> getValues(T attribute) {
		return (IteratorSnapshot<Serializable>) (() -> new AbstractProjectionIterator<T, Serializable>(getHolders(attribute).get().iterator()) {
			@Override
			public Serializable project(T generic) {
				return generic.getValue();
			}
		});
	}

	@Override
	default Snapshot<Serializable> getValues(T attribute, int pos) {
		return () -> getHolders(attribute, pos).get().map(T::getValue);
	}
}
