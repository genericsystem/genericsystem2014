package org.genericsystem.impl;

import java.util.Objects;
import org.genericsystem.kernel.AbstractVertex;
import org.genericsystem.kernel.services.AncestorsService;

public abstract class AbstractGeneric<T extends AbstractGeneric<T>> extends AbstractVertex<T> implements AncestorsService<T> {

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
