package org.genericsystem.kernel.services;

import java.io.Serializable;
import org.genericsystem.kernel.Snapshot;

public interface CompositesInheritanceService<T extends CompositesInheritanceService<T>> extends AncestorsService<T> {

	default Snapshot<T> getAttributes(T attribute) {
		return getInheritings(attribute, 1);
	}

	default Snapshot<T> getHolders(T attribute) {
		return getInheritings(attribute, 2);
	}

	default Snapshot<Serializable> getValues(T attribute) {
		return getHolders(attribute).project(T::getValue);
	}

	Snapshot<T> getInheritings(final T origin, final int level);

}
