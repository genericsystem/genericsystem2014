package org.genericsystem.mutability;

import java.io.Serializable;
import java.util.List;

import org.genericsystem.api.core.ISignature;
import org.genericsystem.api.core.Snapshot;

public class Generic implements ISignature<Generic> {
	protected Cache cache;

	Snapshot<Generic> getInstances() {
		return null;
	}

	Snapshot<Generic> getInheritings() {
		return null;
	}

	Snapshot<Generic> getComposites() {
		return null;
	}

	boolean isAlive() {
		return false;
	}

	Generic addInstance(Generic override, Serializable value, Generic... components) {
		return null;
	}

	Generic addInstance(Serializable value, Generic... components) {
		return null;
	}

	public Generic updateValue(Serializable newValue) {
		return null;
	}

	public Generic update(Generic override, Serializable newValue, Generic... components) {
		return null;
	}

	@Override
	public Generic getMeta() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Generic> getSupers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable getValue() {
		return cache.get(this).getValue();
	}

	@Override
	public List<Generic> getComponents() {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove() {

	}
}
