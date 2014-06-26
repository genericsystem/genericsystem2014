package org.genericsystem.impl;

import java.io.Serializable;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;

public class CacheImpl implements Cache {

	@Override
	public Cache start() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public Engine getEngine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Generic getGeneric(Serializable value, Generic meta, Generic... components) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAlive(Generic generic) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRemovable(Generic generic) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Cache mountNewCache() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cache flushAndUnmount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cache discardAndUnmount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

}
