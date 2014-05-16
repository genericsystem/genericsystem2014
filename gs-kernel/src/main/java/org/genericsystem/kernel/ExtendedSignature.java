package org.genericsystem.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Stream;

public abstract class ExtendedSignature<T> extends Signature<T> {

	private T[] supers;

	@SuppressWarnings("unchecked")
	protected T initFromOverrides(T meta, T[] overrides, Serializable value, T... components) {
		super.init(meta, value, components);
		this.supers = computeSupers(overrides);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	protected T initFromSupers(T meta, T[] supers, Serializable value, T... components) {
		super.init(meta, value, components);
		this.supers = supers;
		return (T) this;
	}

	public abstract T[] computeSupers(T[] overrides);

	public Stream<T> getSupersStream() {
		return Arrays.stream(supers);
	}
}
