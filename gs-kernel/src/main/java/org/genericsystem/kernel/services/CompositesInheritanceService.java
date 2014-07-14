package org.genericsystem.kernel.services;

import java.io.Serializable;
import org.genericsystem.kernel.Snapshot;
import org.genericsystem.kernel.VertexService;

public interface CompositesInheritanceService<T extends VertexService<T>> extends ApiService<T> {

	@Override
	default Snapshot<T> getMetaAttributes(T attribute) {
		return getInheritings(attribute, 0);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getRelations() {
		return getRelations((T) getRoot());// TODO KK getMetaAttribute() would be faster ???
	}

	@Override
	default Snapshot<T> getRelations(T origin) {
		return getAttributes(origin).filter(attribute -> attribute.getComponents().size() > 1);
	}

	@Override
	default Snapshot<T> getAttributes(T attribute) {
		return getInheritings(attribute, 1);
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
