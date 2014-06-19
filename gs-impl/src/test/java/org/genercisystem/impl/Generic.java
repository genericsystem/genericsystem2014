package org.genercisystem.impl;

import org.genericsystem.impl.GenericService;
import org.genericsystem.impl.GenericSignature;

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
