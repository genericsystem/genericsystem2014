package org.genericsystem.impl;

import java.io.Serializable;
import java.util.List;

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
