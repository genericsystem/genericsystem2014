package org.genericsystem.impl;

import java.util.Objects;

import org.genericsystem.kernel.ExtendedSignature;
import org.genericsystem.kernel.services.AncestorsService;

public abstract class GenericSignature<T extends ExtendedSignature<T>> extends ExtendedSignature<T> {

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof AncestorsService))
			return false;
		AncestorsService<?> service = (AncestorsService<?>) obj;
		return equiv(service);
	}

	@Override
	public int hashCode() {
		// TODO introduce : meta and components length
		return Objects.hashCode(getValue());
	}

}
