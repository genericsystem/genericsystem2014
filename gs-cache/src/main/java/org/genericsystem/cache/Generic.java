package org.genericsystem.cache;

import org.genericsystem.impl.GenericSignature;

public class Generic extends GenericSignature<Generic> implements GenericService<Generic> {

	@Override
	public Generic build() {
		return new Generic();
	}
}
