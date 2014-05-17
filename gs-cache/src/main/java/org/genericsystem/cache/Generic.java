package org.genericsystem.cache;

import java.io.Serializable;
import java.util.stream.Stream;

import org.genericsystem.impl.GenericSignature;

public class Generic extends GenericSignature<Generic> implements GenericService<Generic> {

	private final static Generic[] EMPTY_ARRAY = new Generic[] {};

	@Override
	public Generic build() {
		return new Generic();// .init(meta, overrides.toArray(Generic[]::new), value, components.toArray(Generic[]::new));
	}

	@Override
	public Generic initFromOverrides(Generic meta, Stream<Generic> overrides, Serializable value, Stream<Generic> components) {
		return super.initFromOverrides(meta, overrides.toArray(Generic[]::new), value, components.toArray(Generic[]::new));
	}

	@Override
	public Generic initFromSupers(Generic meta, Stream<Generic> overrides, Serializable value, Stream<Generic> components) {
		return super.initFromSupers(meta, overrides.toArray(Generic[]::new), value, components.toArray(Generic[]::new));
	}

	@Override
	public Generic[] getEmptyArray() {
		return EMPTY_ARRAY;
	}

	public Generic[] computeSupers(Generic[] overrides) {
		return computeSupersStream(overrides).toArray(Generic[]::new);
	}
}
