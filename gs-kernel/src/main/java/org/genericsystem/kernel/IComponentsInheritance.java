package org.genericsystem.kernel;

import java.io.Serializable;

import org.genericsystem.api.core.IVertexBase;
import org.genericsystem.api.core.Snapshot;

public interface IComponentsInheritance<T extends AbstractVertex<T, U>, U extends IRoot<T, U>> extends IVertexBase<T, U> {

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
		return () -> getAttributes().stream().filter(attribute -> pos >= 0 && pos < attribute.getComposites().size() && ((T) this).isSpecializationOf(attribute.getComposites().get(pos))).iterator();
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getHolders(T attribute) {
		return ((T) this).getInheritings(attribute, Statics.CONCRETE);
	}

	@SuppressWarnings("unchecked")
	@Override
	default Snapshot<T> getHolders(T attribute, int pos) {
		return () -> getHolders(attribute).stream().filter(holder -> pos >= 0 && pos < holder.getComposites().size() && ((T) this).isSpecializationOf(holder.getComposites().get(pos))).iterator();

	}

	@Override
	default Snapshot<Serializable> getValues(T attribute) {
		return () -> getHolders(attribute).stream().map(T::getValue).iterator();
	}

	@Override
	default Snapshot<Serializable> getValues(T attribute, int pos) {
		return () -> getHolders(attribute, pos).stream().map(T::getValue).iterator();
	}
}
