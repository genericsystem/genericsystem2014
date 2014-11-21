package org.genericsystem.mutability;

import java.io.Serializable;
import java.util.List;

import org.genericsystem.api.core.IRoot;

public class Engine extends Generic implements IRoot<Generic> {
	public final static org.genericsystem.concurrency.Engine engineT = new org.genericsystem.concurrency.Engine();

	private Cache cache;

	public Engine() {
		super(null);
		engine = this;
		cache = Cache.getCache();
		cache.put(this, engineT);
	}

	public Cache getCache() {
		return cache;
	}

	@Override
	public Generic addType(Serializable value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Generic addType(Generic override, Serializable value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Generic addType(List<Generic> overrides, Serializable value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Generic setType(Serializable value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Generic setType(Generic override, Serializable value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Generic setType(List<Generic> overrides, Serializable value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Generic addTree(Serializable value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Generic addTree(Serializable value, int parentsNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Generic setTree(Serializable value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Generic setTree(Serializable value, int parentsNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Generic getMetaAttribute() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Generic getMetaRelation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void discardWithException(Throwable exception) {
		// TODO Auto-generated method stub

	}

}
