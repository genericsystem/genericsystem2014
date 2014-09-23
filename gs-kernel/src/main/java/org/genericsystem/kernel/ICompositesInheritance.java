package org.genericsystem.kernel;

import java.io.Serializable;
import org.genericsystem.api.core.IVertexBase;
import org.genericsystem.api.core.Snapshot;

public interface ICompositesInheritance<T extends AbstractVertex<T, U>, U extends IRoot<T, U>> extends IVertexBase<T, U> {

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getAttributes(T attribute) {
		return ((T) this).getInheritings(attribute, 1);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getAttributes() {
		return ((T) this).getInheritings(getRoot().getMetaAttribute(), 1);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getHolders(T attribute) {
		return ((T) this).getInheritings(attribute, 2);
	}

	@Override
	default Snapshot<Serializable> getValues(T attribute) {
		return () -> getHolders(attribute).stream().map(T::getValue).iterator();
	}
}
