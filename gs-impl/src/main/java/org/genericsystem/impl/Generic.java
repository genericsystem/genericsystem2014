package org.genericsystem.impl;

public class Generic extends GenericSignature<Generic> implements GenericService<Generic> {

	@Override
	public Generic build() {
		return new Generic();
	}

}
