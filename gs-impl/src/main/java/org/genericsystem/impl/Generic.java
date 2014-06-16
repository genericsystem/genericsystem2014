package org.genericsystem.impl;


public class Generic extends GenericSignature<Generic> implements GenericService<Generic> {

	@Override
	public Generic buildInstance() {
		return new Generic();
	}

	@Override
	public Generic getMap() {
		return getRoot().getInstance(Map.class, getRoot());
	}

}
