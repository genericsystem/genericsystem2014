package org.genericsystem.cache;

import java.io.Serializable;
import java.util.List;

import org.genericsystem.impl.GenericSignature;

public class Generic extends GenericSignature<Generic> implements GenericService<Generic> {

	@Override
	public Generic buildInstanceFromSupers(List<Generic> overrides, Serializable value, List<Generic> components) {
		return buildInstance().initFromSupers(this, overrides, value, components);
	}

	@Override
	public Generic buildInstance() {
		return new Generic();
	}
}
