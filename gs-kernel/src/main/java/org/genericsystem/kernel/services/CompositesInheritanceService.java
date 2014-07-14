package org.genericsystem.kernel.services;

import java.io.Serializable;
import org.genericsystem.kernel.Snapshot;

public interface CompositesInheritanceService<T extends CompositesInheritanceService<T>> extends BindingService<T> {

	Snapshot<T> getInheritings(final T origin, final int level);

	Snapshot<T> getMetaComposites(T meta);

	Snapshot<T> getSuperComposites(T superVertex);

	default Snapshot<T> getMetaAttributes(T attribute) {
		return getInheritings(attribute, 0);
	}

	default Snapshot<T> getRelations() {
		return getRelations(getRoot());
	}

	default Snapshot<T> getRelations(T origin) {
		return getAttributes(origin).filter(attribute -> attribute.getComponents().size() > 1);
	}

	default Snapshot<T> getAttributes(T attribute) {
		return getInheritings(attribute, 1);
	}

	default Snapshot<T> getHolders(T attribute) {
		return getInheritings(attribute, 2);
	}

	default Snapshot<Serializable> getValues(T attribute) {
		return () -> getHolders(attribute).stream().map(T::getValue).iterator();
	}
}
