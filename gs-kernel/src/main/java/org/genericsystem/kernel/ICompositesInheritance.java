package org.genericsystem.kernel;

import java.io.Serializable;
import org.genericsystem.kernel.services.IVertexBase;

public interface ICompositesInheritance<T extends IVertex<T, U>, U extends IRoot<T, U>> extends IVertexBase<T, U> {

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
