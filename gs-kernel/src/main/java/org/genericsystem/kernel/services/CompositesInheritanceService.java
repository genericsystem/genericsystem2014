package org.genericsystem.kernel.services;

import java.io.Serializable;
import org.genericsystem.kernel.Snapshot;

public interface CompositesInheritanceService<T extends VertexService<T, U>, U extends RootService<T, U>> extends ApiService<T, U> {

	@Override
	default Snapshot<T> getAttributes(T attribute) {
		return getInheritings(attribute, 1);
	}

	@Override
	default Snapshot<T> getAttributes() {
		return getInheritings(getMetaAttribute(), 1);
	}

	@Override
	default Snapshot<T> getHolders(T attribute) {
		return getInheritings(attribute, 2);
	}

	@Override
	default Snapshot<Serializable> getValues(T attribute) {
		return () -> getHolders(attribute).stream().map(T::getValue).iterator();
	}
}
