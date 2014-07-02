package org.genericsystem.impl;

import org.genericsystem.api.annotation.System;
import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Engine;

@System
public class EngineImpl extends GenericImpl implements Engine {

	private static final long serialVersionUID = 1247189250885720995L;

	private EngineService<? extends EngineService<?, ?>, ?> engineService;

	@Override
	public Engine newEngine(String directoryPath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Engine newInMemoryEngine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Engine setDirectoryPath(String directoryPath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public Cache mountNewCache() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cache getCurrentCache() {
		// TODO Auto-generated method stub
		return null;
	}

}
