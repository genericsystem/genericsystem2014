package org.genericsystem.impl;

import java.io.Serializable;
import java.util.Objects;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.services.AncestorsService;

public abstract class AbstractGeneric<T extends AbstractGeneric<T>> extends AbstractVertex<T> implements AncestorsService<T> {

	public AbstractGeneric(T meta, T[] supers, Serializable value, T[] components) {
		super(meta, supers, value, components);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Generic))
			return false;
		Generic service = (Generic) obj;
		return equiv(service);
	}

	@Override
	public int hashCode() {
		// TODO introduce : meta and components length
		return Objects.hashCode(getValue());
	}

}
