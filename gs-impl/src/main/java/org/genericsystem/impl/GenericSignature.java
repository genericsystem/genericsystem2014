package org.genericsystem.impl;

import java.util.Objects;
import org.genericsystem.kernel.Signature;
import org.genericsystem.kernel.services.AncestorsService;

public abstract class GenericSignature<T extends GenericSignature<T>> extends Signature<T> implements AncestorsService<T> {

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof GenericSignature))
			return false;
		GenericSignature<?> service = (GenericSignature<?>) obj;
		return equiv(service);
	}

	@Override
	public int hashCode() {
		// TODO introduce : meta and components length
		return Objects.hashCode(getValue());
	}

}
